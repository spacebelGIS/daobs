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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Simple Java Client for the ETF Validator Tool.
 *
 * @author Jose García
 */
public class EtfValidatorClient {
  String etfResourceTesterPath;
  String etfResourceTesterHtmlReportsPath;
  String etfResourceTesterHtmlReportsUrl;
  private Log log = LogFactory.getLog(this.getClass());

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  private int timeout = 1;

  /**
   * ETF validator client.
     */
  public EtfValidatorClient(String etfResourceTesterPath,
                            String etfResourceTesterHtmlReportsPath,
                            String etfResourceTesterHtmlReportsUrl,
                            int timeout) {

    this.etfResourceTesterPath = etfResourceTesterPath;
    this.etfResourceTesterHtmlReportsPath = etfResourceTesterHtmlReportsPath;
    this.etfResourceTesterHtmlReportsUrl = etfResourceTesterHtmlReportsUrl;
    this.timeout = timeout;
  }

  public String getEtfResourceTesterPath() {
    return etfResourceTesterPath;
  }

  public void setEtfResourceTesterPath(String etfResourceTesterPath) {
    this.etfResourceTesterPath = etfResourceTesterPath;
  }

  public String getEtfResourceTesterHtmlReportsPath() {
    return etfResourceTesterHtmlReportsPath;
  }

  public void setEtfResourceTesterHtmlReportsPath(String etfResourceTesterHtmlReportsPath) {
    this.etfResourceTesterHtmlReportsPath = etfResourceTesterHtmlReportsPath;
  }

  public String getEtfResourceTesterHtmlReportsUrl() {
    return etfResourceTesterHtmlReportsUrl;
  }

  public void setEtfResourceTesterHtmlReportsUrl(String etfResourceTesterHtmlReportsUrl) {
    this.etfResourceTesterHtmlReportsUrl = etfResourceTesterHtmlReportsUrl;
  }

  /**
   * Validates a resource url with ETF tool.
   * <p>
   * Note that declared protocol values are used as a reference in the ServiceProtocolChecker
   * and values are not standarized.
   * </p>
   *
   * @param declaredProtocol  Declared protocol for the resource url.
   */
  public EtfValidationReport validate(String resourceDescriptorUrl,
                                      ServiceType serviceType,
                                      String declaredProtocol) {

    log.info("Validating link=" + resourceDescriptorUrl + ", serviceType=" + serviceType);

    ServiceProtocolChecker protocolChecker =
        new ServiceProtocolChecker(resourceDescriptorUrl, serviceType, declaredProtocol);

    ServiceProtocol protocol = protocolChecker.check();
    if (protocol == null) {
      String message = protocolChecker.getErrorMessage();

      EtfValidationReport report = new EftValidationReportBuilder()
          .buildErrorReport(resourceDescriptorUrl, message);

      return report;
    }


    String eftResultsPath = "";
    try {
      // Invoke ETF tool
      eftResultsPath = executeEtfTool(resourceDescriptorUrl, protocol);
      File eftResults = new File(eftResultsPath, "TESTS-TestSuites.xml");

      if (!eftResults.exists()) {
        String message = "Can't find ETF validation report for " + resourceDescriptorUrl
            + " (serviceType=" + serviceType.toString() + ").";

        EtfValidationReport report = new EftValidationReportBuilder()
            .buildErrorReport(resourceDescriptorUrl, message);

        return report;
      }

      // Build report
      String reportUrl = this.etfResourceTesterHtmlReportsUrl + "/"
          + FilenameUtils.getName(eftResultsPath) + "/index.html";
      EtfValidationReport report = new EftValidationReportBuilder()
          .build(eftResults, resourceDescriptorUrl, protocol, reportUrl);

      return report;
    } finally {
      // Cleanup report from ETF folder
      if (StringUtils.isNotEmpty(eftResultsPath)) {
        FileUtils.deleteQuietly(new File(eftResultsPath));
      }
    }
  }

  private String executeEtfTool(String resourceDescriptorUrl,
                                ServiceProtocol protocol) {

    String reportName = getReportName();
    Path tmpDir = null;

    try {
      // Create a custom config.properties and temporary directory,
      // to allow parallel executions of ETF.
      FileUtils.copyFile(new File(this.etfResourceTesterPath, "config.properties"),
          new File(this.etfResourceTesterPath, "config-" + reportName + ".properties"));

      tmpDir = Files.createTempDirectory("ETF");

      String[] envp = new String[2];
      envp[0] = "XTF_SEL_GROOVY=" + this.etfResourceTesterPath + "/ETF/Groovy";
      envp[1] = "ETF_SEL_GROOVY=" + this.etfResourceTesterPath + "/ETF/Groovy";

      String command = commandToExecute(resourceDescriptorUrl, protocol,
          reportName, tmpDir.toFile().getAbsolutePath());
      if (StringUtils.isEmpty(command)) {
        return "";
      }

      log.info("EFT validation command: " + command);

      Runtime rt = Runtime.getRuntime();
      Process pr = rt.exec(command,
          envp,
          new File(this.etfResourceTesterPath));

      if (log.isDebugEnabled()) {
        // Log process ouput
        BufferedReader bfr = null;
        String line = "";

        try {
          bfr = new BufferedReader(new InputStreamReader(
              pr.getInputStream(), Charset.forName("UTF8")));
          while ((line = bfr.readLine()) != null) {
            log.debug(line);
          }

        } finally {
          IOUtils.closeQuietly(bfr);
        }

        try {
          bfr = new BufferedReader(new InputStreamReader(
              pr.getErrorStream(), Charset.forName("UTF8")));
          line = "";
          while ((line = bfr.readLine()) != null) {
            log.debug(line);
          }
        } finally {
          IOUtils.closeQuietly(bfr);
        }

      }

      if (!pr.waitFor(timeout, TimeUnit.MINUTES)) {
        pr.destroy(); // consider using destroyForcibly instead
        log.warn(String.format("Process killed after %d minutes.", timeout));
      }
      int exitVal = pr.exitValue();
      log.info("Process exitValue: " + exitVal);

      String eftResultsPath = this.etfResourceTesterPath + "/reports/" + reportName;

      // Move the html folder to the html reports folder, accessible with http
      FileUtils.moveDirectory(new File(eftResultsPath, "html"),
          new File(this.etfResourceTesterHtmlReportsPath, reportName));

      return eftResultsPath;

    } catch (Throwable ex) {
      log.error(ex.getMessage());
      return "";

    } finally {
      // Clean up temporary folders
      cleanTemporaryFolders(reportName, tmpDir);
    }

  }


  /**
   * Creates the command to execute for the link report.
   *
   */
  private String commandToExecute(String resourceDescriptorUrl,
                                  ServiceProtocol protocol,
                                  String reportName,
                                  String tmpDir) {

    String command = this.etfResourceTesterPath
        + "/ant set-serviceEndpoint {0} "
        + "-Dmap={1} -DmapName={2} -DtmpDir={3} -DconfigurationFile={4}";

    String[] args = new String[]{resourceDescriptorUrl,
        reportName, tmpDir, "config-" + reportName + ".properties"};
    List<String> argsList = new ArrayList<String>(Arrays.asList(args));

    if (protocol.equals(ServiceProtocol.WMS)) {
      argsList.add(0, "run-vs-tests");

    } else if (protocol.equals(ServiceProtocol.WMTS)) {
      argsList.add(0, "run-wmts-tests");

    } else if (protocol.equals(ServiceProtocol.WFS)) {
      argsList.add(0, "run-dswfs-tests");

    } else if (protocol.equals(ServiceProtocol.ATOM)) {
      argsList.add(0, "run-dsatom-tests");

    } else {
      return "";
    }

    command = MessageFormat.format(command, argsList.toArray());

    return command;
  }


  /**
   * Generates a name for the report folder.
   *
   * @return Report name.
   */
  private String getReportName() {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    return "report-" + format.format(new Date());
  }


  /**
   * Removes the temporary folders/files created by ETF.
   *
   */
  private void cleanTemporaryFolders(String reportName, Path tmpDir) {
    if (tmpDir != null) {
      FileUtils.deleteQuietly(tmpDir.toFile());
    }

    FileUtils.deleteQuietly(new File(
        this.etfResourceTesterPath,
        "config-" + reportName + ".properties"));
  }
}
