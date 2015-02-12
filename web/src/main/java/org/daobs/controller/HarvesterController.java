package org.daobs.controller;

import org.daobs.harvester.config.Harvester;
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
    public RequestResponse addOrUpdate(@RequestBody Harvester harvester)
            throws Exception {
        harvesterConfigRepository.addOrUpdate(harvester);
        return new RequestResponse("Harvester added", "success");
    }

    @RequestMapping(value = "/harvester/{uuid}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.DELETE)
    @ResponseBody
    public RequestResponse remove(
            @PathVariable(value = "uuid") String harvesterUuid
    ) throws Exception {
        harvesterConfigRepository.remove(harvesterUuid);
        return new RequestResponse("Harvester removed", "success");
    }


    @RequestMapping(value = "/harvester/{uuid}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public RequestResponse run(@PathVariable(value = "uuid") String harvesterUuid,
                      @RequestParam(
                             value = "action",
                             required = false) String action
                     )
            throws Exception {
        harvesterConfigRepository.start(harvesterUuid);
        return new RequestResponse("Harvester started", "success");
    }
}