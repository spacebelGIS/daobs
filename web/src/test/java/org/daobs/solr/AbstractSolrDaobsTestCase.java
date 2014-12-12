package org.daobs.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.util.AbstractSolrTestCase;
import org.daobs.index.SolrServerBean;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

/**
 * Created by francois on 12/12/14.
 */

public class AbstractSolrDaobsTestCase
        extends AbstractSolrTestCase {
    protected SolrServer server;

    @Before
    @Override
    public void setUp() throws Exception {

        // Setup XSLT Transformer Factory
        System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");


        // Test requires to load unsafe resources
        System.setProperty("solr.allow.unsafe.resourceloading", "true");


        // TODO: Fix path
        String configFilePath = new FileSystemResource("/home/francois/dev/daobs/web/src/main/config/data/conf/solrconfig.xml").getPath();
        String schemaFilePath = new FileSystemResource("/home/francois/dev/daobs/web/src/main/config/data/conf/schema.xml").getPath();
        String solrHome = new FileSystemResource("/home/francois/dev/daobs/web/src/main/config/").getPath();



        initCore(configFilePath, schemaFilePath, solrHome, "data");

        super.setUp();

        server = new EmbeddedSolrServer(
                h.getCoreContainer(),
                h.getCore().getName()
        );


        initBeans();
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
}
