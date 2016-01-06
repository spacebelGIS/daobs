package org.daobs.harvester;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

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
        String harvestedContent = original.getIn().getBody(String.class);
        String harvesterDetails = resource.getIn().getBody(String.class);
        String mergeResult = "<harvestedContent>" +
                                harvesterDetails + harvestedContent +
                             "</harvestedContent>";

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
     * @param harvesterConfig
     * @return
     */
    public Document doTransform(
            Document cswResponse,
            Document harvesterConfig) {
        try {
            Element root = (Element) cswResponse.createElement("harvestedContent");
            Element cswRecords = (Element)cswResponse.getFirstChild().cloneNode(true);
            Element harvesterConfigClone = (Element)harvesterConfig.getFirstChild().cloneNode(true);

            cswResponse.replaceChild(root, cswResponse.getFirstChild());
            root.appendChild(
                    root.getOwnerDocument().importNode(
                            harvesterConfigClone, true));
            root.appendChild(cswRecords);
            return cswResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cswResponse;
    }
}
