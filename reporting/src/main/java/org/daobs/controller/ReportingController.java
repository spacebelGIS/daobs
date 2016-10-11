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

package org.daobs.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.solr.client.solrj.SolrClient;
import org.daobs.index.SolrServerBean;
import org.daobs.indicator.config.Reporting;
import org.daobs.indicator.config.Reports;
import org.daobs.util.UnzipUtility;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.JDOMResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


@Api(value = "reports",
    tags = "reports",
    description = "Report operations")
@EnableWebMvc
@Controller
public class ReportingController {

  public static final String INDICATOR_CONFIGURATION_DIR =
      "/WEB-INF/datadir/monitoring/";
  public static final String INDICATOR_CONFIGURATION_FILE_PREFIX = "config-";
  private static final String INDICATOR_CONFIGURATION_ID_MATCHER =
      INDICATOR_CONFIGURATION_FILE_PREFIX + "(.*).xml";
  private static final Pattern INDICATOR_CONFIGURATION_ID_PATTERN =
      Pattern.compile(INDICATOR_CONFIGURATION_ID_MATCHER);

  @Resource(name = "dataSolrServer")
  SolrServerBean server;

  @Value("${solr.core.data}")
  private String collection;

  @Value("${reports.dir}")
  private String reportsPath;

  @Value("${es.url}")
  private String esUrl;

  public void setCollection(String collection) {
    this.collection = collection;
  }

  public String elementToJson(Element xml) {
    try {
      XContentBuilder xcb = jsonBuilder()
        .startObject();

      List childNodes = xml.getChildren();

      if (childNodes != null) {
        childNodes.forEach(o -> {
          if (o instanceof Element) {
            Element e = (Element) o;
            try {
              xcb.field(
                e.getAttributeValue("name"),
                e.getTextNormalize());
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        });
      }
      xcb.endObject();
      return xcb.string();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getId(Element xml) {
    Iterator i = xml.getChildren().iterator();
    while (i.hasNext()) {
      Object o = i.next();

      if (o instanceof Element) {
        Element e = (Element) o;
        if (e.getAttributeValue("name").equals("id")) {
          return e.getTextNormalize();
        }
      }
    }
    return null;
  }

  /**
   * Add a report.
   */
  @ApiOperation(value = "Add a report",
      nickname = "addReport")
  @RequestMapping(
      value = "/reports",
      produces = {
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Map<String, String>> add(
      @ApiParam(value = "The file to upload")
      @RequestParam("file")
      MultipartFile file) throws Exception {

    File xmlFile = File.createTempFile("report", ".xml");
    FileUtils.writeByteArrayToFile(xmlFile, file.getBytes());

    final String xslt = "/xslt/inspire-monitoring-reporting.xsl";
    InputStream streamSource = this.getClass().getResourceAsStream(xslt);
    Source stylesheet = new StreamSource(streamSource);
    URL url = this.getClass().getResource(xslt);
    // http://stackoverflow.com/questions/3699860/resolving-relative-paths-when-loading-xslt-files
    if (url != null) {
      stylesheet.setSystemId(url.toExternalForm());
    } else {
      // log.warning("WARNING: Error when setSystemId for XSL: "
      // + xslt + ". Check resource location.");
    }

    Element results = simpleTransform(xmlFile.getAbsolutePath(),
        stylesheet);

    boolean success = true;
    Map<String, String> errors = new HashMap<>();
    if (results != null) {
      Iterator i = results.getChildren().iterator();
      // TODO: Use bulk import
      while (i.hasNext()) {
        Object o = i.next();
        try {
          if (o instanceof Element) {
            Element e = (Element) o;
            String json = elementToJson(e);
            String id = getId(e);
            HttpPost uploadFile = new HttpPost(
              esUrl + "/indicators/" +  URLEncoder.encode(id, "UTF-8")
            );
            HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
            uploadFile.setEntity(entity);
            uploadFile.setHeader("Content-Type", "application/json");
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(uploadFile);
            if (response.getStatusLine().getStatusCode() != 201) {
              success = false;
              errors.put(id, IOUtils.toString(response.getEntity().getContent()));
            }
          }
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    }
    if (success) {
      return new ResponseEntity<>(errors, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Simple XSL transformation. To be Improved.
   */
  public static Element simpleTransform(String sourcePath,
                                        Source stylesheet) {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    JDOMResult jdomResult = new JDOMResult();
    try {
      Transformer transformer =
          transformerFactory.newTransformer(stylesheet);

      transformer.transform(new StreamSource(new File(sourcePath)),
          jdomResult);//new StreamResult(new File(resultDir)));
      return jdomResult.getDocument().getRootElement().detach();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return null;
  }

  /**
   * Remove one or more reports.
   */
  @ApiOperation(value = "Remove one or more reports",
                nickname = "deleteReport")
  @RequestMapping(
      value = "/reports",
      produces = {
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> delete(
      @ApiParam(
          value = "A query to select report to delete",
          required = true)
      @RequestParam final String query) throws Exception {

    SolrClient client = server.getServer();
    // TODO: This can delete whatever docs
    client.deleteByQuery(collection, query);
    client.commit(collection);

    return new ResponseEntity<>("", HttpStatus.OK);
  }


  /**
   * Render reporting using XSLT view.
   */
  public static IndicatorCalculatorImpl generateReporting(HttpServletRequest request,
                                                          String reporting,
                                                          String scopeId,
                                                          String fq,
                                                          boolean calculate)
      throws FileNotFoundException {
    String configurationFilePath =
        INDICATOR_CONFIGURATION_DIR
        + INDICATOR_CONFIGURATION_FILE_PREFIX
        + reporting + ".xml";
    File configurationFile =
        new File(request.getSession().getServletContext()
           .getRealPath(configurationFilePath));

    if (configurationFile.exists()) {
      IndicatorCalculatorImpl indicatorCalculator =
          new IndicatorCalculatorImpl(configurationFile);

      if (calculate) {
        indicatorCalculator.computeIndicators(scopeId, fq);
      }
      // adds the XML source file to the model so the XsltView can detect
      return indicatorCalculator;
    } else {
      throw new FileNotFoundException(String.format(
          "Reporting configuration "
          + "'%s' file does not exist for reporting '%s'.",
          configurationFilePath,
          reporting));
    }
  }

  /**
   * Get list of available reports.
   */
  @ApiOperation(value = "Get list of available reports",
      nickname = "getReports")
  @RequestMapping(value = "/reports",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.GET)
  @ResponseBody
  public Reports getReports(HttpServletRequest request)
    throws IOException {
    File file = null;
    File[] paths = null;
    Reports reports = new Reports();
    try {
      file = new File(request.getSession().getServletContext()
        .getRealPath(INDICATOR_CONFIGURATION_DIR));
      FilenameFilter filenameFilter = (file1, name) -> {
        if (name.startsWith(INDICATOR_CONFIGURATION_FILE_PREFIX)
            && name.endsWith(".xml")) {
          return true;
        }
        return false;
      };

      paths = file.listFiles(filenameFilter);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (paths != null && paths.length > 0) {
      for (File configFile : paths) {
        Reporting reporting = new Reporting();
        Matcher matcher = INDICATOR_CONFIGURATION_ID_PATTERN.matcher(configFile.getName());
        if (matcher.find()) {
          reporting.setId(matcher.group(1));
        }
        reports.addReporting(reporting);
      }
    }
    return reports;
  }

  /**
   * Get report specification in XML or JSON format.
   */
  @ApiOperation(value = "Get report specification",
      nickname = "getReports")
  @RequestMapping(value = "/reports/{reporting}",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.GET)
  @ResponseBody
  public Reporting get(HttpServletRequest request,
                       @ApiParam(
                           value = "The report type to generate",
                           required = true)
                       @PathVariable(value = "reporting") String reporting,
                       @ApiParam(
                           value = "An optional scope")
                       @RequestParam(
                         value = "scopeId",
                         defaultValue = "",
                         required = false) String scopeId,
                       @ApiParam(
                         value = "An optional filter query to generate report on a subset",
                         required = true)
                       @RequestParam(
                         value = "fq",
                         defaultValue = "",
                         required = false) String fq)
    throws IOException {
    IndicatorCalculatorImpl indicatorCalculator =
        generateReporting(request, reporting, scopeId, fq.trim(), true);
    return indicatorCalculator.getConfiguration();
  }

  /**
   * Generate a specific report for a specific area in XML or JSON format.
   *
   * @param fq Filter query to be applied on top of the territory filter
   *
   */
  @ApiOperation(value = "Generate a report",
      nickname = "getReports")
  @RequestMapping(value = "/reports/{reporting}/{territory}",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.GET)
  @ResponseBody
  public Reporting get(HttpServletRequest request,
                       @ApiParam(
                         value = "The report type to generate",
                         required = true)
                       @PathVariable(value = "reporting") String reporting,
                       @ApiParam(
                         value = "A territory",
                         required = true)
                       @PathVariable(value = "territory") String territory,
                       @ApiParam(
                         value = "An optional scope")
                       @RequestParam(
                         value = "scopeId",
                         defaultValue = "",
                         required = false) String scopeId,
                       @ApiParam(
                         value = "An optional filter query to generate report on a subset",
                         required = true)
                       @RequestParam(
                         value = "fq",
                         defaultValue = "",
                         required = false) String fq)
    throws IOException {
    IndicatorCalculatorImpl indicatorCalculator =
        generateReporting(request, reporting, scopeId, "+territory:" + territory
          + (StringUtils.isEmpty(fq) ? "" : " " + fq.trim()), true);
    return indicatorCalculator.getConfiguration();
  }

  /**
   * Remove all ETF reports.
   */
  @ApiOperation(value = "Remove all ETF reports",
      nickname = "deleteEtfReports")
  @RequestMapping(value = "/reports/etf",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> deleteEtfReports() throws Exception {

    File reportDirectory = new File(this.reportsPath);
    try {
      if (reportDirectory.isDirectory()) {
        FileUtils.cleanDirectory(reportDirectory);
      }
    } catch (Exception ex) {
      ;
    }

    return new ResponseEntity<>("All ETF reports removed", HttpStatus.OK);
  }

  /**
   * Remove all reports related to a harvester.
   */
  @ApiOperation(value = "Remove reports related to a harvester",
      nickname = "deleteEtfHarvesterReports")
  @RequestMapping(value = "/reports/etf/{uuid}",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> deleteEtfHarvesterReports(
      @PathVariable(value = "uuid") String harvesterUuid
  ) throws Exception {

    // Delete the ETF reports
    String harvesterReportsPath = Paths.get(this.reportsPath,
        harvesterUuid).toString();

    File reportDirectory = new File(harvesterReportsPath);
    if (reportDirectory.exists()) {
      FileUtils.deleteQuietly(reportDirectory);
      return new ResponseEntity<>("All ETF reports for harvester ("
          + harvesterUuid + ") removed", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("No ETF reports for harvester ("
          + harvesterUuid + ") found", HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Add a ETF reports.
   */
  @ApiOperation(value = "Add ETF reports",
      nickname = "addEtfReports")
  @RequestMapping(
      value = "/reports/etf",
      produces = {
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> addEtfReports(
      @ApiParam(value = "The file to upload")
      @RequestParam("file")
      MultipartFile file) throws Exception {

    File zipFile = File.createTempFile("reports", ".zip");
    FileUtils.writeByteArrayToFile(zipFile, file.getBytes());

    UnzipUtility unzipper = new UnzipUtility();
    unzipper.unzip(zipFile, new File(this.reportsPath));

    return new ResponseEntity<>("", HttpStatus.OK);
  }
}
