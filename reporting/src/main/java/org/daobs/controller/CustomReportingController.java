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
import io.swagger.annotations.ApiParam;

import org.daobs.index.EsRequestBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by francois on 18/12/14.
 */
@Api(value = "reports",
    tags = "reports",
    description = "Report operations")
@EnableWebMvc
@Controller
public class CustomReportingController {

  // TODO: config should move to configuration file
  public static final String[] SPATIALDATASETS_QUERY_URL = new String[]{
    "metadataIdentifier", "resourceTitle", "isAboveThreshold",
    "inspireAnnex", "inspireTheme", "inspireConformResource",
    "recordOperatedByType", "recordOperatedByTypeview", "recordOperatedByTypedownload",
    "OrgForResource", "custodianOrgForResource", "ownerOrgForResource",
    "pointOfContactOrgForResource", "harvesterUuid"
  };

  public static final String[] SPATIALDATASERVICE_QUERY_URL = new String[]{
    "metadataIdentifier", "resourceTitle", "isAboveThreshold",
      "inspireAnnex", "inspireTheme", "inspireConformResource",
      "serviceType", "linkUrl", "link",
      "OrgForResource", "custodianOrgForResource", "ownerOrgForResource",
      "pointOfContactOrgForResource", "harvesterUuid"
  };

  public static final List<String> BOOLEAN_PARAMETERS = new ArrayList<>(
      Arrays.asList("withRowData")
  );


  /**
   * Generate a report.
   * TODO: provide custom XSLT as parameter
   */
  @ApiOperation(value = "Generate a report",
      nickname = "generateReport")
  @RequestMapping(value = "/reports/custom/{reporting}",
      produces = {
        MediaType.APPLICATION_XML_VALUE
      },
      method = RequestMethod.GET)
  public ModelAndView generateMonitoring(
      HttpServletRequest request,
      @RequestParam Map<String, String> allRequestParams,
      @ApiParam(
       value = "The report type to generate",
       required = true)
       @PathVariable(value = "reporting") String reporting,
      @ApiParam(
       value = "Add raw data section")
      @RequestParam(
       value = "withRowData",
       required = false) Boolean withRowData,
      @ApiParam(
       value = "An optional filter query to generate report on a subset",
       required = true)
      @RequestParam(
       value = "fq",
       defaultValue = "",
       required = false) String fq,
      @ApiParam(
       value = "An optional scope")
      @RequestParam(
       value = "scopeId",
       defaultValue = "",
       required = false) String scopeId,
      @ApiParam(
       value = "Max number of documents to add in the raw data section")
      @RequestParam(
       value = "rows",
       defaultValue = "10000",
       required = false) int rows)
      throws IOException {
    IndicatorCalculatorImpl indicatorCalculator =
        ReportingController.generateReporting(request, reporting, scopeId, fq, true);

    ModelAndView model = new ModelAndView("reporting-xslt-" + reporting);
    model.addObject("xmlSource", indicatorCalculator.toSource());

    addRequestParametersToModel(allRequestParams, model);

    addRowDataToModel(withRowData, rows, fq, model);

    return model;
  }


  /**
   * Generate a specific report for a specific area in
   * INSPIRE monitoring reporting format.
   *
   * @param withRowData Include the rowData section in the report.
   * @param rows Number of rows to return for spatial data sets and services
   *             Default value is 10000. When the number of records to return
   *             is too high, error may occurs. Only applies if withRowData
   *             is true.
   */
  @ApiOperation(value = "Generate a specific report "
      + "for a specific area in INSPIRE monitoring reporting format",
      nickname = "generateINSPIREReport")
  @RequestMapping(value = "/reports/custom/{reporting}/{territory}",
      produces = {
        MediaType.APPLICATION_XML_VALUE
      },
      method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView generateMonitoring(
      HttpServletRequest request,
      @RequestParam Map<String, String> allRequestParams,
      @ApiParam(
       value = "Add raw data section")
      @RequestParam(
       value = "withRowData",
       required = false) Boolean withRowData,
      @ApiParam(
       value = "An optional filter query to generate report on a subset",
       required = true)
      @RequestParam(
       value = "fq",
       defaultValue = "",
       required = false) String fq,
      @ApiParam(
       value = "An optional scope")
      @RequestParam(
       value = "scopeId",
       defaultValue = "",
       required = false) String scopeId,
      @ApiParam(
       value = "Max number of documents to add in the raw data section")
      @RequestParam(
       value = "rows",
       defaultValue = "10000",
       required = false) int rows,
      @ApiParam(
       value = "The report type to generate",
       required = true)
      @PathVariable(value = "reporting") String reporting,
      @ApiParam(
       value = "A territory",
       required = true)
      @PathVariable(value = "territory") String territory)
      throws IOException {
    String filter = fq + " +territory:" + territory;
    IndicatorCalculatorImpl indicatorCalculator =
        ReportingController.generateReporting(request, reporting, scopeId, filter, true);


    ModelAndView model = new ModelAndView("reporting-xslt-" + reporting);
    model.addObject("xmlSource", indicatorCalculator.toSource());
    // Add path parameters
    if (territory != null) {
      model.addObject("territory", territory);
    }
    if (filter != null) {
      model.addObject("filter", filter);
    }

    addRequestParametersToModel(allRequestParams, model);

    // TODO: URL encoding should be done when the HTTP request is made
    addRowDataToModel(withRowData, rows, filter, model);

    return model;
  }

  private void addRowDataToModel(Boolean withRowData, int rows, String fq, ModelAndView model) {
    // Handle defaults for boolean
    if (withRowData == null) {
      withRowData = false;
    }
    model.addObject("withRowData", withRowData);

    // Grab data sets and services to later
    // build the raw data section
    if (withRowData) {
      Node spatialDataSets = null;
      try {
        spatialDataSets = EsRequestBean.query(
              SPATIALDATASETS_QUERY_URL,
              fq, rows);
        model.addObject("spatialDataSets", spatialDataSets);
      } catch (Exception ex) {
        ex.printStackTrace();
      }


      Node spatialDataServices = null;
      try {
        spatialDataServices = EsRequestBean.query(
              SPATIALDATASERVICE_QUERY_URL,
              fq, rows);
        model.addObject("spatialDataServices", spatialDataServices);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private void addRequestParametersToModel(
      Map<String, String> allRequestParams,
      ModelAndView model) {
    // Add all request parameters to the model
    // in order to have them as XSL parameters in the view.
    Iterator iterator = allRequestParams.entrySet().iterator();

    while (iterator.hasNext()) {
      Map.Entry<String, String> parameter = (Map.Entry<String, String>) iterator.next();
      String parameterName = parameter.getKey();
      Object parameterValue = (String) parameter.getValue();
      if (BOOLEAN_PARAMETERS.contains(parameterName)) {
        parameterValue = Boolean.parseBoolean((String) parameterValue);
      }
      model.addObject(parameterName, parameterValue);
    }
  }

}
