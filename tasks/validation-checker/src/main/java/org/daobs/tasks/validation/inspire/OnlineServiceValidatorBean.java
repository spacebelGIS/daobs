package org.daobs.tasks.validation.inspire;

import org.apache.camel.Exchange;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Simple bean to call the validation service.
 *
 * Created by francois on 08/12/14.
 */
public class OnlineServiceValidatorBean {

    String inspireResourceTesterURL;

    public String getInspireResourceTesterURL() {
        return inspireResourceTesterURL;
    }

    public void setInspireResourceTesterURL(String inspireResourceTesterURL) {
        this.inspireResourceTesterURL = inspireResourceTesterURL;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    private double threshold;

    /**
     * Get the input message body and validate
     * it against the INSPIRE validation service.
     * The output body contains the validation report.
     *
     * Headers are propagated.
     *
     * @param exchange
     */
    public void validateBody(Exchange exchange) {
        String xml = exchange.getIn().getBody(String.class);

        ValidationReport report = null;
        OnlineServiceValidatorClient validatorClient =
                new OnlineServiceValidatorClient(this.inspireResourceTesterURL, threshold);

        try {
            report = validatorClient.validate(xml);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        exchange.getOut().setBody(report);
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
    }
}
