package org.daobs.harvester;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Aggregation strategy to combine harvester
 * response and harvester details.
 *
 * Created by francois on 29/09/14.
 */
public class HarvesterDetailsAggregate implements
        org.apache.camel.processor.aggregate.AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        // TODO: Use Document.class instead
        String cswResponse = original.getIn().getBody(String.class);
        String harvesterDetails = resource.getIn().getBody(String.class);
        String mergeResult = "<harvestedContent>" +
                harvesterDetails + cswResponse + "</harvestedContent>";

        if (original.getPattern().isOutCapable()) {
            original.getOut().setBody(mergeResult);
        } else {
            original.getIn().setBody(mergeResult);
        }
        return original;
    }

    /**
     * Combine a CSW response with harvester details.
     * @param cswResponse
     * @param url
     * @param territory
     * @return
     */
    public Document doTransform(
            Document cswResponse,
            String url,
            String territory) {
        try {
            Element root = (Element) cswResponse.createElement("harvestedContent");
            Element cswRecords = (Element)cswResponse.getFirstChild().cloneNode(true);
            Element harvesterDetails = (Element) cswResponse.createElement("harvester");
            Element harvesterTerritory = (Element) cswResponse.createElement("territory");
            harvesterTerritory.setTextContent(territory);
            Element harvesterUrl = (Element) cswResponse.createElement("url");
            harvesterUrl.setTextContent(url);
            harvesterDetails.appendChild(harvesterUrl);
            harvesterDetails.appendChild(harvesterTerritory);

            cswResponse.replaceChild(root, cswResponse.getFirstChild());
            root.appendChild(harvesterDetails);
            root.appendChild(cswRecords);
            return cswResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cswResponse;
    }
}
