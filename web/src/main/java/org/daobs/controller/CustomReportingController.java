package org.daobs.controller;

import org.daobs.index.SolrRequestBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Created by francois on 18/12/14.
 */
@Controller
public class CustomReportingController {

    // TODO: config should move to configuration file
    public static final String SPATIALDATASETS_QUERY_URL = "/data/select?" +
            "q=%%2BdocumentType:metadata+%%2B(resourceType%%3Adataset+resourceType%%3Aseries)+%%2Bterritory:%s&" +
            "start=0&rows=%d&" +
            "fl=metadataIdentifier,resourceTitle," +
            "inspireAnnex,inspireTheme,inspireConformResource," +
            "recordOperatedByType," +
            "OrgForResource,custodianOrgForResource,ownerOrgForResource,pointOfContactOrgForResource";

    public static final String SPATIALDATASERVICE_QUERY_URL = "/data/select?" +
            "q=%%2BdocumentType:metadata+%%2BresourceType:service+%%2Bterritory:%s&" +
            "start=0&rows=%d&" +
            "fl=metadataIdentifier,resourceTitle," +
            "inspireAnnex,inspireTheme,inspireConformResource," +
            "serviceType,linkUrl," +
            "OrgForResource,custodianOrgForResource,ownerOrgForResource,pointOfContactOrgForResource";

    public static final List<String> BOOLEAN_PARAMETERS = new ArrayList<>(
            Arrays.asList("withRowData")
    );


    /**
     * Generate report
     * TODO: provide custom XSLT as parameter
     *
     * @param request
     * @param reporting
     * @return
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/reporting/custom/{reporting}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE
            },
            method = RequestMethod.GET)
    public ModelAndView generateMonitoring(HttpServletRequest request,
                                           @RequestParam Map<String,String> allRequestParams,
                                           @PathVariable(value = "reporting") String reporting,
                                           @RequestParam(
                                                   value = "withRowData",
                                                   required = false) Boolean withRowData,
                                           @RequestParam(
                                                   value = "rows",
                                                   defaultValue = "10000",
                                                   required = false) int rows)
            throws IOException {
        IndicatorCalculatorImpl indicatorCalculator =
                ReportingController.generateReporting(request, reporting, null, true);

        ModelAndView model = new ModelAndView("reporting-xslt-" + reporting);
        model.addObject("xmlSource", indicatorCalculator.toSource());

        addRequestParametersToModel(allRequestParams, model);

        return model;
    }



    /**
     * Generate a specific report for a specific area in
     * INSPIRE monitoring reporting format
     *
     * @param request
     * @param reporting
     * @param territory
     * @param withRowData Include the rowData section in the report.
     * @param rows Number of rows to return for spatial data sets and services
     *             Default value is 10000. When the number of records to return
     *             is too high, error may occurs. Only applies if withRowData
     *             is true.
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/reporting/custom/{reporting}/{territory}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView generateMonitoring(HttpServletRequest request,
                                           @RequestParam Map<String,String> allRequestParams,
                                           @RequestParam(
                                                   value = "withRowData",
                                                   required = false) Boolean withRowData,
                                           @RequestParam(
                                                   value = "rows",
                                                   defaultValue = "10000",
                                                   required = false) int rows,
                                           @PathVariable(value = "reporting") String reporting,
                                           @PathVariable(value = "territory") String territory)
            throws IOException {

        IndicatorCalculatorImpl indicatorCalculator =
                ReportingController.generateReporting(request, reporting, territory, true);


        ModelAndView model = new ModelAndView("reporting-xslt-" + reporting);
        model.addObject("xmlSource", indicatorCalculator.toSource());
        // Add path parameters
        if (territory != null) {
            model.addObject("territory", territory);
        }

        addRequestParametersToModel(allRequestParams, model);

        addRowDataToModel(withRowData, rows, territory, model);

        return model;
    }

    private void addRowDataToModel(Boolean withRowData, int rows, String territory, ModelAndView model) {
        // Handle defaults for boolean
        if (withRowData == null) {
            withRowData = false;
        }
        model.addObject("withRowData", withRowData);

        // Grab data sets and services to later
        // build the raw data section
        if (withRowData) {
            Node spatialDataSets = SolrRequestBean.query(
                    String.format(
                            SPATIALDATASETS_QUERY_URL,
                            territory, rows));
            model.addObject("spatialDataSets", spatialDataSets);


            Node spatialDataServices = SolrRequestBean.query(
                    String.format(
                            SPATIALDATASERVICE_QUERY_URL,
                            territory, rows));
            model.addObject("spatialDataServices", spatialDataServices);
        }
    }

    private void addRequestParametersToModel(Map<String, String> allRequestParams, ModelAndView model) {
        // Add all request parameters to the model
        // in order to have them as XSL parameters in the view.
        Iterator iterator = allRequestParams.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> parameter = (Map.Entry<String, String>) iterator.next();
            String parameterName = parameter.getKey();
            Object parameterValue = (String)parameter.getValue();
            if (BOOLEAN_PARAMETERS.contains(parameterName)) {
                parameterValue = Boolean.parseBoolean((String)parameterValue);
            }
            model.addObject(parameterName, parameterValue);
        }
    }

}
