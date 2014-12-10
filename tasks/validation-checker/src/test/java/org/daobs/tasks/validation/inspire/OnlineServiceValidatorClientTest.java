package org.daobs.tasks.validation.inspire;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import junit.framework.TestCase;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Ignore;
import org.junit.Test;

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
        validator.setDontProbeDataResourceLocators(false);
        report = validator.validate(xml, false);
        System.out.println("Invalid slow: " + report.getTotalTimeSeconds());



        file = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("inspirevalid.xml").toURI());
        xml = Files.toString(file, Charsets.UTF_8);
        validator.setDontGenerateHtmlFiles(true);
        validator.setDontGenerateLayerPreviews(true);
        validator.setDontProbeDataResourceLocators(true);
        report = validator.validate(xml, false);
        System.out.println("Valid fast: " + report.getTotalTimeSeconds());

        validator.setDontGenerateHtmlFiles(false);
        validator.setDontGenerateLayerPreviews(false);
        validator.setDontProbeDataResourceLocators(false);
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