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

import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by francois on 10/12/14.
 */
public class Utility {
  Logger log = Logger.getLogger("org.daobs.utility");

  /**
   * Encrypt a string using sha256Hex.
   */
  public String encrypt(@Header("stringToEncrypt") String stringToEncrypt) {
    return DigestUtils.sha256Hex(stringToEncrypt);
  }
  public static String documentToString(Document doc) {
    try {
      StringWriter sw = new StringWriter();
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

      transformer.transform(new DOMSource(doc), new StreamResult(sw));
      return sw.toString();
    } catch (Exception ex) {
      throw new RuntimeException("Error converting to String", ex);
    }
  }
  public Map<String, String> documentToJson(Document xml) {
    try {
      NodeList root = xml.getChildNodes();
      Node addNode = root.item(0);
      Map<String, String> listOfXcb = new HashMap<>();
      if (root != null) {
        NodeList records = addNode.getChildNodes();
        for (int i = 0; i < records.getLength(); i++) {
          Node record = records.item(i);
          if (record != null && record.getNodeType() == Node.ELEMENT_NODE) {
            XContentBuilder xcb = jsonBuilder()
              .startObject();
            String id = null;
            NodeList fields = record.getChildNodes();
            for (int j = 0; j < fields.getLength(); j++) {
              Node currentField = fields.item(j);
              if (currentField != null && currentField.getNodeType() == Node.ELEMENT_NODE) {
                Node name = currentField.getAttributes().getNamedItem("name");

                if (name != null) {
                  if (name.getTextContent().equals("geom")) {
                    continue;
                  }

                  if (name.getTextContent().equals("id")) {
                    id = currentField.getTextContent();
                  }

                  if (name.getTextContent().equals("geojson")) {
                    xcb.field("geom", currentField.getTextContent());
                  } else if (
                    // Skip some fields causing errors / TODO
                    !name.getTextContent().startsWith("conformTo_") &&
                      !name.getTextContent().startsWith("thesaurus_")) {
                    xcb.field(
                      name.getTextContent(),
                      currentField.getTextContent());
                  }
                }
              }
            }
            xcb.endObject();
            listOfXcb.put(id, xcb.string());
          }
        }
      }
      return listOfXcb;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String xmlToBulkJson(Exchange exchange) {
    Document xml = exchange.getIn().getBody(Document.class);
    StringBuffer stringBuffer = new StringBuffer();
    Map<String, String> xcb = documentToJson(xml);
    Iterator<String> iterator = xcb.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      stringBuffer.append(String.format(
        "{\"index\": {\"_index\": \"%s\", \"_type\": \"records\", \"_id\": \"%s\"}}",
        "records", key)).append("\n");
      stringBuffer.append(xcb.get(key)).append("\n");
    }
    exchange.getOut().setBody(stringBuffer.toString());
    return stringBuffer.toString();
  }
  /**
   * Run XSLT transformation on the body of the Exchange
   * and set the output body to the results of the transformation.
   */
  public void transform(Exchange exchange, String xslt) {
    String xml = exchange.getIn().getBody(String.class);

    exchange.getOut().setHeaders(exchange.getIn().getHeaders());


    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true);
    DocumentBuilder builder = null;
    try {
      builder = domFactory.newDocumentBuilder();
      builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
          log.warning(exception.getMessage());
          exception.printStackTrace();
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
          log.warning(exception.getMessage());
          exception.printStackTrace();
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
          log.warning(exception.getMessage());
          exception.printStackTrace();
        }
      });
    } catch (ParserConfigurationException exception) {
      exception.printStackTrace();
    }

    Assert.notNull(builder, "Null builder not allowed.");

    Document document = null;

    try {
      document = builder.parse(
        new InputSource(
          new StringReader(xml)
        )
      );
    } catch (SAXException exception) {
      exception.printStackTrace();
    } catch (IOException exception) {
      exception.printStackTrace();
    }

    DOMSource source = new DOMSource(document);

    TransformerFactory transFact = TransformerFactory.newInstance();

    InputStream streamSource = this.getClass().getResourceAsStream(xslt);

    Source stylesheet = new StreamSource(streamSource);


    URL url = this.getClass().getResource(xslt);
    // http://stackoverflow.com/questions/3699860/resolving-relative-paths-when-loading-xslt-files
    if (url != null) {
      stylesheet.setSystemId(url.toExternalForm());
    } else {
      log.warning("WARNING: Error when setSystemId for XSL: "
          + xslt + ". Check resource location.");
    }

    StringWriter sw = new StringWriter();

    StreamResult result = new StreamResult(sw);
    try {
      transFact.setAttribute(FeatureKeys.VERSION_WARNING, false);
      transFact.setAttribute(FeatureKeys.LINE_NUMBERING, true);
      transFact.setAttribute(FeatureKeys.PRE_EVALUATE_DOC_FUNCTION, true);
      transFact.setAttribute(FeatureKeys.RECOVERY_POLICY, Configuration.RECOVER_SILENTLY);
      // Add the following to get timing info on xslt transformations
      transFact.setAttribute(FeatureKeys.TIMING, true);
    } catch (IllegalArgumentException exception) {
      log.warning("WARNING: transformerfactory doesnt like saxon attributes!");
    } finally {
      Transformer trans = null;
      try {
        trans = transFact.newTransformer(stylesheet);
      } catch (TransformerConfigurationException exception) {
        exception.printStackTrace();
      }

      if (trans != null) {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        Iterator<Map.Entry<String, Object>> iterator = headers.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry<String, Object> entry = iterator.next();
          trans.setParameter(entry.getKey(), entry.getValue());
        }
        try {
          trans.transform(source, result);
          exchange.getOut().setBody(result.getWriter().toString());
        } catch (TransformerException exception) {
          log.warning(exception.getMessage());
          exception.printStackTrace();
        }
      }
    }
  }
}
