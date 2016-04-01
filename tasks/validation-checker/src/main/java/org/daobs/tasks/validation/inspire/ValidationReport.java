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

import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validation report
 *
 * Created by francois on 08/12/14.
 */
public class ValidationReport {

    private final Pattern completenessPattern = Pattern.compile("CompletenessIndicator>(.*)</.*:CompletenessIndicator");
    /**
     * The validation status.
     */
    boolean status;
    private double threshold = 100.0;
    private boolean isAboveThreshold;
    /**
     * HTTP status code
     */
    private int httpStatus;
    /**
     * Validation report. Could be an exception message
     * or an XML document depending on the validator.
     */
    private String report;
    private Date startTime;
    private Date endTime;
    private double completenessIndicator = -1;
    private double timeWaitingForResponseSeconds;
    private double totalTimeSeconds;
    private StopWatch watch = new StopWatch();
    /**
     * URL of the validation report provided by the validator.
     */
    private String resultUrl;
    /**
     * Extra information provided by the validator.
     */
    private String info;
    ValidationReport(double threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isAboveThreshold() {
        return isAboveThreshold;
    }

    public double getCompletenessIndicator() {
        return completenessIndicator;
    }

    public double getTimeWaitingForResponseSeconds() {
        return timeWaitingForResponseSeconds;
    }

    public double getTotalTimeSeconds() {
        return totalTimeSeconds;
    }

    public boolean getStatus() {
        return status;
    }

    public ValidationReport setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public ValidationReport start() {
        this.startTime = new Date();
        watch.start();
        return this;
    }

    public ValidationReport gotResponse() {
        this.timeWaitingForResponseSeconds = watch.getTotalTimeSeconds();
        return this;
    }

    public ValidationReport stop() {
        this.endTime = new Date();
        watch.stop();
        this.totalTimeSeconds = watch.getTotalTimeSeconds();
        return this;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public ValidationReport setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public ValidationReport setInfo(String info) {
        this.info = info;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getHTTPStatus() {
        return httpStatus;
    }

    public ValidationReport setHTTPStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public String getReport() {
        return report;
    }

    public ValidationReport setReport(String report) {
        this.report = report;

        // Quick hack to retrieve completeness indicator
        // Xpath may be better
        try {
            Matcher matcher = completenessPattern.matcher(report);
            if (matcher.find()) {
                String completeness = matcher.group(1);
                this.completenessIndicator = Double.parseDouble(completeness);
                if (this.completenessIndicator >= 0) {
                    this.isAboveThreshold =
                        this.completenessIndicator >= this.threshold;
                }
            }
        } catch (Exception e) {
            setInfo(getInfo() + ". Exception" + e.getMessage());
        }
        return this;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer("Validation report:");
        buffer.append("\nInfo: ").append(this.getInfo());
        buffer.append("\nTotal time: ").append(this.getTotalTimeSeconds());
        buffer.append("\nCompleteness : ").append(this.getCompletenessIndicator());
        buffer.append("\nResult URL: ").append(this.getResultUrl());
        buffer.append("\nReport: ").append(this.getReport());
        return buffer.toString();
    }
}
