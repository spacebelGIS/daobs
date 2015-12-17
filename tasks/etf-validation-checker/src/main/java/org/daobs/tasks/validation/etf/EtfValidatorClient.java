package org.daobs.tasks.validation.etf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

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

        try {
            Runtime rt = Runtime.getRuntime();

            String[] envp = new String[1];
            envp[0] = "XTF_SEL_GROOVY=" + this.etfResourceTesterPath + "/ETF/Groovy";

            String command = commandToExecute(resourceDescriptorUrl, protocol, reportName);
            if (StringUtils.isEmpty(command)) return "";

            log.info("EFT validation command: " + command);

            Process pr = rt.exec(command,
                    envp,
                    new File(this.etfResourceTesterPath));

            if (log.isDebugEnabled()) {
                // Log process ouput
                BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));

                String line = "";
                while((line = bfr.readLine()) != null) {
                    log.debug(line);
                }

                bfr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                line = "";
                while((line = bfr.readLine()) != null) {
                    log.debug(line);
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
            cleanTemporaryFolders(reportName);
        }

    }



    /**
     * Creates the command to execute for the link report.
     *
     * @param resourceDescriptorUrl
     * @param protocol
     * @param reportName
     * @return
     */
    private String commandToExecute(String resourceDescriptorUrl,
                                    ServiceProtocol protocol,
                                    String reportName) {

        String command = "ant ";

        if (protocol.equals(ServiceProtocol.WMS)) {
            command = command + "set-serviceEndpoint run-vs-tests -Dmap=" + resourceDescriptorUrl + " -DmapName=" + reportName;

        } else if (protocol.equals(ServiceProtocol.WMTS)) {
            command = command + "set-serviceEndpoint run-wmts-tests -Dmap=" + resourceDescriptorUrl + " -DmapName=" + reportName;

        } else if (protocol.equals(ServiceProtocol.WFS)) {
            command = command + "set-serviceEndpoint run-dswfs-tests -Dmap=" + resourceDescriptorUrl + " -DmapName=" + reportName;

        } else if (protocol.equals(ServiceProtocol.ATOM)) {
            command = command + "set-serviceEndpoint run-dsatom-tests -Dmap=" + resourceDescriptorUrl + " -DmapName=" + reportName;

        } else {
            return "";
        }

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
     * Removes the temporary folders created by ETF.
     *
     */
    private void cleanTemporaryFolders(String reportName) {
        try {
            String tempFolder =  System.getProperty("java.io.tmpdir");

            FileFilter directoryFilter = new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory() && file.getName().startsWith("xtf_sel_");
                }
            };

            File[] files = new File(tempFolder).listFiles(directoryFilter);
            for (File dir : files) {
                FileUtils.deleteQuietly(dir);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}