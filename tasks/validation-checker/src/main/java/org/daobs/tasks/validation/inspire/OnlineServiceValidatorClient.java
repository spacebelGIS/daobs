package org.daobs.tasks.validation.inspire;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;


/**
 * Simple Java Client for the INSPIRE Geoportal Validator
 * http://inspire-geoportal.ec.europa.eu/validator2/#
 */
public class OnlineServiceValidatorClient {

    String inspireResourceTesterURL;

    public String getInspireResourceTesterURL() {
        return inspireResourceTesterURL;
    }

    public OnlineServiceValidatorClient setInspireResourceTesterURL(String inspireResourceTesterURL) {
        this.inspireResourceTesterURL = inspireResourceTesterURL;
        return this;
    }

    boolean dontGenerateLayerPreviews = true;


    public boolean isDontGenerateLayerPreviews() {
        return dontGenerateLayerPreviews;
    }

    public void setDontGenerateLayerPreviews(boolean dontGenerateLayerPreviews) {
        this.dontGenerateLayerPreviews = dontGenerateLayerPreviews;
    }


    boolean dontGenerateHtmlFiles = true;


    public boolean isDontGenerateHtmlFiles() {
        return dontGenerateHtmlFiles;
    }

    public void setDontGenerateHtmlFiles(boolean dontGenerateHtmlFiles) {
        this.dontGenerateHtmlFiles = dontGenerateHtmlFiles;
    }

    boolean dontProbeDataResourceLocators = true;

    public boolean isDontProbeDataResourceLocators() {
        return dontProbeDataResourceLocators;
    }

    public void setDontProbeDataResourceLocators(boolean dontProbeDataResourceLocators) {
        this.dontProbeDataResourceLocators = dontProbeDataResourceLocators;
    }

    OnlineServiceValidatorClient() {
    }

    OnlineServiceValidatorClient(String inspireResourceTesterURL) {
        this.inspireResourceTesterURL = inspireResourceTesterURL;
    }


    public ValidationReport validate(String resourceDescriptorText) throws UnsupportedEncodingException, MalformedURLException {
        return validate(resourceDescriptorText, null);
    }
    public ValidationReport validate(String resourceDescriptorText, boolean zipContent) throws UnsupportedEncodingException, MalformedURLException {
        return validate(resourceDescriptorText, null);
    }

    public ValidationReport validate(File resourceDescriptorFile) throws UnsupportedEncodingException, MalformedURLException {
        return validate(null, resourceDescriptorFile);

    }

    private ValidationReport validate(
            String resourceDescriptorText,
            File resourceDescriptorFile) throws MalformedURLException {
        HttpResponse retVal = null;

        HttpClient httpClient = HttpClientBuilder.create().build();

        ValidationReport report = new ValidationReport();
        URL validatorUrl = new URL(this.inspireResourceTesterURL);
        HttpPost httpPost = new HttpPost(this.inspireResourceTesterURL);
        httpPost.addHeader("Accept", "application/xml");

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder
                .create()
                .addTextBody("dontGenerateLayerPreviews",
                        String.valueOf(this.dontGenerateLayerPreviews))
                .addTextBody("dontGenerateHtmlFiles",
                        String.valueOf(this.dontGenerateHtmlFiles))
                .addTextBody("dontProbeDataResourceLocators",
                        String.valueOf(this.dontProbeDataResourceLocators));

        if (resourceDescriptorFile != null) {
            multipartEntityBuilder.addBinaryBody(
                    "uploadedFile",
                    resourceDescriptorFile);
        }
        if (resourceDescriptorText != null) {
            // For text/plain mode use
            multipartEntityBuilder.addTextBody("resourceRepresentation", resourceDescriptorText);
        }

        httpPost.setEntity(multipartEntityBuilder.build());

        try {
            report.start();

            retVal = httpClient.execute(httpPost);
        } catch (ClientProtocolException ex) {
            report.setInfo(ex.getMessage());
        } catch (IOException ex) {
            report.setInfo(ex.getMessage());
        }

        report.gotResponse();

        getResponse(retVal, report);

        report.stop();
        return report;
    }

    private void getResponse(HttpResponse validatorResponse,
                                        ValidationReport report) {
        try {
            //The HTTP response status codes can be:
            //HTTP STATUS CODE 201 (Created)               - a validation report is created or just the resource representation if no issues are found
            //HTTP STATUS CODE 400 (Bad Request)           - the representation sent was not understood at all
            //HTTP STATUS CODE 500 (Internal server error) - a serious system error has occurred
            int responseStatusCode = validatorResponse.getStatusLine().getStatusCode();
            report.setHTTPStatus(responseStatusCode);

            if (responseStatusCode == 201) {
                //URL of the validation report or of the resource metadata if no issues were found
                String resultUrl = validatorResponse.getHeaders("Location")[0].getValue();
                report.setResultUrl(resultUrl);

                if (resultUrl.endsWith("resourceReport")) {
                    //Validation issues were found, hence a validation report has been generated
                    report.setInfo(String.format("Validation issues were found. A report is available at the following URL: %s", resultUrl));
                    report.setStatus(false);
                } else {
                    //No validation issues were found

                    report.setInfo(String.format("No validation issues were found. The metadata information extracted is available at the following URL: %s", resultUrl));
                    report.setStatus(true);
                }

                //XML representation of the validation report or of the resource metadata if no issues were found
                String xml = org.apache.commons.io.IOUtils.toString(validatorResponse.getEntity().getContent(), "UTF-8");
                report.setReport(xml);
            }
        } catch (IOException ex) {
            report.setInfo(ex.getMessage());
        }
    }
}