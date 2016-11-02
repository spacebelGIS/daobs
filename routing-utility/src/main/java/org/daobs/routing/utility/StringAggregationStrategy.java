/**
 * Copyright 2014-2016 European Environment Agency
 * <p>
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * <p>
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 * <p>
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package org.daobs.routing.utility;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by francois on 05/01/16.
 */
public class StringAggregationStrategy implements AggregationStrategy {

  /**
   * Aggregate old exchange with the new one.
   */
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    if (oldExchange == null) {
      return newExchange;
    }

    String oldBody = oldExchange.getIn().getBody(String.class);
    String newBody = newExchange.getIn().getBody(String.class);
    oldExchange.getIn().setBody(oldBody + "\n" + newBody);
    return oldExchange;
  }
}
