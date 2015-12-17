package org.daobs.tasks.validation.etf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;

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
     * @param eftResults
     * @param endPoint
     * @param protocol
     * @param reportUrl
     * @return
     */
    public EtfValidationReport build(File eftResults, String endPoint, ServiceProtocol protocol, String reportUrl) {
        EtfValidationReport report = new EtfValidationReport(endPoint, protocol.toString());

        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream input = new ByteArrayInputStream(
                    FileUtils.readFileToByteArray(eftResults));
            Document doc = builder.parse(input);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/testsuites/testsuite";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

            int totalErrorsMandatory = 0;
            int totalFailuresMandatory = 0;
            int totalTestsMandatory = 0;
            double totalTimeMandatory = 0.0;

            int totalErrorsOptional = 0;
            int totalFailuresOptional = 0;
            int totalTestsOptional = 0;
            double totalTimeOptional = 0.0;

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String testName = eElement.getAttribute("name").toLowerCase();

                    // Optional/mandatory tests are indicated in the report created by ETF in the name attribute
                    if (testName.endsWith("optional")) {
                        totalErrorsOptional += Integer.parseInt(eElement.getAttribute("errors"));
                        totalFailuresOptional += Integer.parseInt(eElement.getAttribute("failures"));
                        totalTestsOptional += Integer.parseInt(eElement.getAttribute("tests"));
                        totalTimeOptional += Double.parseDouble(eElement.getAttribute("time"));
                    } else {
                        totalErrorsMandatory += Integer.parseInt(eElement.getAttribute("errors"));
                        totalFailuresMandatory += Integer.parseInt(eElement.getAttribute("failures"));
                        totalTestsMandatory += Integer.parseInt(eElement.getAttribute("tests"));
                        totalTimeMandatory += Double.parseDouble(eElement.getAttribute("time"));
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

        } catch (Exception ex) {
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
     * @param endPoint
     * @return
     */
    public EtfValidationReport buildErrorReport(String endPoint, String error) {
        EtfValidationReport report = new EtfValidationReport(endPoint, "");
        report.setInfo(error);
        report.setValidationFailed(true);

        return report;
    }
}
