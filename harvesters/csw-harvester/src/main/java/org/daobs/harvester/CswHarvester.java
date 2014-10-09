package org.daobs.harvester;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.NamedNode;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by francois on 9/1/14.
 */
public class CswHarvester {
    class Config {
        public void Config() {

        }
        List<String> pages = new ArrayList<String>();

        public int getMaxRecords() {
            return maxRecords;
        }

        public Config setMaxRecords(int maxRecords) {
            this.maxRecords = maxRecords;
            return this;
        }

        public int getStartPosition() {
            return startPosition;
        }

        public Config setStartPosition(int startPosition) {
            this.startPosition = startPosition;
            return this;
        }

        private int maxRecords = 20;
        private int startPosition = 1;
        private int numberOfRecordsMatched;

        private Node getRecordsFilter;

        public int getNumberOfPages() {
            return pages.size();
        }

        public List<String> getPages() {
            return pages;
        }
        public List<String> computeNumberOfPages() {
            int numberOfPages = numberOfRecordsMatched / maxRecords;
            int remainingRecords = numberOfRecordsMatched - (numberOfPages * maxRecords);

            int i;
            for (i = 0; i < numberOfPages; i++) {
                pages.add(i + "");
            }

            // Add one more page to collect remaining records
            if (remainingRecords > 0) {
                pages.add(i++ + "");
            }

            System.out.println(this);
            System.out.println("numberOfRecordsMatched " + numberOfRecordsMatched + ".");
            System.out.println("maxRecords " + maxRecords + ".");
            System.out.println("numberOfPages " + pages.size() + ".");
            System.out.println("remainingRecords " + remainingRecords + ".");
            return pages;
        }

        public Config setNumberOfRecordsMatched(int numberOfRecordsMatched) {
            this.numberOfRecordsMatched = numberOfRecordsMatched;
            return this;
        }


        public Node getRecordsFilter() {
            return this.getRecordsFilter;
        }
        public Config setRecordsFilter(Node getRecordsFilter) {
            this.getRecordsFilter = getRecordsFilter;
            return this;
        }
    }

    private Map<String, Config> harvesters = new HashMap<String, Config>();
    private String getRecordsTemplate = "";
    private String getRecordsHitsTemplate = "";

    private int maxRecords = 100;
    public int getMaxRecords() {
        return maxRecords;
    }
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public CswHarvester() {
        String fileName = "csw-get-records.xml";
        try {
            getRecordsTemplate = Files.toString(
                    new ClassPathResource(fileName).getFile(),
                    Charsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Can't find '" + fileName + "'.");
        }
        String fileNameHits = "csw-get-records-hits.xml";
        try {
            getRecordsHitsTemplate = Files.toString(
                    new ClassPathResource(fileNameHits).getFile(),
                    Charsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Can't find '" + fileNameHits + "'.");
        }
    }

    public Config initialize(@Header("harvesterUrl") String identifier,
                             @Header("harvesterFilter") Document filter) {

        Config config = new Config();
        config.setMaxRecords(maxRecords);
        config.setRecordsFilter(filter.getFirstChild());
        harvesters.put(identifier, config);
        return config;
    }
    private Config getConfig(@Header("harvesterUrl") String identifier) {
        Config config = harvesters.get(identifier);
        if (config == null) {
            config = new Config();
            config.setMaxRecords(maxRecords);
            harvesters.put(identifier, config);

        }
        System.out.println(identifier + ">getConfig:" + config);

        return config;
    }

    public void setNumberOfRecords(@Header("harvesterUrl") String identifier, String numberOfRecordsMatched) {
        Config config = getConfig(identifier);
        config.setNumberOfRecordsMatched(Integer.parseInt(numberOfRecordsMatched));
        System.out.println(identifier + ">setNumberOfRecords:" + numberOfRecordsMatched);

    }
    public List<String> getPages(@Header("harvesterUrl") String identifier) {
        Config config = getConfig(identifier);
        return config.computeNumberOfPages();
    }
    public int getNumberOfPages(@Header("harvesterUrl") String identifier) {
        Config config = getConfig(identifier);
        System.out.println(identifier + ">getNumberOfPages:" + config.getPages().size());
        return config.getPages().size();
    }

    private String buildGetRecordsQuery(Node filter,
                                        boolean forHits,
                                        int startPosition,
                                        int maxRecords) {

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        try {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document getRecordsRequest =
                    builder.parse(
                            new InputSource(
                                    new StringReader(
                                            forHits ?
                                                    this.getRecordsHitsTemplate :
                                                    this.getRecordsTemplate)));

            // Append the optional CSW filter to the query element
            if (filter != null) {
                Node children =
                        getRecordsRequest.getDocumentElement()
                                .getElementsByTagNameNS(
                                        "http://www.opengis.net/ogc",
                                        "Filter").item(0);
                if (children != null) {
                    Node filterNode = getRecordsRequest.importNode(filter,true);
                    Node constraintNode = children.getParentNode();
                    constraintNode.removeChild(children);
                    constraintNode.appendChild(filterNode);
                }
            }

            if (!forHits) {
                // Set the start position and max record
                NamedNodeMap attributes = getRecordsRequest.getFirstChild().getAttributes();
                Node maxRecordsNode = attributes.getNamedItem("maxRecords");
                if (maxRecordsNode != null) {
                    maxRecordsNode.setTextContent(maxRecords + "");
                }


                Node startPositionNode = attributes.getNamedItem("startPosition");
                if (startPositionNode != null) {
                    startPositionNode.setTextContent(startPosition + "");
                }
            }
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(
                    new DOMSource(getRecordsRequest),
                    new StreamResult(sw));
            String response = sw.toString();
            return response;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String generateGetRecordsHitsQuery(
            @Header("harvesterUrl") String identifier,
            Exchange exchange) {
        Config config = getConfig(identifier);
        return buildGetRecordsQuery(config.getRecordsFilter(),
                true,
                0,
                0
        );

    }
    public synchronized String generateGetRecordsQuery(
            @Header("harvesterUrl") String identifier,
            Exchange exchange) {
        int page = (Integer) exchange.getProperty("CamelSplitIndex");
        Config config = getConfig(identifier);
        return buildGetRecordsQuery(config.getRecordsFilter(),
                false,
                page * maxRecords + 1,
                maxRecords);
    }
}
