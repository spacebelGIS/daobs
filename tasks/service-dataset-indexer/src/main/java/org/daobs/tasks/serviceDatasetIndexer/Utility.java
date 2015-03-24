package org.daobs.tasks.serviceDatasetIndexer;

import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;
import org.apache.camel.Exchange;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by francois on 24/03/15.
 */
public class Utility {

    /**
     * Run XSLT transformation on the body of the Exchange
     * and set the output body to the results of the transformation.
     */
    public void transform(Exchange exchange, String xslt)  {
        String xml = exchange.getIn().getBody(String.class);

        exchange.getOut().setHeaders(exchange.getIn().getHeaders());


        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = domFactory.newDocumentBuilder();
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
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        try {
            transFact.setAttribute(FeatureKeys.VERSION_WARNING,false);
            transFact.setAttribute(FeatureKeys.LINE_NUMBERING,true);
            transFact.setAttribute(FeatureKeys.PRE_EVALUATE_DOC_FUNCTION,true);
            transFact.setAttribute(FeatureKeys.RECOVERY_POLICY, Configuration.RECOVER_SILENTLY);
            // Add the following to get timing info on xslt transformations
            transFact.setAttribute(FeatureKeys.TIMING,true);
        } catch (IllegalArgumentException e) {
            System.out.println("WARNING: transformerfactory doesnt like saxon attributes!");
            //e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }
}