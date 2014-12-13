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

        String fileToLoad = "metadata_basic.xml";
        String response = loadMetadata(fileToLoad);

        assertU(response);
        assertU(commit());
        assertQ("test metadata document was correctly indexed", req("q", "*:*")
                , "//result[@numFound='1']"
                , "//str[@name='id'][.='81aea739-4d21-427d-bec4-082cb64b825b']"
                , "//str[@name='metadataIdentifier'][.='81aea739-4d21-427d-bec4-082cb64b825b']"
                , "//str[@name='resourceTitle'][.='Urban Atlas - Spain - Santander']"
                , "//str[@name='resourceAbstract'][.='The Urban Atlas is providing pan-European comparable land use and land cover data for Large Urban Zones with more than 100.000 inhabitants as defined by the Urban Audit. Urban Atlas mission is to provide high-resolution hotspot mapping of changes in urban spaces and indicators for users such as city governments, the European Environment Agency (EEA) and European Commission departments.']"
                , "//str[@name='documentType'][.='metadata']"
                , "//str[@name='documentStandard'][.='iso19139']"
                , "//str[@name='territory'][.='']"
                , "//str[@name='harvesterId'][.='']"
                , "//date[@name='dateStamp'][.='2014-12-09T08:52:17Z']"
                , "//str[@name='mainLanguage'][.='eng']"
                , "//arr[@name='resourceType']/str[.='dataset']"
                , "//arr[@name='publicationDateForResource']/str[.='2010-05-28']"
                , "//arr[@name='creationDateForResource']/str[.='2010-05-28']"
                // not stored , "//arr[@name='presentationForm']/str[.='2010']"
                // not stored , "//arr[@name='publicationMonthForResource']/str[.='2010-05']"
                , "//arr[@name='spatialRepresentationType']/str[.='vector']"
                , "//arr[@name='overviewUrl']/str[.='http://sdi.eea.europa.eu/public/catalogue-graphic-overview/c8ecdabf-2e71-4d0b-b27d-9d409ce8cb6f.png']"
                , "//arr[@name='resourceLanguage']/str[.='eng']"
                , "//arr[@name='inspireTheme_syn']/str[.='Land use']"
//                , "//arr[@name='inspireTheme']/str[.='Land use']"
                , "//arr[@name='inspireAnnex']/str[.='iii']"
                , "//arr[@name='tag']/str[.='urban area']"
                , "//arr[@name='tag']/str[.='Land use']"
                , "//arr[@name='tag']/str[.='Spain']"
//                , "//arr[@name='geoTag']/str[.='Spain']"
                , "//arr[@name='topic']/str[.='society']"
                , "//arr[@name='coordinateSystem']/str[.='http://www.opengis.net/def/crs/EPSG/0/4936']"
                , "//arr[@name='accessConstraints']/str[.='otherRestrictions']"
                , "//arr[@name='otherConstraints']/str[.='no limitations']"
                , "//arr[@name='useLimitation']/str[.='EEA standard re-use policy: unless otherwise indicated, re-use of content on the EEA website for commercial or non-commercial purposes is permitted free of charge, provided that the source is acknowledged (http://www.eea.europa.eu/legal/copyright). Copyright holder: Directorate-General Enterprise and Industry.']"
                , "//arr[@name='resolutionScaleDenominator']/str[.='10000']"
                , "//int[@name='numberOfInspireTheme'][.='1']"
                , "//arr[@name='inspireConformResource']/bool[.='true']"
                , "//arr[@name='lineage']/str[.='Earth Observation (EO) Data used: * Spot 5 2,50 m - 50322620610131130401B7 (PS) (Date: 2006/10/13)']"

        );
        // harvestedDate
        // constraintClassification
        // resolutionDistance
        // presentationForm
        // otherLanguage
        // Contact
    }

    /**
     * Load an ISO19139 document.
     *
     * @param fileToLoad
     * @return
     * @throws Exception
     */
    private String loadMetadata(String fileToLoad) throws Exception {
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
    private String loadReporting(String fileToLoad) throws Exception {
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
    private String loadData(String fileToLoad, String xsltTransformation) throws Exception {
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
