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

import org.apache.camel.Exchange;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Simple bean to call the validation service.
 * Created by francois on 08/12/14.
 */
public class OnlineServiceValidatorBean {

  String inspireResourceTesterUrl;
  boolean probeNetworkServices = false;
  boolean probeDataResourceLocators = false;
  private double threshold;

  public String getInspireResourceTesterUrl() {
    return inspireResourceTesterUrl;
  }

  public void setInspireResourceTesterUrl(String inspireResourceTesterUrl) {
    this.inspireResourceTesterUrl = inspireResourceTesterUrl;
  }

  public double getThreshold() {
    return threshold;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  public boolean isProbeNetworkServices() {
    return probeNetworkServices;
  }

  public void setProbeNetworkServices(boolean probeNetworkServices) {
    this.probeNetworkServices = probeNetworkServices;
  }

  public boolean isProbeDataResourceLocators() {
    return probeDataResourceLocators;
  }

  public void setProbeDataResourceLocators(boolean probeDataResourceLocators) {
    this.probeDataResourceLocators = probeDataResourceLocators;
  }

  /**
   * Get the input message body and validate
   * it against the INSPIRE validation service.
   * The output body contains the validation report.
   * Headers are propagated.
   */
  public void validateBody(Exchange exchange) {
    String xml = exchange.getIn().getBody(String.class);

    ValidationReport report = null;
    OnlineServiceValidatorClient validatorClient =
        new OnlineServiceValidatorClient(this.inspireResourceTesterUrl,
            threshold,
            probeDataResourceLocators,
            probeNetworkServices);

    try {
      report = validatorClient.validate(xml);
    } catch (UnsupportedEncodingException exception) {
      exception.printStackTrace();
    } catch (MalformedURLException exception) {
      exception.printStackTrace();
    }
    exchange.getOut().setBody(report);
    exchange.getOut().setHeaders(exchange.getIn().getHeaders());
  }
}
