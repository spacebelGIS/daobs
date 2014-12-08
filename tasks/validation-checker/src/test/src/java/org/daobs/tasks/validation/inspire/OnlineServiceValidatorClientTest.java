package org.daobs.tasks.validation.inspire;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OnlineServiceValidatorClientTest extends TestCase {

    @org.junit.Test
    public void testValidate() throws Exception {

        String inspireResourceTesterURL =
                "http://inspire-geoportal.ec.europa.eu/GeoportalProxyWebServices/resources/INSPIREResourceTester";

        OnlineServiceValidatorClient validator =
            new OnlineServiceValidatorClient()
                    .setInspireResourceTesterURL(inspireResourceTesterURL);

        // Invalid document
        File file = new File("/home/francois/dev/daobs/tasks/validation-checker/src/test/resources/invalid.xml");

        String xml = Files.toString(file, Charsets.UTF_8);
        OnlineServiceValidatorClient.ValidationReport report = validator.validate(xml);

        assertNotNull(report);
        assertEquals(201, report.getStatus());
        assertTrue("Report contains GeoportalExceptionMessage.",
                report.getReport().contains("GeoportalExceptionMessage"));
        System.out.println(report.getInfo());
        System.out.println(report.getReport());
        System.out.println(report.getResultUrl());
    }
}