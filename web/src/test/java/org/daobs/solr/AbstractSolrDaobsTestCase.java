package org.daobs.solr;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.UpdateParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.UpdateRequestHandler;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.response.QueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.util.AbstractSolrTestCase;
import org.daobs.index.SolrServerBean;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by francois on 12/12/14.
 */

public class AbstractSolrDaobsTestCase
        extends AbstractSolrTestCase {
    protected SolrServer server;

    @Before
    @Override
    public void setUp() throws Exception {
/*
        // Setup XSLT Transformer Factory
        System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");


        // Test requires to load unsafe resources
        System.setProperty("solr.allow.unsafe.resourceloading", "true");


        // TODO: Fix path
        String configFilePath = new FileSystemResource("/home/francois/dev/daobs/web/src/main/solr-cores/data/conf/solrconfig.xml").getPath();
        String schemaFilePath = new FileSystemResource("/home/francois/dev/daobs/web/src/main/solr-cores/data/conf/schema.xml").getPath();
        String solrHome = new FileSystemResource("/home/francois/dev/daobs/web/src/main/solr-cores/").getPath();



        initCore(configFilePath, schemaFilePath, solrHome, "data");

        super.setUp();

        server = new EmbeddedSolrServer(
                h.getCoreContainer(),
                h.getCore().getName()
        );


        initBeans();*/
    }

    public void initBeans() {
        // Initialize the server bean which may be used
        // in XSLT java calls for example.
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-configuration.xml");

        SolrServerBean message = (SolrServerBean) applicationContext.getBean("SolrServer");
        SolrServerBean.get().setServer(server);
    }

    @After
    public void destroy() {
        h.getCoreContainer().shutdown();
    }



    /**
     * Load an ISO19139 document.
     *
     * @param fileToLoad
     * @return
     * @throws Exception
     */
    public String loadMetadata(String fileToLoad) throws Exception {
        String xsltTransformation = "metadata-iso19139.xsl";
        return loadData(fileToLoad, xsltTransformation);
    }

    /**
     * Load an INSPIRE reporting or ancillary information.
     *
     * @param fileToLoad
     * @return
     * @throws Exception
     */
    public String loadReporting(String fileToLoad) throws Exception {
        String xsltTransformation = "inspire-monitoring-reporting.xsl";
        return loadData(fileToLoad, xsltTransformation);
    }

    /**
     * Load an XML document to Solr using an XSLT transformation
     * which has to be define in the Solr core configuration (see conf/xslt/).
     *
     * @param fileToLoad The XML file name to load.
     * @param xsltTransformation The XSLT name to use.
     *
     * @return  The Solr response.
     *
     * @throws Exception
     */
    public String loadData(String fileToLoad, String xsltTransformation) throws Exception {
        File file = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource(fileToLoad).toURI());

        String xml = Files.toString(file, Charsets.UTF_8);

        Map<String, String> args = new HashMap<String, String>();
        args.put(CommonParams.TR, xsltTransformation);
        args.put(UpdateParams.ASSUME_CONTENT_TYPE, "application/xml");

        SolrCore core = h.getCore();
        LocalSolrQueryRequest req =
                new LocalSolrQueryRequest(core, new MapSolrParams(args));
        ArrayList<ContentStream> streams = new ArrayList<ContentStream>();
        streams.add(new ContentStreamBase.StringStream(xml));
        req.setContentStreams(streams);
        SolrQueryResponse rsp = new SolrQueryResponse();

        UpdateRequestHandler handler = new UpdateRequestHandler();
        handler.init(new NamedList<String>());
        handler.handleRequestBody(req, rsp);

        StringWriter sw = new StringWriter(32000);
        QueryResponseWriter responseWriter = core.getQueryResponseWriter(req);
        responseWriter.write(sw, req, rsp);
        req.close();

        return sw.toString();
    }
}
