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

import org.daobs.indicator.config.Reporting;
import org.daobs.indicator.config.Reports;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ReportingController {

    public static final String INDICATOR_CONFIGURATION_DIR =
        "/WEB-INF/datadir/monitoring/";
    public static final String INDICATOR_CONFIGURATION_FILE_PREFIX = "config-";
    private static final String INDICATOR_CONFIGURATION_ID_MATCHER =
        INDICATOR_CONFIGURATION_FILE_PREFIX + "(.*).xml";
    private static final Pattern INDICATOR_CONFIGURATION_ID_PATTERN =
        Pattern.compile(INDICATOR_CONFIGURATION_ID_MATCHER);

    /**
     * Render reporting using XSLT view.
     *
     * @param request
     * @param territory
     * @param calculate
     * @return
     * @throws FileNotFoundException
     */
    public static IndicatorCalculatorImpl generateReporting(HttpServletRequest request,
                                                            String reporting,
                                                            String fq,
                                                            boolean calculate) throws FileNotFoundException {
        String configurationFilePath =
            INDICATOR_CONFIGURATION_DIR +
                INDICATOR_CONFIGURATION_FILE_PREFIX +
                reporting + ".xml";
        File configurationFile =
            new File(request.getSession().getServletContext()
                .getRealPath(configurationFilePath));

        if (configurationFile.exists()) {
            IndicatorCalculatorImpl indicatorCalculator =
                new IndicatorCalculatorImpl(configurationFile);

            if (calculate) {
                // TODO: filter should be more generic
                indicatorCalculator.computeIndicators(fq);
            }
            // adds the XML source file to the model so the XsltView can detect
            return indicatorCalculator;
        } else {
            throw new FileNotFoundException(String.format("Reporting configuration " +
                    "'%s' file does not exist for reporting '%s'.",
                configurationFilePath,
                reporting));
        }
    }

    /**
     * Get list of available reports
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/reporting",
        produces = {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE
        },
        method = RequestMethod.GET)
    @ResponseBody
    public Reports getReports(HttpServletRequest request)
        throws IOException {
        File f = null;
        File[] paths = null;
        Reports reports = new Reports();
        try {
            f = new File(request.getSession().getServletContext()
                .getRealPath(INDICATOR_CONFIGURATION_DIR));
            FilenameFilter filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    if (name.startsWith(INDICATOR_CONFIGURATION_FILE_PREFIX) &&
                        name.endsWith(".xml")) {
                        return true;
                    }
                    return false;
                }
            };

            paths = f.listFiles(filenameFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (paths != null && paths.length > 0) {
            for (File configFile : paths) {
                Reporting reporting = new Reporting();
                Matcher m = INDICATOR_CONFIGURATION_ID_PATTERN.matcher(configFile.getName());
                if (m.find()) {
                    reporting.setId(m.group(1));
                }
                reports.addReporting(reporting);
            }
        }
        return reports;
    }

    /**
     * Get report specification in XML or JSON format.
     *
     * @param request
     * @param reporting
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/reporting/{reporting}",
        produces = {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE
        },
        method = RequestMethod.GET)
    @ResponseBody
    public Reporting get(HttpServletRequest request,
                         @PathVariable(value = "reporting") String reporting,
                         @RequestParam(
                             value = "fq",
                             defaultValue = "",
                             required = false) String fq)
        throws IOException {
        IndicatorCalculatorImpl indicatorCalculator =
            generateReporting(request, reporting, fq, true);
        return indicatorCalculator.getConfiguration();
    }

    /**
     * Generate a specific report for a specific area in XML or JSON format.
     *
     * @param request
     * @param reporting
     * @param territory
     * @param fq Filter query to be applied on top of the territory filter
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/reporting/{reporting}/{territory}",
        produces = {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE
        },
        method = RequestMethod.GET)
    @ResponseBody
    public Reporting get(HttpServletRequest request,
                         @PathVariable(value = "reporting") String reporting,
                         @PathVariable(value = "territory") String territory,
                         @RequestParam(
                             value = "fq",
                             defaultValue = "",
                             required = false) String fq)
        throws IOException {
        IndicatorCalculatorImpl indicatorCalculator =
            generateReporting(request, reporting, fq + " +territory:" + territory, true);
        return indicatorCalculator.getConfiguration();
    }
}
