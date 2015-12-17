package org.daobs.tasks.validation.etf;

import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Simple bean to call the ETF validation service.
 *
 * @author Jose García
 */
public class EtfValidatorBean {
    private Log log = LogFactory.getLog(this.getClass());

    String etfResourceTesterPath;

    String etfResourceTesterHtmlReportsPath;

    String etfResourceTesterHtmlReportsUrl;

    public String getEtfResourceTesterPath() {
        return etfResourceTesterPath;
    }

    public void setEtfResourceTesterPath(String etfResourceTesterPath) {
        this.etfResourceTesterPath = etfResourceTesterPath;
    }

    public String getEtfResourceTesterHtmlReportsPath() {
        return etfResourceTesterHtmlReportsPath;
    }

    public void setEtfResourceTesterHtmlReportsPath(String etfResourceTesterHtmlReportsPath) {
        this.etfResourceTesterHtmlReportsPath = etfResourceTesterHtmlReportsPath;
    }

    public String getEtfResourceTesterHtmlReportsUrl() {
        return etfResourceTesterHtmlReportsUrl;
    }

    public void setEtfResourceTesterHtmlReportsUrl(String etfResourceTesterHtmlReportsUrl) {
        this.etfResourceTesterHtmlReportsUrl = etfResourceTesterHtmlReportsUrl;
    }

    /**
     * Get the input message body and validate
     * it against the ETF validation tool.
     * The output body contains the validation report.
     *
     * Headers are propagated.
     *
     * @param exchange
     */
    public void validateBody(Exchange exchange) {
        String xml = exchange.getIn().getBody(String.class);

        EtfValidationReport report = null;
        EtfValidatorClient validatorClient =
                new EtfValidatorClient(this.etfResourceTesterPath,
                        this.etfResourceTesterHtmlReportsPath,
                        this.etfResourceTesterHtmlReportsUrl);

        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(input);

            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/doc/arr[@name='serviceType']/str";
            String serviceType = (String) xPath.compile(expression).evaluate(doc, XPathConstants.STRING);

            expression = "/doc/arr[@name='linkUrl']/str";
            String url = (String) xPath.compile(expression).evaluate(doc, XPathConstants.STRING);

            expression = "/doc/arr[@name='linkProtocol']/str";
            String declaredProtocol = (String) xPath.compile(expression).evaluate(doc, XPathConstants.STRING);

            report = validatorClient.validate(url, ServiceType.fromString(serviceType), declaredProtocol);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        exchange.getOut().setBody(report);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
