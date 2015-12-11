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
     * @return
     */
    public EtfValidationReport build(File eftResults, String endPoint, ServiceProtocol protocol) {
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

            int totalErrors = 0;
            int totalFailures = 0;
            int totalTests = 0;
            double totalTime = 0.0;

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    totalErrors += Integer.parseInt(eElement.getAttribute("errors"));
                    totalFailures += Integer.parseInt(eElement.getAttribute("failures"));
                    totalTests += Integer.parseInt(eElement.getAttribute("tests"));
                    totalTime += Double.parseDouble(eElement.getAttribute("time"));

                }
            }

            report.setTotalErrors(totalErrors);
            report.setTotalFailures(totalFailures);
            report.setTotalTests(totalTests);
            report.setTotalTime(totalTime);

            report.setReport(FileUtils.readFileToString(eftResults));

        } catch (Exception ex) {
            log.error(ex.getMessage());
            report.setInfo(ex.getMessage());
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

        return report;
    }
}
