package org.daobs.harvester;

import org.apache.camel.Exchange;

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

    public void doTransform(Exchange original) {
        // TODO: Pass harvester details by parameters or file
        String cswResponse = original.getIn().getBody(String.class);
        String harvesterDetails = "<harvester><territory>nl</territory>" +
                "<url>nl</url></harvester>";//resource.getIn().getBody(String.class);
        String mergeResult = "<harvestedContent>" +
                harvesterDetails + cswResponse + "</harvestedContent>";
        if (original.getPattern().isOutCapable()) {
            original.getOut().setBody(mergeResult);
        } else {
            original.getIn().setBody(mergeResult);
        }
    }
}
