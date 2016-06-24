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

package org.daobs.tasks.validation.etf;

import junit.framework.TestCase;


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

    validator = new EtfValidatorClient("./ETF/ETF", "./ETF/ETF/etf-html-reports",
      "http://localhost/validation-reports", 5);
  }

  @org.junit.Test
  public void testValidateValidService() throws Exception {
    String resourceDescriptorUrl = "http://services.rce.geovoorziening.nl/rce/wms?";

    EtfValidationReport report = validator.validate(resourceDescriptorUrl, ServiceType.View, "OGC:WMS");
    assertNotNull(report);
    //assertEquals(report.getCompletenessIndicator(), 100.0);
  }

  @org.junit.Test
  public void testValidateNonValidService() throws Exception {
    String resourceDescriptorUrl = "http://maps.waterschapservices.nl/wms/inspire?";

    EtfValidationReport report = validator.validate(resourceDescriptorUrl, ServiceType.View, "OGC:WMS");
    assertNotNull(report);
    //assertEquals(report.getCompletenessIndicator(), 100.0);
  }

}
