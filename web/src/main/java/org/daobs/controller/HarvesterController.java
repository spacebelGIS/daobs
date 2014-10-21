package org.daobs.controller;

import org.daobs.harvester.config.Harvesters;
import org.daobs.indicator.config.Reporting;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

/**
 * Created by francois on 21/10/14.
 */

@Controller
public class HarvesterController {

    @RequestMapping(value = "/harvester",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public Harvesters get(HttpServletRequest request)
            throws IOException {
        JAXBContext jaxbContext = null;
        Harvesters harvesters;
        try {
            jaxbContext = JAXBContext.newInstance(Harvesters.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            harvesters = (Harvesters) unmarshaller.unmarshal(
                    new File(
                            request.getSession().getServletContext().
                                    getRealPath("/WEB-INF/harvester/config-harvesters.xml")
            ));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return harvesters;
    }
}
