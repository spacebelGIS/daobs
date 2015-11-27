package org.daobs.tasks.validation.etf;

import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
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
 * Simple bean to call the etf validation service.
 *
 * @author Jose García
 */
public class EtfValidatorBean {
    String etfResourceTesterPath;

    public String getEtfResourceTesterPath() {
        return etfResourceTesterPath;
    }

    public void setEtfResourceTesterPath(String etfResourceTesterPath) {
        this.etfResourceTesterPath = etfResourceTesterPath;
    }

    /**
     * Get the input message body and validate
     * it against the INSPIRE validation service.
     * The output body contains the validation report.
     *
     * Headers are propagated.
     *
     * @param exchange
     */
    public void validateBody(Exchange exchange) {
        String xml = exchange.getIn().getBody(String.class);

        System.out.println("validateBody:" + xml);
        EtfValidationReport report = null;
        EtfValidatorClient validatorClient =
                new EtfValidatorClient(this.etfResourceTesterPath);

        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(input);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/doc/arr[@name='link']/str";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node n = nodeList.item(i);

                // TODO: Manage exceptional cases
                String[] linkInfo = n.getTextContent().split("\\|");
                String url = linkInfo[1];
                String protocol = linkInfo[0];

                if (!canProcessProtocol(protocol)) continue;

                report = validatorClient.validate(url, protocol);

                // TODO: Check this requirement
                // If multiple endpoints are mentioned in one metadata, only the first will be evaluated
                break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        exchange.getOut().setBody(report);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }


    private boolean canProcessProtocol(String protocol) {
        return (protocol.equalsIgnoreCase("OGC:WMS") ||
                protocol.equalsIgnoreCase("OGC:WTMS") ||
                protocol.equalsIgnoreCase("OGC:WFS") ||
                protocol.equalsIgnoreCase("INSPIRE Atom"));

    }
}
