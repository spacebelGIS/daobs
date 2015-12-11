package org.daobs.tasks.validation.inspire;

import junit.framework.TestCase;
import org.daobs.tasks.validation.etf.EtfValidationReport;
import org.daobs.tasks.validation.etf.EtfValidatorClient;
import org.daobs.tasks.validation.etf.ServiceType;


/**
 * Test class for EtfValidatorClient.
 *
 * @author Jose García
 */
public class EtfValidatorClientTest extends TestCase {

    private EtfValidatorClient validator;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        validator = new EtfValidatorClient("./ETF/ETF");
    }

    @org.junit.Test
    public void testValidateValidService() throws Exception {
        String resourceDescriptorUrl = "http://services.rce.geovoorziening.nl/rce/wms?";

        EtfValidationReport report = validator.validate(resourceDescriptorUrl, ServiceType.View);
        assertNotNull(report);
        //assertEquals(report.getCompletenessIndicator(), 100.0);
    }

    @org.junit.Test
    public void testValidateNonValidService() throws Exception {
        String resourceDescriptorUrl = "http://maps.waterschapservices.nl/wms/inspire?";

        EtfValidationReport report = validator.validate(resourceDescriptorUrl, ServiceType.View);
        assertNotNull(report);
        //assertEquals(report.getCompletenessIndicator(), 100.0);
    }

}