package org.daobs.harvester;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

/**
 * Created by francois on 05/01/16.
 */
public class XmlDocumentAggregationStrategy implements AggregationStrategy {
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Document newBody = (Document)newExchange.getIn().getBody();
        Document results = null;
        if (oldExchange == null) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = null;
            try {
                docBuilder = docFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            results = docBuilder.newDocument();
            Element rootElement = results.createElement("results");
            rootElement.appendChild(
                        results.importNode(
                            newBody.getDocumentElement().cloneNode(true), true));
            results.appendChild(rootElement);
            newExchange.getIn().setBody(results);
            return newExchange;
        } else {
            results = oldExchange.getIn().getBody(Document.class);
            results.getFirstChild().appendChild(
                    results.importNode(
                            newBody.getDocumentElement().cloneNode(true), true));
            return oldExchange;
        }
    }
}