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

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import org.daobs.index.SolrRequestBean;
import org.daobs.indicator.config.*;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by francois on 17/10/14.
 */
public class IndicatorCalculatorImpl implements IndicatorCalculator {
    private File configurationFile;
    private long lastModificationDate;
    private Reporting reporting;
    private Map<String, Double> indicatorResults;
    private Logger logger = Logger.getLogger("org.daobs.indicator");

    public IndicatorCalculatorImpl(File configurationFile) throws FileNotFoundException {
        this.configurationFile = configurationFile;
        loadConfig();
    }

    @Override
    public IndicatorCalculator loadConfig() throws FileNotFoundException {
        if (configurationFile == null) {
            throw new NullPointerException("Indicator configuration file is null.");
        }
        if (!configurationFile.exists()) {
            throw new FileNotFoundException(String.format("'%s' does not exist.",
                configurationFile));
        }

        this.lastModificationDate = configurationFile.lastModified();

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Reporting.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            reporting = (Reporting) unmarshaller.unmarshal(this.configurationFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        indicatorResults = new HashMap<String, Double>(
            reporting.getVariables().getVariable().size() +
                reporting.getIndicators().getIndicator().size());
        return this;
    }

    @Override
    public Double get(String indicatorName) {
        return null;
    }

    @Override
    public Reporting getConfiguration() {
        return reporting;
    }

    @Override
    public IndicatorCalculator computeIndicators(String... filterQuery) {
        long start = System.currentTimeMillis();
        Iterator<Variable> iterator = reporting.getVariables().getVariable().iterator();
        while (iterator.hasNext()) {
            Variable variable = iterator.next();
            logger.log(java.util.logging.Level.FINE,
                String.format("Compute variable for '%s'.", variable.getId())
            );
            logger.log(java.util.logging.Level.FINE,
                String.format("  Expression '%s'.", variable.getQuery())
            );
            Double statValue;
            Double defaultValue = variable.getDefault();
            Query query = variable.getQuery();
            String format = variable.getNumberFormat();
            if (query != null) {
                // 2 cases here:
                // a) Simple query and the number of records found are returned
                // b) Query with stats

                String statsField = query.getStatsField();
                if (statsField != null) {
                    statValue = SolrRequestBean.getStats(
                        variable.getQuery().getValue(),
                        filterQuery,
                        statsField,
                        query.getStats());
                } else {
                    statValue = SolrRequestBean.getNumFound(
                        variable.getQuery().getValue(),
                        filterQuery);
                }

                logger.log(java.util.logging.Level.FINE,
                    String.format("  Results '%s'.", statValue)
                );
                if (statValue != null) {
                    String valueAsText = formatValue(statValue, format);
                    indicatorResults.put(variable.getId(), statValue);
                    variable.setValue(valueAsText);
                } else if (defaultValue != null) {
                    logger.log(java.util.logging.Level.FINE,
                        String.format("  Set to default value '%s'.", defaultValue)
                    );
                    String valueAsText = formatValue(defaultValue, format);
                    indicatorResults.put(variable.getId(), defaultValue);
                    variable.setValue(valueAsText);
                }

            } else {
                variable.setStatus("No query defined.");
            }
            // TODO: Add indicator exception when computing value
        }

        Iterator<Indicator> iteratorIndicator =
            reporting.getIndicators().getIndicator().iterator();
        while (iteratorIndicator.hasNext()) {
            Indicator indicator = iteratorIndicator.next();
            logger.log(java.util.logging.Level.FINE,
                String.format("Compute indicator for '%s'.", indicator.getId())
            );
            logger.log(java.util.logging.Level.FINE,
                String.format("  Expression '%s'.", indicator.getExpression())
            );
            Calculable e = null;
            double result;
            ExpressionBuilder expressionBuilder = null;
            String expression = indicator.getExpression();
            String format = indicator.getNumberFormat();
            if (expression == null) {
                String message = String.format("  Null expression for indicator '%s'. " +
                        "Check the reporting configuration file.",
                    indicator.getId());
                logger.log(Level.WARNING, message);
                indicator.setStatus(message);
                continue;
            }

            try {
                expressionBuilder = new ExpressionBuilder(expression);
                for (Parameter param : indicator.getParameters().getParameter()) {
                    Double paramValue = indicatorResults.get(param.getId());

                    if (paramValue == null) {
                        logger.log(Level.FINE,
                            String.format("  Parameter '%s' not defined in " +
                                    "variables or failed to compute value.",
                                indicator.getId())
                        );
                        param.setStatus("undefined");
                    } else {
                        String valueAsText = formatValue(paramValue, format);
                        param.setStatus("set");
                        param.setValue(valueAsText);
                        expressionBuilder.withVariable(
                            param.getId(),
                            paramValue
                        );
                    }
                }
                e = expressionBuilder.build();
            } catch (UnknownFunctionException e1) {
                e1.printStackTrace();
                String message = String.format("  Unknown function in expression '%s'. " +
                        ". Error is %s.",
                    indicator.getExpression(),
                    e1.getMessage());
                logger.log(Level.WARNING, message);
                indicator.setStatus(message);
            } catch (UnparsableExpressionException e1) {
                e1.printStackTrace();
                String message = String.format("  Error parsing expression '%s'. " +
                        "Error is %s.",
                    indicator.getExpression(),
                    e1.getMessage());
                logger.log(Level.WARNING, message);
                indicator.setStatus(message);
            }
            if (e != null) {
                try {
                    result = e.calculate();
                    String valueAsText = formatValue(result, format);
                    indicator.setValue(valueAsText);
                    indicatorResults.put(indicator.getId(), result);
                } catch (ArithmeticException e1) {
                    e1.printStackTrace();
                    String message = String.format("  Arithmetic exception. Check expression (%s) or " +
                            "parameter values. Error is %s.",
                        indicator.getExpression(),
                        e1.getMessage());
                    logger.log(Level.WARNING, message);
                    indicator.setStatus(message);
                }
            }
        }
        long elapsed = System.currentTimeMillis() - start;
        // new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(new Date())
        reporting.setComputationTime(Math.round(elapsed));
        return null;
    }

    private String formatValue(Double statValue, String format) {
        String valueAsText = statValue + "";
        if (format != null) {
            DecimalFormat df = new DecimalFormat(format);
            valueAsText = df.format(statValue);
        }
        return valueAsText;
    }

    @Override
    public Map<String, Double> getResults() {

        return indicatorResults;
    }

    public Source toSource() {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Reporting.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            File file = File.createTempFile("indicator", ".xml");
            marshaller.marshal(reporting, file);
            return new StreamSource(file);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Source toDocument() {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Reporting.class);
            Marshaller marshaller = jaxbContext.createMarshaller();

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document document = builder.newDocument();
            marshaller.marshal(reporting, document);
            return new DOMSource(document.getFirstChild());
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        StringBuffer string = new StringBuffer("Reporting calculator:\n");

        Iterator<String> keySetIterator = indicatorResults.keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            string.append(key)
                .append("\t\t\t")
                .append(indicatorResults.get(key))
                .append("\n");
        }
        return string.toString();
    }
}
