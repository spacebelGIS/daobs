/*
 * Copyright 2014-2016 European Environment Agency
 *
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package org.fao.geonet.solr;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "search",
    tags = "search",
    description = "Search operations")
@Controller
public class SolrHttpProxy {
  public static final String[] _validContentTypes = {
    "application/json", "text/plain"
  };

  @Autowired
  private SolrConfig config;

  /**
   * Search.
   */
  @ApiOperation(value = "Search",
      notes = "See https://cwiki.apache.org/confluence/display/solr/Common+Query+Parameters "
        + "for parameters.")
  @RequestMapping(value = "/search",
      method = RequestMethod.GET)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void handleGetMetadata(
      HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    final String url = config.getSolrServerUrl() + "/"
        + config.getSolrServerCore() + "/select?" + request.getQueryString();
    handleRequest(request, response, url, true);
  }

  /**
   * Search in a collection.
     */
  @ApiOperation(value = "Search in a collection",
      notes = "See https://cwiki.apache.org/confluence/display/solr/Common+Query+Parameters "
          + "for parameters.")
  @RequestMapping(value = "/search/{collection}",
      method = RequestMethod.GET)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public void handleGetMetadata(
      @PathVariable String collection,
      HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    final String url = config.getSolrServerUrl() + "/"
        + collection + "/select?" + request.getQueryString();
    handleRequest(request, response, url, true);
  }

  private void handleRequest(HttpServletRequest request,
                             HttpServletResponse response,
                             String serverUrl,
                             boolean addPermissions) throws Exception {
    try {
      URL url = new URL(serverUrl);

      // open communication between proxy and final host
      // all actions before the connection can be taken now
      HttpURLConnection connectionWithFinalHost = (HttpURLConnection) url.openConnection();
      try {
        connectionWithFinalHost.setRequestMethod("GET");

        // copy headers from client's request to request that will be send to the final host
        copyHeadersToConnection(request, connectionWithFinalHost);

        // connect to remote host
        // interactions with the resource are enabled now
        connectionWithFinalHost.connect();

        int code = connectionWithFinalHost.getResponseCode();
        if (code != 200) {
          response.sendError(code,
              connectionWithFinalHost.getResponseMessage());
          return;
        }

        // get content type
        String contentType = connectionWithFinalHost.getContentType();
        if (contentType == null) {
          response.sendError(HttpServletResponse.SC_FORBIDDEN,
              "Host url has been validated by proxy but content type given by remote host is null");
          return;
        }

        // content type has to be valid
        if (!isContentTypeValid(contentType)) {
          if (connectionWithFinalHost.getResponseMessage() != null) {
            if (connectionWithFinalHost.getResponseMessage().equalsIgnoreCase("Not Found")) {
              // content type was not valid because it was a not found page (text/html)
              response.sendError(HttpServletResponse.SC_NOT_FOUND, "Remote host not found");
              return;
            }
          }

          response.sendError(HttpServletResponse.SC_FORBIDDEN,
              "The content type of the remote host's response \"" + contentType
                  + "\" is not allowed by the proxy rules");
          return;
        }

        // send remote host's response to client
        String contentEncoding = getContentEncoding(connectionWithFinalHost.getHeaderFields());

        // copy headers from the remote server's response to the response to send to the client
        copyHeadersFromConnectionToResponse(response, connectionWithFinalHost);

        if (!contentType.split(";")[0].equals("application/json")) {
          addPermissions = false;
        }

        final InputStream streamFromServer;
        final OutputStream streamToClient;
        if (contentEncoding == null || !addPermissions) {
          // A simple stream can do the job for data that is not in content encoded
          // but also for data content encoded with a known charset
          streamFromServer = connectionWithFinalHost.getInputStream();
          streamToClient = response.getOutputStream();
        } else if ("gzip".equalsIgnoreCase(contentEncoding)) {
          // the charset is unknown and the data are compressed in gzip
          // we add the gzip wrapper to be able to read/write the stream content
          streamFromServer = new GZIPInputStream(connectionWithFinalHost.getInputStream());
          streamToClient = new GZIPOutputStream(response.getOutputStream());
        } else if ("deflate".equalsIgnoreCase(contentEncoding)) {
          // same but with deflate
          streamFromServer = new DeflaterInputStream(connectionWithFinalHost.getInputStream());
          streamToClient = new DeflaterOutputStream(response.getOutputStream());
        } else {
          throw new UnsupportedOperationException("Please handle the stream when it is encoded in "
              + contentEncoding);
        }

        try {
          IOUtils.copy(connectionWithFinalHost.getInputStream(), response.getOutputStream());
        } finally {
          streamFromServer.close();
          streamToClient.close();
        }
      } finally {
        connectionWithFinalHost.disconnect();
      }
    } catch (IOException e1) {
      // connection problem with the host
      e1.printStackTrace();

      throw new Exception(
        String.format("Failed to request Solr at URL %s. "
            + "Check Solr configuration.",
          serverUrl),
        e1);
    }
  }



  @Value("${solr.server.url}")
  private String solrUrl;

  /**
   * Update using JSON command.
   */
  @RequestMapping(value = "/update/{collection}",
      method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public ResponseEntity<String> update(
      @PathVariable String collection,
      @RequestParam(defaultValue = "true") boolean commit,
      @RequestBody String body) throws Exception {

    HttpPost httpPost = new HttpPost(
        solrUrl + "/" + collection
        + "/update" + (commit ? "?commit=" + commit : "")
    );
    httpPost.setEntity(new StringEntity(body, Charset.forName("UTF-8")));
    httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
    CloseableHttpClient httpClient = HttpClients.createDefault();
    CloseableHttpResponse response = httpClient.execute(httpPost);
    if (response.getStatusLine().getStatusCode() == 200) {
      return new ResponseEntity<>("", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * Gets the encoding of the content sent by the remote host: extracts the content-encoding
   * header
   *
   * @param headerFields headers of the HttpURLConnection
   * @return null if not exists otherwise name of the encoding (gzip, deflate...)
   */
  private String getContentEncoding(Map<String, List<String>> headerFields) {
    for (String headerName : headerFields.keySet()) {
      if (headerName != null) {
        if ("Content-Encoding".equalsIgnoreCase(headerName)) {
          List<String> valuesList = headerFields.get(headerName);
          StringBuilder stringBuilder = new StringBuilder();
          valuesList.forEach(stringBuilder::append);
          return stringBuilder.toString().toLowerCase();
        }
      }
    }
    return null;
  }

  /**
   * Copy headers from the connection to the response.
   *
   * @param response   to copy headers in
   * @param uc         contains headers to copy
   * @param ignoreList list of headers that mustn't be copied
   */
  private void copyHeadersFromConnectionToResponse(HttpServletResponse response,
                                                   HttpURLConnection uc,
                                                   String... ignoreList) {
    Map<String, List<String>> map = uc.getHeaderFields();
    for (String headerName : map.keySet()) {

      if (!isInIgnoreList(headerName, ignoreList)) {

        // concatenate all values from the header
        List<String> valuesList = map.get(headerName);
        StringBuilder stringBuilder = new StringBuilder();
        valuesList.forEach(stringBuilder::append);

        // add header to HttpServletResponse object
        if (headerName != null && !"Content-Length".equalsIgnoreCase(headerName)) {
          if ("Transfer-Encoding".equalsIgnoreCase(headerName)
              && "chunked".equalsIgnoreCase(stringBuilder.toString())) {
            // do not write this header because Tomcat already assembled the chunks itself
            continue;
          }
          response.addHeader(headerName, stringBuilder.toString());
        }
      }
    }
  }

  /**
   * Helper function to detect if a specific header is in a given ignore list.
   *
   * @return true: in, false: not in
   */
  private boolean isInIgnoreList(String headerName, String[] ignoreList) {
    if (headerName == null) {
      return false;
    }

    for (String headerToIgnore : ignoreList) {
      if (headerName.equalsIgnoreCase(headerToIgnore)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Copy client's headers in the request to send to the final host Trick the host by hiding the
   * proxy indirection and keep useful headers information.
   *
   * @param uc Contains now headers from client request except Host
   */
  protected void copyHeadersToConnection(HttpServletRequest request, HttpURLConnection uc) {

    for (Enumeration enumHeader = request.getHeaderNames(); enumHeader.hasMoreElements(); ) {
      String headerName = (String) enumHeader.nextElement();
      String headerValue = request.getHeader(headerName);

      // copy every header except host
      if (!"host".equalsIgnoreCase(headerName)) {
        uc.setRequestProperty(headerName, headerValue);
      }
    }
  }

  /**
   * Check if the content type is accepted by the proxy.
   *
   * @return true: valid; false: not valid
   */
  protected boolean isContentTypeValid(final String contentType) {

    // focus only on type, not on the text encoding
    String type = contentType.split(";")[0];
    for (String validTypeContent : SolrHttpProxy._validContentTypes) {
      if (validTypeContent.equals(type)) {
        return true;
      }
    }
    return false;
  }
}
