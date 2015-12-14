package org.daobs.tasks.validation.etf;


/**
 * ETF Validation report.
 *
 * @author Jose García
 */
public class EtfValidationReport {

    private String endPoint;

    private String protocol;

    private int totalErrors;

    private int totalFailures;

    private int totalTests;

    private double totalTime;

    /**
     * Validation report. Could be an exception message
     * or an XML document depending on the validator.
     */
    private String report;

    /**
     * Extra information provided by the validator.
     */
    private String info;

    /**
     * Url to the html report.
     * @return
     */
    private  String reportUrl;

    public String getEndPoint() {
        return endPoint;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public int getTotalFailures() {
        return totalFailures;
    }

    public void setTotalFailures(int totalFailures) {
        this.totalFailures = totalFailures;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * The validation status.
     */
    public boolean isStatus() {
        return ((getTotalErrors() + getTotalFailures()) == 0);
    }


    public double getCompletenessIndicator() {
        return 100 - (((totalErrors + totalFailures) * 1.0 / totalTests) * 100);
    }


    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public EtfValidationReport(String endPoint, String protocol) {
        this.endPoint = endPoint;
        this.protocol = protocol;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("ETF Validation report:");
        buffer.append("\nValid: ").append(this.isStatus());
        buffer.append("\nCompleteness indicator: ").append(this.getCompletenessIndicator());
        buffer.append("\nReport: ").append(this.getReport());
        buffer.append("\nReport URL: ").append(this.getReportUrl());
        return buffer.toString();
    }
}
