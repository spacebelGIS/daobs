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
package org.daobs.harvester;

import org.apache.camel.Exchange;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
            Element cswRecords = (Element) cswResponse.getFirstChild().cloneNode(true);
            Element harvesterConfigClone = (Element) harvesterConfig.getFirstChild().cloneNode(true);

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
