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

package org.daobs.controller;

import org.daobs.event.EtfValidatorEvent;
import org.daobs.messaging.JmsMessager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller to launch the ETF Validator.
 *
 * @author Jose García
 */
@Controller
public class EtfValidatorController {
  @Autowired
  private ApplicationContext appContext;

  @Autowired
  private JmsMessager jmsMessager;

  @RequestMapping(value = "/etf-validator",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.GET)
  @ResponseBody
  public RequestResponse run(@RequestParam(required = true) String fq)
    throws Exception {
    sendMessage(fq);
    return new RequestResponse("ETF Validator started", "success");
  }

  private void sendMessage(String fq) {
    EtfValidatorEvent event = new EtfValidatorEvent(appContext, fq);

    jmsMessager.sendMessage("etf-task-validate", event);
  }
}
