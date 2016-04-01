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
package org.daobs.tasks.validation.inspire;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import junit.framework.TestCase;

import java.io.File;

public class OnlineServiceValidatorClientTest extends TestCase {

    private OnlineServiceValidatorClient validator;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        String inspireResourceTesterURL =
            "http://inspire-geoportal.ec.europa.eu/GeoportalProxyWebServices/resources/INSPIREResourceTester";

        validator =
            new OnlineServiceValidatorClient()
                .setInspireResourceTesterURL(inspireResourceTesterURL);
    }

    /**
     * Validate document by file upload
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testValidateFileByUpload() throws Exception {
        File file = new File(Thread.currentThread().getContextClassLoader().getResource("inspirevalid.xml").toURI());

        ValidationReport report = validator.validate(file);
        assertNotNull(report);
        assertEquals(report.getCompletenessIndicator(), 100.0);
    }

    /**
     * Validate invalid document by file upload
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testValidateInvalidFileByUpload() throws Exception {
        File file = new File(Thread.currentThread()
            .getContextClassLoader()
            .getResource("invalid.xml").toURI());

        ValidationReport report = validator.validate(file);
        assertNotNull(report);
        assertEquals(201, report.getHTTPStatus());
        assertTrue("Report contains GeoportalExceptionMessage.",
            report.getReport().contains("GeoportalExceptionMessage"));
    }

    @org.junit.Test
    public void testValidatePostContent() throws Exception {

        // Invalid document
        File file = new File(Thread.currentThread()
            .getContextClassLoader()
            .getResource("invalid.xml").toURI());

        String xml = Files.toString(file, Charsets.UTF_8);
        ValidationReport report = validator.validate(xml, false);
        assertNotNull(report);
        assertEquals(201, report.getHTTPStatus());
        assertTrue("Report contains GeoportalExceptionMessage.",
            report.getReport().contains("GeoportalExceptionMessage"));
    }


    @org.junit.Test
    public void testValidateService() throws Exception {

        // Invalid document
        File file = new File(Thread.currentThread()
            .getContextClassLoader()
            .getResource("service.xml").toURI());

        String xml = Files.toString(file, Charsets.UTF_8);
        validator.setProbeDataResourceLocators(false);
        validator.setProbeNetworkServices(false);
        ValidationReport report = validator.validate(xml, false);
        assertNotNull(report);
        System.out.println("Service with no probe: " + report.getTotalTimeSeconds());

        validator.setProbeDataResourceLocators(true);
        validator.setProbeNetworkServices(true);
        report = validator.validate(xml, false);
        assertNotNull(report);
        System.out.println("Service with probe: " + report.getTotalTimeSeconds());

    }

    /*@Ignore
    @Test
    public void testValidateZipFile() throws Exception {
        File file = new File(getClass()
                .getResource("inspirevalid.xml.zip").toURI());

        ValidationReport report = validator.validate(file);
        report.toString();
        assertNotNull(report);
        assertEquals(201, report.getHTTPStatus());
        assertTrue("Report contains GeoportalExceptionMessage.",
                report.getReport().contains("GeoportalExceptionMessage"));
    }*/


    /**
     * Test to investigate performance
     * @throws Exception
     */
    @org.junit.Test
    public void testValidatePostContentWithOptions() throws Exception {

        // Invalid document
        File file = new File(Thread.currentThread()
            .getContextClassLoader()
            .getResource("invalid.xml").toURI());
        String xml = Files.toString(file, Charsets.UTF_8);
        ValidationReport report = validator.validate(xml, false);
        System.out.println("Invalid fast: " + report.getTotalTimeSeconds());

        validator.setDontGenerateHtmlFiles(false);
        validator.setDontGenerateLayerPreviews(false);
        validator.setProbeDataResourceLocators(false);
        report = validator.validate(xml, false);
        System.out.println("Invalid slow: " + report.getTotalTimeSeconds());


        file = new File(Thread.currentThread()
            .getContextClassLoader()
            .getResource("inspirevalid.xml").toURI());
        xml = Files.toString(file, Charsets.UTF_8);
        validator.setDontGenerateHtmlFiles(true);
        validator.setDontGenerateLayerPreviews(true);
        validator.setProbeDataResourceLocators(true);
        report = validator.validate(xml, false);
        System.out.println("Valid fast: " + report.getTotalTimeSeconds());

        validator.setDontGenerateHtmlFiles(false);
        validator.setDontGenerateLayerPreviews(false);
        validator.setProbeDataResourceLocators(false);
        report = validator.validate(xml, false);
        System.out.println("Valid slow: " + report.getTotalTimeSeconds());


        assertNotNull(report);
        assertEquals(201, report.getHTTPStatus());
//        assertTrue("Report contains GeoportalExceptionMessage.",
//                report.getReport().contains("GeoportalExceptionMessage"));
    }


    /*@Ignore
    @Test
    public void testDomException() throws Exception {

        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                String testFilePath = getClass()
                        .getResource("test").getPath();
                String testXSDPath = getClass()
                        .getResource("/schemas/iso19139/schema.xsd").getPath();

                from("file://" + testFilePath)
//                        .filter().xpath("//doc")
                        .setBody().xpath("//doc/str[@name = 'document']/text()", String.class)
//                        .setHeader("document").xpath("doc/str[@name = 'document']/text()", String.class)
//                        .log("${header.document}")
//                        .transform().header("document")
                        .log("############# ${body}")
//                        .convertBodyTo(org.w3c.dom.Document.class)
//                        .log("${body}")
                        .to("validator:file:" + testXSDPath + "?useDom=false")
                        .log("############# ${body}");

            }
        });
        context.start();
        Thread.sleep(10000);
        context.stop();
        assert (true);
    }*/

}
