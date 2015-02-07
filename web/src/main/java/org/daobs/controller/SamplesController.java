package org.daobs.controller;

import org.daobs.solr.samples.loader.DashboardLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by francois on 21/10/14.
 */

@Controller
public class SamplesController {

    public static final String DASHBOARD_FOLDER = "/dashboard/app/dashboards";
    @Autowired
    DashboardLoader loader;

    /**
     * Return the list of sample dashboards available.
     *
     * @param request
     * @return
     */
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
     * @param request
     * @return
     */
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
     * @param request
     * @param dashboardFilePrefix
     * @return
     * @throws IOException
     */
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
