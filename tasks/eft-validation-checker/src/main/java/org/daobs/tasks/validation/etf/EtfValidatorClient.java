package org.daobs.tasks.validation.etf;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.Arrays;
import java.util.Scanner;

/**
 * Simple Java Client for the ETF Validator Tool.
 *
 * @author Jose García
 */
public class EtfValidatorClient {
    String etfResourceTesterPath;

    public String getEtfResourceTesterPath() {
        return etfResourceTesterPath;
    }

    public void setEtfResourceTesterPath(String etfResourceTesterPath) {
        this.etfResourceTesterPath = etfResourceTesterPath;
    }

    public EtfValidatorClient(String etfResourceTesterPath) {
        this.etfResourceTesterPath = etfResourceTesterPath;
    }

    public EtfValidationReport validate(String resourceDescriptorUrl,
                                        String resourceDescriptorProtocol) {
        // Invoke ETF tool
        String eftResultsPath = executeEtfTool(resourceDescriptorUrl, resourceDescriptorProtocol);
        File eftResults = new File(eftResultsPath, "TESTS-TestSuites.xml");

        // Build report
        EtfValidationReport report = new EftValidationReportBuilder()
                .build(eftResults, resourceDescriptorProtocol, resourceDescriptorProtocol);

        return report;
    }

    private String executeEtfTool(String resourceDescriptorUrl,
                                  String resourceDescriptorProtocol) {

        // TODO: Create a temporal folder name for the report
        String reportName = "report";


        try {
            FileUtils.cleanDirectory(new File(this.etfResourceTesterPath, "reports"));

            Runtime rt = Runtime.getRuntime();

            String[] envp = new String[1];
            envp[0] = "XTF_SEL_GROOVY=" + this.etfResourceTesterPath + "/ETF/Groovy";

            String command = commandToExecute(resourceDescriptorUrl, resourceDescriptorProtocol, reportName);
            // TODO: Handle this case
            if (StringUtils.isEmpty(command)) return "";

            Process pr = rt.exec(command,
                    envp,
                    new File(this.etfResourceTesterPath));

            // retrieve output from python script
            BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = "";
            while((line = bfr.readLine()) != null) {
                // display each output line form python script
                System.out.println(line);
            }

            bfr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            line = "";
            while((line = bfr.readLine()) != null) {
                // display each output line form python script
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            System.out.println("Process exitValue: " + exitVal);

            // TODO: Change this, the report folder should be configurable
            String[] directories = new File(this.etfResourceTesterPath, "reports").list();
            reportName = directories[0];

            String eftResultsPath = this.etfResourceTesterPath + "/reports/" + reportName;
            return eftResultsPath;

        } catch (Throwable ex) {
            ex.printStackTrace();
            return "";
        }

    }



    /**
     * Creates the command to execute for the link report.
     *
     * @param resourceDescriptorUrl
     * @param resourceDescriptorProtocol
     * @return
     */
    private String commandToExecute(String resourceDescriptorUrl,
                                    String resourceDescriptorProtocol,
                                    String reportName) {

        String command = "ant ";

        if (resourceDescriptorProtocol.equalsIgnoreCase("OGC:WMS")) {
            command = command + " run-vs-tests -DVS.serviceEndpoint=" + resourceDescriptorUrl;

        } else if (resourceDescriptorProtocol.equalsIgnoreCase("OGC:WMTS")) {
            command = command + " run-wmts-tests -DVS.serviceEndpoint=" + resourceDescriptorUrl;

        } else if (resourceDescriptorProtocol.equalsIgnoreCase("OGC:WFS")) {
            command = command + " run-dswfs-tests -DDS.serviceEndpoint=" + resourceDescriptorUrl;

        } else if (resourceDescriptorProtocol.equalsIgnoreCase("INSPIRE Atom")) {
            command = command + " run-dsatom-tests -DDS.serviceEndpoint=" + resourceDescriptorUrl;

        } else {
            return "";
        }

        return command;
    }
}