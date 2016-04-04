/**
 * Copyright 2014-2016 European Environment Agency <p> Licensed under the EUPL, Version 1.1 or – as
 * soon they will be approved by the European Commission - subsequent versions of the EUPL (the
 * "Licence"); You may not use this work except in compliance with the Licence. You may obtain a
 * copy of the Licence at: <p> https://joinup.ec.europa.eu/community/eupl/og_page/eupl <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */

package org.daobs.tasks.validation.etf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Class to create the validation report from the EFT validation results.
 *
 * @author Jose García
 */
public class EftValidationReportBuilder {
  private Log log = LogFactory.getLog(this.getClass());


  /**
   * Creates a validation report from the ETF validation results.
   *
   */
  public EtfValidationReport build(File eftResults, String endPoint,
                                   ServiceProtocol protocol, String reportUrl) {
    EtfValidationReport report = new EtfValidationReport(endPoint, protocol.toString());

    try {
      DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      ByteArrayInputStream input = new ByteArrayInputStream(
          FileUtils.readFileToByteArray(eftResults));
      Document doc = builder.parse(input);

      XPath xpath = XPathFactory.newInstance().newXPath();
      String expression = "/testsuites/testsuite";
      NodeList nodeList = (NodeList) xpath
          .compile(expression)
          .evaluate(doc, XPathConstants.NODESET);

      int totalErrorsMandatory = 0;
      int totalFailuresMandatory = 0;
      int totalTestsMandatory = 0;
      double totalTimeMandatory = 0.0;

      int totalErrorsOptional = 0;
      int totalFailuresOptional = 0;
      int totalTestsOptional = 0;
      double totalTimeOptional = 0.0;

      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;

          String testName = element.getAttribute("name").toLowerCase();

          // Optional/mandatory tests are indicated in the report
          // created by ETF in the name attribute
          if (testName.endsWith("optional")) {
            totalErrorsOptional += Integer.parseInt(element.getAttribute("errors"));
            totalFailuresOptional += Integer.parseInt(element.getAttribute("failures"));
            totalTestsOptional += Integer.parseInt(element.getAttribute("tests"));
            totalTimeOptional += Double.parseDouble(element.getAttribute("time"));
          } else {
            totalErrorsMandatory += Integer.parseInt(element.getAttribute("errors"));
            totalFailuresMandatory += Integer.parseInt(element.getAttribute("failures"));
            totalTestsMandatory += Integer.parseInt(element.getAttribute("tests"));
            totalTimeMandatory += Double.parseDouble(element.getAttribute("time"));
          }

        }
      }

      report.setTotalErrors(totalErrorsMandatory);
      report.setTotalFailures(totalFailuresMandatory);
      report.setTotalTests(totalTestsMandatory);
      report.setTotalTime(totalTimeMandatory);

      report.setTotalErrorsOptional(totalErrorsOptional);
      report.setTotalFailuresOptional(totalFailuresOptional);
      report.setTotalTestsOptional(totalTestsOptional);
      report.setTotalTimeOptional(totalTimeOptional);

      // Replace CDATA sections in the xml
      report.setReport(FileUtils.readFileToString(eftResults).replace("]]>", "]]]]><![CDATA[>"));
      report.setReportUrl(reportUrl);

    } catch (Throwable ex) {
      log.error(ex.getMessage());
      report.setInfo(ex.getMessage());
      report.setValidationFailed(true);

    }

    log.info(report.toString());

    return report;
  }

  /**
   * Creates an error report.
   *
   */
  public EtfValidationReport buildErrorReport(String endPoint, String error) {
    EtfValidationReport report = new EtfValidationReport(endPoint, "");
    report.setInfo(error);
    report.setValidationFailed(true);

    return report;
  }
}
