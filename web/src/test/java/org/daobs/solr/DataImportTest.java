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


import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
@Ignore("not ready yet")
public class DataImportTest extends AbstractSolrDaobsTestCase {

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


    @Test
    public void testXSLTINSPIREReportingImport() throws Exception {
        String fileToLoad = "inspire_indicators.xml";
        String response = loadReporting(fileToLoad);

        assertU(response);
        assertU(commit());
        assertQ("test reporting document was correctly indexed",
                req("q", "documentType:indicator")
                , "//result[@numFound='86']");
    }


    @Test
    public void testXSLTINSPIREAncillaryInformationImport() throws Exception {
        String fileToLoad = "inspire_indicators_with_ai.xml";
        String response = loadReporting(fileToLoad);

        assertU(response);
        assertU(commit());
        assertQ("test ancillary information was correctly indexed",
                req("q", "documentType:ai")
                , "//result[@numFound='7']");

        assertQ("test ancillary information user requests on each service types " +
                        "were correctly indexed at service type level",
                req("q", "id:\"aiuserRequestview2013-12-31T12:00:00Zfi\"")
                , "//result[@numFound='1']"
                , "//str[@name='territory'][.='fi']"
                , "//str[@name='indicatorName'][.='userRequestview']"
                , "//date[@name='reportingDateSubmission'][.='2014-06-05T12:00:00Z']"
                , "//date[@name='reportingDate'][.='2013-12-31T12:00:00Z']"
                , "//str[@name='reportingYear'][.='2013']"
                , "//double[@name='indicatorValue'][.='1000.0']"
        );

        assertQ("test ancillary information user requests on each service types " +
                        "were correctly indexed at record level",
                req("q", "indicatorName:userRequestdiscovery*")
                , "//result[@numFound='2']"
        );

        assertQ("test ancillary information on actual / relevant area were correctly indexed",
                req("q", "id:\"aiactualArea2013-12-31T12:00:00ZfiBB\"")
                , "//result[@numFound='1']"
                , "//str[@name='territory'][.='fi']"
                , "//str[@name='indicatorName'][.='actualArea']"
                , "//date[@name='reportingDateSubmission'][.='2014-06-05T12:00:00Z']"
                , "//date[@name='reportingDate'][.='2013-12-31T12:00:00Z']"
                , "//str[@name='reportingYear'][.='2013']"
                , "//double[@name='indicatorValue'][.='1000.0']"
        );
    }

}
