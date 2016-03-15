package org.daobs.routing.utility;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Use this strategy when using split and no aggregation
 * of the results is required.
 *
 * Created by francois on 15/03/16.
 */
public class NullBodyStrategy implements AggregationStrategy {
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        newExchange.getIn().setBody(null);
        return oldExchange;
    }
}
