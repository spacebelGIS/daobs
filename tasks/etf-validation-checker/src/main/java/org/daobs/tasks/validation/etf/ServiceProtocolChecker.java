package org.daobs.tasks.validation.etf;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import scala.actors.threadpool.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Class to identify the service protocol.
 *
 * @author Jose García
 *
 */
public class ServiceProtocolChecker {
    private Log log = LogFactory.getLog(this.getClass());

    private String endPoint;

    private String errorMessage;

    // Type of service: download, view
    private ServiceType serviceType;

    // The declared protocol, to establish the precedence of checks
    private String declaredProtocol;


    public String getErrorMessage() {
        if (StringUtils.isNotEmpty(this.errorMessage)) {
            return this.errorMessage;

        } else {
            return "Protocol from " + this.endPoint +
                    " (serviceType=" + this.serviceType.toString() +  ") can't be identified.";
        }
    }

    public ServiceProtocolChecker(String endPoint, ServiceType serviceType, String declaredProtocol) {
        this.endPoint = endPoint;
        this.serviceType = serviceType;
        this.declaredProtocol = declaredProtocol;
    }

    public ServiceProtocol check() {
        if (serviceType.equals(ServiceType.Download)) {
            return checkDownloadService();

        } else if (serviceType.equals(ServiceType.View)) {
            return checkViewService();
        }

        return null;
    }


    private ServiceProtocol checkViewService() {
        if (declaredProtocol.toLowerCase().contains("wms")) {
            if (checkWMS()) {
                return ServiceProtocol.WMS;
            } else if (checkWMTS()) {
                return ServiceProtocol.WMTS;
            }

        } else {
            if (checkWMTS()) {
                return ServiceProtocol.WMTS;
            } else if (checkWMS()) {
                return ServiceProtocol.WMS;
            }
        }

        return null;
    }

    private ServiceProtocol checkDownloadService() {
        if (declaredProtocol.toLowerCase().contains("atom")) {
            if (checkAtom()) {
                return ServiceProtocol.ATOM;

            } else if (checkWFS()) {
                return ServiceProtocol.WFS;
            }

        } else {
            if (checkWFS()) {
                return ServiceProtocol.WFS;

            } else if (checkAtom()) {
                return ServiceProtocol.ATOM;
            }
        }

        return null;
    }


    private boolean checkWMS() {
        Document doc = retrieve(buildUrl(this.endPoint, "request=GetCapabilities&service=WMS&version=1.3.0"));
        if (doc == null) return false;

        return hasRootNode(doc, Arrays.asList(new String[]{"WMS_Capabilities", "ServiceExceptionReport"}));
    }


    private boolean checkWMTS() {
        Document doc = retrieve(buildUrl(this.endPoint, "request=GetCapabilities&service=WMTS&version=1.0.0"));
        if (doc == null) return false;

        return hasRootNode(doc, Arrays.asList(new String[]{"WMTS_Capabilities", "ServiceExceptionReport"}));
    }


    private boolean checkWFS() {
        Document doc = retrieve(buildUrl(this.endPoint, "request=GetCapabilities&service=WFS&version=1.1.0"));
        if (doc == null) return false;

        return hasRootNode(doc, Arrays.asList(new String[]{"WFS_Capabilities", "ServiceExceptionReport"}));
    }

    private boolean checkAtom() {
        Document doc = retrieve(this.endPoint);
        if (doc == null) return false;

        return hasRootNode(doc, Arrays.asList(new String[]{"feed"}));
    }

    /**
     * Retrieves the content of the url provided as a org.w3c.dom.Document object.
     *
     * @param url
     * @return
     */
    private Document retrieve(String url) {
        log.info("Retrieving url: " + url);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        try(CloseableHttpClient httpclient = HttpClients.custom().
                setDefaultRequestConfig(defaultRequestConfig).build()){
            try(CloseableHttpResponse response = httpclient.execute(new HttpGet(url))){
                String body = EntityUtils.toString(response.getEntity());

                DocumentBuilderFactory factory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                ByteArrayInputStream input = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
                Document doc = builder.parse(input);

                return doc;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            this.errorMessage = ex.getMessage();
        }

        return null;
    }

    /**
     * Checks if a org.w3c.dom.Document has any of the root elements provided.
     *
     * @param doc
     * @param roots
     * @return
     */
    private boolean hasRootNode(Document doc, List<String> roots) {
        Element rootNode = doc.getDocumentElement();
        if (rootNode == null) return false;

        String rootNodeName = rootNode.getNodeName();

        return (roots.contains(rootNodeName));
    }


    /**
     * Builds url with provided parameters.
     *
     * @param url
     * @param params
     * @return
     */
    private String buildUrl(String url, String params) {
        return url + (url.endsWith("?")?"":"?") + params;
    }
}
