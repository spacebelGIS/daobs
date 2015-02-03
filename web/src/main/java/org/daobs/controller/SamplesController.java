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

/**
 * Created by francois on 21/10/14.
 */

@Controller
public class SamplesController {

    @Autowired
    DashboardLoader loader;

    @RequestMapping(value = "/samples/dashboard/{dashboardFilePrefix}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> get(HttpServletRequest request,
                            @PathVariable(value = "dashboardFilePrefix")
                                String dashboardFilePrefix)
            throws IOException {


        return loader.load(
                request.getSession()
                        .getServletContext()
                        .getRealPath("/dashboard/app/dashboards"),
                dashboardFilePrefix + "*.json");
    }
}
