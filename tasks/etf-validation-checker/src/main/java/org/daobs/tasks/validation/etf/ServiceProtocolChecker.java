/**
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

package org.daobs.tasks.validation.etf;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import scala.actors.threadpool.Arrays;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Class to identify the service protocol.
 *
 * @author Jose García
 *
 */
public class ServiceProtocolChecker {
  private Log log = LogFactory.getLog(this.getClass());

  private String endPoint;

  private String errorMessage;

  // Type of service: download, view
  private ServiceType serviceType;

  // The declared protocol, to establish the precedence of checks
  private String declaredProtocol;


  /**
   * Service protocol checker.
     */
  public ServiceProtocolChecker(String endPoint, ServiceType serviceType, String declaredProtocol) {
    this.endPoint = endPoint;
    this.serviceType = serviceType;
    this.declaredProtocol = declaredProtocol;
  }

  /**
   * Get the error message.
   */
  public String getErrorMessage() {
    if (StringUtils.isNotEmpty(this.errorMessage)) {
      return this.errorMessage;

    } else {
      return "Protocol from " + this.endPoint
          + " (serviceType=" + this.serviceType.toString()
          + ") can't be identified.";
    }
  }

  /**
   * Check service protocol.
     */
  public ServiceProtocol check() {
    if (serviceType.equals(ServiceType.Download)) {
      return checkDownloadService();

    } else if (serviceType.equals(ServiceType.View)) {
      return checkViewService();
    }

    return null;
  }


  private ServiceProtocol checkViewService() {
    if (declaredProtocol.toLowerCase().contains("wms")) {
      if (checkWms()) {
        return ServiceProtocol.WMS;
      } else if (checkWmts()) {
        return ServiceProtocol.WMTS;
      }

      errorMessage = errorMessage + ". Tried with WMS (declared protocol) "
        + "and WMTS protocols. ETF validation not applied.";

    } else {
      if (checkWmts()) {
        return ServiceProtocol.WMTS;
      } else if (checkWms()) {
        return ServiceProtocol.WMS;
      }

      errorMessage = errorMessage + ". Tried with WMTS (declared protocol) "
        + "and WMS protocols. ETF validation not applied.";
    }

    return null;
  }

  private ServiceProtocol checkDownloadService() {
    if (declaredProtocol.toLowerCase().contains("atom")) {
      if (checkAtom()) {
        return ServiceProtocol.ATOM;

      } else if (checkWfs()) {
        return ServiceProtocol.WFS;
      }

      errorMessage = errorMessage + ". Tried with ATOM (declared protocol) "
        + "and WFS protocols. ETF validation not applied.";

    } else {
      if (checkWfs()) {
        return ServiceProtocol.WFS;

      } else if (checkAtom()) {
        return ServiceProtocol.ATOM;
      }

      errorMessage = errorMessage + ". Tried with WFS (declared protocol) "
        + "and ATOM protocols. ETF validation not applied.";

    }

    return null;
  }


  private boolean checkWms() {
    Document doc = retrieve(buildUrl(this.endPoint,
        "request=GetCapabilities&service=WMS&version=1.3.0"));
    if (doc == null) {
      return false;
    }

    return hasRootNode(doc, Arrays.asList(new String[]{
        "WMS_Capabilities", "ServiceExceptionReport"}));
  }


  private boolean checkWmts() {
    Document doc = retrieve(buildUrl(this.endPoint,
        "request=GetCapabilities&service=WMTS&version=1.0.0"));
    if (doc == null) {
      return false;
    }

    return hasRootNode(doc, Arrays.asList(new String[]{
        "WMTS_Capabilities", "ServiceExceptionReport"}));
  }


  private boolean checkWfs() {
    Document doc = retrieve(buildUrl(this.endPoint,
        "request=GetCapabilities&service=WFS&version=1.1.0"));
    if (doc == null) {
      return false;
    }

    return hasRootNode(doc, Arrays.asList(new String[]{
        "WFS_Capabilities", "ServiceExceptionReport"}));
  }

  private boolean checkAtom() {
    Document doc = retrieve(this.endPoint);
    if (doc == null) {
      return false;
    }

    return hasRootNode(doc, Arrays.asList(new String[]{"feed"}));
  }

  /**
   * Retrieves the content of the url provided as a org.w3c.dom.Document object.
   *
   */
  private Document retrieve(String url) {
    log.info("Retrieving url: " + url);

    RequestConfig defaultRequestConfig = RequestConfig.custom()
        .setSocketTimeout(5000)
        .setConnectTimeout(5000)
        .setConnectionRequestTimeout(5000)
        .build();

    try (CloseableHttpClient httpclient = HttpClients.custom()
        .setDefaultRequestConfig(defaultRequestConfig).build()) {
      try (CloseableHttpResponse response = httpclient.execute(new HttpGet(url))) {

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          String body = EntityUtils.toString(response.getEntity());

          DocumentBuilderFactory factory =
              DocumentBuilderFactory.newInstance();
          factory.setNamespaceAware(true);

          DocumentBuilder builder = factory.newDocumentBuilder();

          ByteArrayInputStream input =
              new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
          Document doc = builder.parse(input);

          return doc;

        } else {

          // Only add the error message if not already filled. The first protocol
          // checked (declared protocol) has higher priority that next protocols checked.
          if (StringUtils.isEmpty(this.errorMessage)) {
            this.errorMessage = response.getStatusLine().getReasonPhrase();
          }
        }
      }
    } catch (Exception ex) {
      log.error(ex.getMessage());
      this.errorMessage = ex.getMessage();
    }

    return null;
  }

  /**
   * Checks if a org.w3c.dom.Document has any of the root elements provided.
   *
   */
  private boolean hasRootNode(Document doc, List<String> roots) {
    Element rootNode = doc.getDocumentElement();
    if (rootNode == null) {
      return false;
    }

    String rootNodeName = rootNode.getLocalName();

    return (roots.contains(rootNodeName));
  }


  /**
   * Builds url with provided parameters.
   *
   */
  private String buildUrl(String url, String params) {
    return url + (url.endsWith("?") ? "" : "?") + params;
  }
}
