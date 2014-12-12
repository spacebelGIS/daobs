package org.daobs.solr;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
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
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DataImportTests extends AbstractSolrDaobsTestCase {

    @Test
    public void testXSLTMetadataImport() throws Exception {

        File file = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("metadata_basic.xml").toURI());
        String xml = Files.toString(file, Charsets.UTF_8);


        Map<String, String> args = new HashMap<String, String>();
        args.put(CommonParams.TR, "metadata-iso19139.xsl");
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

        String response = sw.toString();

        assertU(response);
        assertU(commit());
        assertQ("test document was correctly committed", req("q", "*:*")
                , "//result[@numFound='1']"
                , "//str[@name='id'][.='81aea739-4d21-427d-bec4-082cb64b825b']"
        );
    }

}
