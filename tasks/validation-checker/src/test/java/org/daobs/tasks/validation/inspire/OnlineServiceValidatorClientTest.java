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
        .setInspireResourceTesterUrl(inspireResourceTesterURL);
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
    assertEquals(89, Math.round(report.getCompletenessIndicator()));
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
    assertEquals(201, report.getHttpStatus());
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
    assertEquals(201, report.getHttpStatus());
    assertTrue("Report contains GeoportalExceptionMessage.",
      report.getReport().contains("GeoportalExceptionMessage"));
  }


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
//        System.out.println("Invalid fast: " + report.getTotalTimeSeconds());

    validator.setDontGenerateHtmlFiles(false);
    validator.setDontGenerateLayerPreviews(false);
    validator.setProbeDataResourceLocators(false);
    report = validator.validate(xml, false);
//        System.out.println("Invalid slow: " + report.getTotalTimeSeconds());


    file = new File(Thread.currentThread()
      .getContextClassLoader()
      .getResource("inspirevalid.xml").toURI());
    xml = Files.toString(file, Charsets.UTF_8);
    validator.setDontGenerateHtmlFiles(true);
    validator.setDontGenerateLayerPreviews(true);
    validator.setProbeDataResourceLocators(true);
    report = validator.validate(xml, false);
//        System.out.println("Valid fast: " + report.getTotalTimeSeconds());

    validator.setDontGenerateHtmlFiles(false);
    validator.setDontGenerateLayerPreviews(false);
    validator.setProbeDataResourceLocators(false);
    report = validator.validate(xml, false);
//        System.out.println("Valid slow: " + report.getTotalTimeSeconds());


    assertNotNull(report);
    assertEquals(201, report.getHttpStatus());
  }
}
