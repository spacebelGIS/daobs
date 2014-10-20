package org.daobs.controller;

import org.daobs.indicator.config.Reporting;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
public class ReportingController {

    public static final String INDICATOR_CONFIGURATION_FILE_PREFIX = "WEB-INF/reporting/config-";

    @RequestMapping(value = "/daobs")
    public ModelAndView goHome2(HttpServletResponse response) throws IOException {
        return new ModelAndView("home");
    }


    @RequestMapping(value = "/reporting/raw/{reporting}/{territory}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XHTML_XML_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public Reporting get(HttpServletRequest request,
                         @PathVariable(value = "reporting") String reporting,
                         @PathVariable(value = "territory") String territory)
            throws IOException {
        IndicatorCalculatorImpl indicatorCalculator =
                generateReporting(request, reporting, territory);
        return indicatorCalculator.getConfiguration();
    }


    @RequestMapping(value = "/reporting/{reporting}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE
            },
            method = RequestMethod.GET)
    public ModelAndView generateMonitoring(HttpServletRequest request,
                                           @PathVariable(value = "reporting") String reporting)
            throws IOException {
        IndicatorCalculatorImpl indicatorCalculator =
                generateReporting(request, reporting, null);
        ModelAndView model = new ModelAndView("reporting");
        model.addObject("xmlSource", indicatorCalculator.toSource());
        // TODO: Add global info
        return model;
    }


    @RequestMapping(value = "/reporting/{reporting}/{territory}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE
            },
            method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView generateMonitoring(HttpServletRequest request,
                                           @PathVariable(value = "reporting") String reporting,
                                           @PathVariable(value = "territory") String territory)
            throws IOException {
        IndicatorCalculatorImpl indicatorCalculator =
                generateReporting(request, reporting, territory);
        ModelAndView model = new ModelAndView("reporting");
        model.addObject("xmlSource", indicatorCalculator.toSource());

        if (territory != null) {
            model.addObject("territory", territory);
            // TODO: Add organisation info
        }
        return model;
    }

    /**
     * Render reporting using XSLT view.
     *
     * @param request
     * @param territory
     * @return
     * @throws FileNotFoundException
     */
    private IndicatorCalculatorImpl generateReporting(HttpServletRequest request,
                                           String reporting,
                                           String territory) throws FileNotFoundException {
        String configurationFilePath = INDICATOR_CONFIGURATION_FILE_PREFIX + reporting + ".xml";
        File configurationFile =
                new File(request.getServletContext().getRealPath(""),
                        configurationFilePath);

        if (configurationFile.exists()) {
            IndicatorCalculatorImpl indicatorCalculator =
                    new IndicatorCalculatorImpl(configurationFile);

            indicatorCalculator.computeIndicators("+territory:" + territory);
            // adds the XML source file to the model so the XsltView can detect
            return indicatorCalculator;
        } else {
            throw new FileNotFoundException(String.format("Reporting configuration " +
                    "'%s' file does not exist for reporting '%s'.",
                    configurationFilePath,
                    reporting));
        }

    }
}