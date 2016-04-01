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
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by francois on 10/12/14.
 */
public class Utility {
    Logger log = Logger.getLogger("org.daobs.utility");

    /**
     * Encrypt a string using sha256Hex
     *
     * @param stringToEncrypt
     * @return
     */
    public String encrypt(@Header("stringToEncrypt") String stringToEncrypt) {
        return DigestUtils.sha256Hex(stringToEncrypt);
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
                public void warning(SAXParseException e) throws SAXException {
                    log.warning(e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    log.warning(e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    log.warning(e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = builder.parse(
                new InputSource(
                    new StringReader(xml)
                )
            );
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            log.warning("WARNING: Error when setSystemId for XSL: " +
                xslt + ". Check resource location.");
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
        } catch (IllegalArgumentException e) {
            log.warning("WARNING: transformerfactory doesnt like saxon attributes!");
        } finally {
            Transformer t = null;
            try {
                t = transFact.newTransformer(stylesheet);
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            Map<String, Object> headers = exchange.getIn().getHeaders();
            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                t.setParameter(key, headers.get(key));
            }
            try {
                t.transform(source, result);
                exchange.getOut().setBody(result.getWriter().toString());
            } catch (TransformerException e) {
                log.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
