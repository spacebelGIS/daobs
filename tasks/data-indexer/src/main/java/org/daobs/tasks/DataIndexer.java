/**
 * Copyright 2014-2016 European Environment Agency <p> Licensed under the EUPL, Version 1.1 or – as
 * soon they will be approved by the European Commission - subsequent versions of the EUPL (the
 * "Licence"); You may not use this work except in compliance with the Licence. You may obtain a
 * copy of the Licence at: <p> https://joinup.ec.europa.eu/community/eupl/og_page/eupl <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */

package org.daobs.tasks;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by francois on 18/05/15.
 */
public class DataIndexer {

  private static final String SEPARATOR = "\\|";
  Logger log = Logger.getLogger("org.daobs.task.DataIndexer");

  /**
   * Split a link field in a document.
   */
  public void splitLink(Exchange exchange) {
    exchange.getOut().setHeaders(exchange.getIn().getHeaders());

    Document linkDocument = (Document) exchange.getIn().getBody();
    String linkInfo = linkDocument.getFirstChild().getFirstChild().getTextContent();
    log.info(String.format("Parsing link %s.", linkInfo));
    String[] linkInfoArray = linkInfo.split(SEPARATOR);
    if (linkInfoArray.length == 4) {
      exchange.getOut().setHeader("linkProtocol", linkInfoArray[0]);
      exchange.getOut().setHeader("linkUrl", linkInfoArray[1]);
      exchange.getOut().setHeader("linkName", linkInfoArray[2]);
      exchange.getOut().setHeader("linkDesc", linkInfoArray[3]);
    } else {
      log.info(String.format(
          "  Link not properly formatted. Length != 4 is %s",
          linkInfoArray.length));
    }
  }


  /**
   * Use tika to parse document.
   *
   */
  public void parseDocument(@Header("linkUrl") String linkUrl,
                            Exchange exchange) {
    ContentHandler handler = new ToXMLContentHandler();
    exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    try {
      InputStream stream = new URL(linkUrl).openStream();
      AutoDetectParser parser = new AutoDetectParser();
      Metadata metadata = new Metadata();
      try {
        parser.parse(stream, handler, metadata);
        String text = handler.toString();
        exchange.getOut().setBody(text);
      } finally {
        stream.close();
      }
    } catch (IOException ioe) {
      log.warning(String.format("File '%s' not found. Error is %s",
          linkUrl, ioe.getMessage()));
      ioe.printStackTrace();
    } catch (SAXException se) {
      log.warning(String.format("SAX exception when opening '%s' not found. "
          + "Error is %s", linkUrl, se.getMessage()));
      se.printStackTrace();
    } catch (TikaException te) {
      log.warning(String.format("Tika exception when opening '%s' not found. "
          + "Error is %s", linkUrl, te.getMessage()));
      te.printStackTrace();
    }
  }
}
