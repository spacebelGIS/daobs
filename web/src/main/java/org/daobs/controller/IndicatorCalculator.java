package org.daobs.controller;

import org.daobs.indicator.config.Reporting;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by francois on 17/10/14.
 */
public interface IndicatorCalculator {
    /**
     * Load or reload the configuration.
     *
     * @return
     * @throws FileNotFoundException
     */
    public IndicatorCalculator loadConfig() throws FileNotFoundException;

    Reporting getConfiguration();

    public Double get(String indicatorName);

    /**
     * Compute indicators.
     *
     * @param filterQuery
     * @return
     */
    public IndicatorCalculator computeIndicators(String... filterQuery);

    Map<String, Double> getResults();
}
