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


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.daobs.solr.samples.loader.DashboardLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by francois on 21/10/14.
 */
@Api(value = "samples",
    tags = "samples",
    description = "Samples operations")
@Controller
public class SamplesController {

  public static final String DASHBOARD_FOLDER = "/dashboard/app/dashboards";
  @Autowired
  DashboardLoader loader;

  /**
   * Get the list of sample dashboards available.
   *
   */
  @ApiOperation(value = "Get the list of sample dashboards available",
      nickname = "getDashboards")
  @RequestMapping(value = "/samples/dashboard",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.GET)
  @ResponseBody
  public Set<String> getDashboards(HttpServletRequest request) {
    return loader.getListOfResources(
      request.getSession()
        .getServletContext()
        .getRealPath(DASHBOARD_FOLDER), false);
  }

  /**
   * Return the list of sample dashboard types available.
   * Dashboard type is defined by the upper-case file prefix.
   * The dashboard file pattern is
   * {@link org.daobs.solr.samples.loader.DashboardLoader#dashboardSampleFilePattern}.
   *
   */
  @ApiOperation(value = "Get the list of dashboard types",
      nickname = "getDashboardTypes")
  @RequestMapping(value = "/samples/dashboardType",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.GET)
  @ResponseBody
  public Set<String> getTypes(HttpServletRequest request) {
    return loader.getListOfResources(
      request.getSession()
        .getServletContext()
        .getRealPath(DASHBOARD_FOLDER), true);
  }

  /**
   * Put a dashboard in the Solr dashboard core.
   *
   */
  @ApiOperation(value = "Add samples",
      nickname = "getDashboardTypes")
  @RequestMapping(value = "/samples/dashboard/{dashboardFilePrefix}",
      produces = {
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_JSON_VALUE
      },
      method = RequestMethod.PUT)
  @ResponseBody
  public Map<String, List<String>> get(HttpServletRequest request,
                                       @PathVariable(value = "dashboardFilePrefix")
                                         String dashboardFilePrefix)
    throws IOException {
    return loader.load(
      request.getSession()
        .getServletContext()
        .getRealPath(DASHBOARD_FOLDER),
      dashboardFilePrefix);
  }
}
