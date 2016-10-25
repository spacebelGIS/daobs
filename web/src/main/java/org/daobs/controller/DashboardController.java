/*
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Created by francois on 21/10/14.
 */
@Api(value = "dashboards",
    tags = "dashboards",
    description = "Dashboard operations")
@EnableWebMvc
@Controller
public class DashboardController {

  /**
   * Remove one dashboard.
   */
  @ApiOperation(value = "Remove one dashboard",
      nickname = "deleteDashboardById")
  @RequestMapping(value = "/dashboards/{id:.+}",
      produces = {
          MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> delete(
      @PathVariable final String id) throws Exception {

    // TODO ES
    //    SolrClient client = server.getServer();
    //    client.deleteById(collection, id);
    //    client.commit(collection);

    return new ResponseEntity<>("", HttpStatus.OK);
  }

  /**
   * Remove all dashboards.
   */
  @ApiOperation(value = "Remove all dashboards",
      nickname = "deleteDashsboards")
  @RequestMapping(
      value = "/dashboards",
      produces = {
          MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<String> deleteAll() throws Exception {

    // TODO ES
    //    SolrClient client = server.getServer();
    //    client.deleteByQuery(collection, "+type:dashboard");
    //    client.commit(collection);

    return new ResponseEntity<>("", HttpStatus.OK);
  }
}
