/**
 * Copyright 2014-2016 European Environment Agency <p> Licensed under the EUPL, Version 1.1 or – as
 * soon they will be approved by the European Commission - subsequent versions of the EUPL (the
 * "Licence"); You may not use this work except in compliance with the Licence. You may obtain a
 * copy of the Licence at: <p> https://joinup.ec.europa.eu/community/eupl/og_page/eupl <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */

package org.daobs.controller;

import org.daobs.harvester.config.Harvester;
import org.daobs.harvester.config.Harvesters;
import org.daobs.harvester.repository.HarvesterConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
