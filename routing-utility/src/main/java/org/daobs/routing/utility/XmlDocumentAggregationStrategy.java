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

package org.daobs.routing.utility;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by francois on 05/01/16.
 */
public class XmlDocumentAggregationStrategy implements AggregationStrategy {
  private String rootTagName;

  /**
   * Aggregate old exchange with the new one.
     */
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    Document newBody = (Document) newExchange.getIn().getBody();
    Document results = null;
    if (oldExchange == null) {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = null;
      try {
        docBuilder = docFactory.newDocumentBuilder();
      } catch (ParserConfigurationException exception) {
        exception.printStackTrace();
      }

      if (docBuilder != null) {
        results = docBuilder.newDocument();
        Element rootElement = results.createElement(rootTagName);
        rootElement.appendChild(
            results.importNode(
               newBody.getDocumentElement().cloneNode(true), true));
        results.appendChild(rootElement);
        newExchange.getIn().setBody(results);
        return newExchange;
      } else {
        return oldExchange;
      }
    } else {
      results = oldExchange.getIn().getBody(Document.class);
      results.getFirstChild().appendChild(
          results.importNode(
              newBody.getDocumentElement().cloneNode(true), true));
      return oldExchange;
    }
  }

  public String getRootTagName() {
    return rootTagName;
  }

  public void setRootTagName(String rootTagName) {
    this.rootTagName = rootTagName;
  }
}
