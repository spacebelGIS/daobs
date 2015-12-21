package org.daobs.tasks.validation.etf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Simple Java Client for the ETF Validator Tool.
 *
 * @author Jose García
 */
public class EtfValidatorClient {
    private Log log = LogFactory.getLog(this.getClass());

    String etfResourceTesterPath;
    String etfResourceTesterHtmlReportsPath;
    String etfResourceTesterHtmlReportsUrl;

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


    public EtfValidatorClient(String etfResourceTesterPath,
                              String etfResourceTesterHtmlReportsPath,
                              String etfResourceTesterHtmlReportsUrl) {

        this.etfResourceTesterPath = etfResourceTesterPath;
        this.etfResourceTesterHtmlReportsPath = etfResourceTesterHtmlReportsPath;
        this.etfResourceTesterHtmlReportsUrl = etfResourceTesterHtmlReportsUrl;
    }

    /**
     * Validates a resource url with ETF tool.
     *
     * Note that declared protocol values are used as a reference in the ServiceProtocolChecker
     * and values are not standarized.
     *
     * @param resourceDescriptorUrl
     * @param serviceType
     * @param declaredProtocol  Declared protocol for the resource url.
     * @return
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
                String message = "Can't find ETF validation report for " + resourceDescriptorUrl +
                        " (serviceType=" + serviceType.toString() +  ").";

                EtfValidationReport report = new EftValidationReportBuilder()
                        .buildErrorReport(resourceDescriptorUrl, message);

                return report;
            }

            // Build report
            String reportUrl = this.etfResourceTesterHtmlReportsUrl + "/" + FilenameUtils.getName(eftResultsPath) + "/index.html";
            EtfValidationReport report = new EftValidationReportBuilder()
                    .build(eftResults, resourceDescriptorUrl, protocol, reportUrl);

            return report;
        } finally {
            // Cleanup report from ETF folder
            if (StringUtils.isNotEmpty(eftResultsPath)) FileUtils.deleteQuietly(new File(eftResultsPath));

        }
    }

    private String executeEtfTool(String resourceDescriptorUrl,
                                  ServiceProtocol protocol) {

        String reportName = getReportName();
        Path tmpDir = null;

        try {
            // Create a custom config.properties and temporary directory, to allow parallel executions of ETF.
            FileUtils.copyFile(new File(this.etfResourceTesterPath, "config.properties"),
                    new File(this.etfResourceTesterPath, "config-" + reportName + ".properties"));

            tmpDir = Files.createTempDirectory("ETF");

            Runtime rt = Runtime.getRuntime();

            String[] envp = new String[1];
            envp[0] = "XTF_SEL_GROOVY=" + this.etfResourceTesterPath + "/ETF/Groovy";

            String command = commandToExecute(resourceDescriptorUrl, protocol,
                    reportName, tmpDir.toFile().getAbsolutePath());
            if (StringUtils.isEmpty(command)) return "";

            log.info("EFT validation command: " + command);

            Process pr = rt.exec(command,
                    envp,
                    new File(this.etfResourceTesterPath));

            if (log.isDebugEnabled()) {
                // Log process ouput
                BufferedReader bfr = null;
                String line = "";

                try {
                    bfr = new BufferedReader(new InputStreamReader(pr.getInputStream(), Charset.forName("UTF8")));
                    while((line = bfr.readLine()) != null) {
                        log.debug(line);
                    }

                } finally {
                    IOUtils.closeQuietly(bfr);
                }

                try {
                    bfr = new BufferedReader(new InputStreamReader(pr.getErrorStream(), Charset.forName("UTF8")));
                    line = "";
                    while((line = bfr.readLine()) != null) {
                        log.debug(line);
                    }
                } finally {
                    IOUtils.closeQuietly(bfr);
                }

            }

            int exitVal = pr.waitFor();
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
     * @param resourceDescriptorUrl
     * @param protocol
     * @param reportName
     * @param tmpDir
     * @return
     */
    private String commandToExecute(String resourceDescriptorUrl,
                                    ServiceProtocol protocol,
                                    String reportName,
                                    String tmpDir) {

        String command = "ant set-serviceEndpoint {0} -Dmap={1} -DmapName={2} -DtmpDir={3} -DconfigurationFile={4}";

        String args[] = new String[]{resourceDescriptorUrl, reportName, tmpDir, "config-" + reportName + ".properties"};
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

        FileUtils.deleteQuietly(new File(this.etfResourceTesterPath, "config-" + reportName + ".properties"));
    }
}