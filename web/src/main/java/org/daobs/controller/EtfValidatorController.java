package org.daobs.controller;

import org.daobs.event.EtfValidatorEvent;
import org.daobs.messaging.JMSMessager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


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
    private JMSMessager jmsMessager;

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
