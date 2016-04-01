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
