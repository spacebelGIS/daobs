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
    private int totalErrorsOptional;
    private int totalFailuresOptional;
    private int totalTestsOptional;
    private double totalTimeOptional;
    private boolean validationFailed;

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
    private String reportUrl;

    public EtfValidationReport(String endPoint, String protocol) {
        this.endPoint = endPoint;
        this.protocol = protocol;
        this.validationFailed = false;
    }

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

    public int getTotalErrorsOptional() {
        return totalErrorsOptional;
    }

    public void setTotalErrorsOptional(int totalErrorsOptional) {
        this.totalErrorsOptional = totalErrorsOptional;
    }

    public int getTotalFailuresOptional() {
        return totalFailuresOptional;
    }

    public void setTotalFailuresOptional(int totalFailuresOptional) {
        this.totalFailuresOptional = totalFailuresOptional;
    }

    public int getTotalTestsOptional() {
        return totalTestsOptional;
    }

    public void setTotalTestsOptional(int totalTestsOptional) {
        this.totalTestsOptional = totalTestsOptional;
    }

    public double getTotalTimeOptional() {
        return totalTimeOptional;
    }

    public void setTotalTimeOptional(double totalTimeOptional) {
        this.totalTimeOptional = totalTimeOptional;
    }

    /**
     * The validation status for mandatory tests.
     */
    public boolean isStatus() {
        if (totalTests == 0) {
            return false;
        } else {
            return ((getTotalErrors() + getTotalFailures()) == 0);
        }
    }

    /**
     * The validation status for optional tests.
     */
    public boolean isStatusOptional() {
        if (totalTestsOptional == 0) {
            return false;
        } else {
            return ((getTotalErrorsOptional() + getTotalFailuresOptional()) == 0);
        }
    }

    /**
     * Completeness indicator for mandatory tests.
     *
     * @return -1 if no tests are available, otherwise the indicator value rounded to 2 decimals.
     */
    public double getCompletenessIndicator() {
        if (totalTests == 0) return -1;

        double indicator = 100 - (((totalErrors + totalFailures) * 1.0 / totalTests) * 100);
        return (double) (Math.round(indicator * 100)) / 100;
    }

    /**
     * Completeness indicator for optional tests.
     *
     * @return -1 if no tests are available, otherwise the indicator value rounded to 2 decimals.
     */
    public double getCompletenessIndicatorOptional() {
        if (totalTestsOptional == 0) return -1;

        double indicator = 100 - (((totalErrorsOptional + totalFailuresOptional) * 1.0 / totalTestsOptional) * 100);
        return (double) (Math.round(indicator * 100)) / 100;
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

    public boolean isValidationFailed() {
        return validationFailed;
    }

    public void setValidationFailed(boolean validationFailed) {
        this.validationFailed = validationFailed;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("ETF Validation report:");
        buffer.append("\nValid (mandatory): ").append(this.isStatus());
        buffer.append("\nValid (optional): ").append(this.isStatusOptional());
        buffer.append("\nCompleteness indicator (mandatory): ").append(this.getCompletenessIndicator());
        buffer.append("\nCompleteness indicator (optional): ").append(this.getCompletenessIndicatorOptional());
        buffer.append("\nReport: ").append(this.getReport());
        buffer.append("\nReport URL: ").append(this.getReportUrl());
        buffer.append("\nValidation failed: ").append(this.isValidationFailed());
        return buffer.toString();
    }
}
