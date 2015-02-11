package org.daobs.controller;

import org.daobs.harvester.config.Harvesters;
import org.daobs.harvester.repository.HarvesterConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    HarvesterConfigRepository harvesterConfigRepository;

    @RequestMapping(value = "/harvester",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public Harvesters get()
            throws IOException {
        return harvesterConfigRepository.getAll();
    }

    @RequestMapping(value = "/harvester",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.PUT)
    @ResponseBody
    public boolean add(HttpServletRequest request)
            throws IOException {
        return false;
    }


    @RequestMapping(value = "/harvester/{id}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public String start(HttpServletRequest request,
                         @PathVariable(value = "id") String harvesterId,
                         @RequestParam(
                                 value = "action",
                                 required = false) String action
                         )
            throws Exception {
        System.out.println(harvesterId);
        System.out.println(action);
        harvesterConfigRepository.start(harvesterId);

        return action;
    }
}
