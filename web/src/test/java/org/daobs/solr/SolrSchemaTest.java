package org.daobs.solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 *
 */
@Ignore("not ready yet")
public class SolrSchemaTest extends AbstractSolrDaobsTestCase {

    @Test
    public void testThatNoResultsAreReturned() throws SolrServerException {
        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = null;
        try {
            response = server.query(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(0L, response.getResults().getNumFound());
    }

    @Test
    public void testThatMetadataDocumentIsFound() throws SolrServerException, IOException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "1");
        document.addField("documentType", "metadata");

        server.add(document);
        server.commit();

        SolrParams params = new SolrQuery("+documentType:metadata");
        QueryResponse response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        assertEquals("1", response.getResults().get(0).get("id"));
    }
}
