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

package org.daobs.index;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.AnalysisParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Utility class to run Solr requests like field analysis.
 * Created by francois on 30/09/14.
 */
public class SolrRequestBean {
  private static String PHASE_INDEX = "index";
  private static String PHASE_QUERY = "query";
  private static String DEFAULT_FILTER_CLASS = "org.apache.lucene.analysis.synonym.SynonymFilter";

  /**
   * Query solr over HTTP.
   */
  static Node query(String collection, String query) {
    try {
      SolrServerBean serverBean = SolrServerBean.get();
      URL url = new URL(serverBean.getSolrServerUrl() + collection + query);
      String xmlResponse = IOUtils.toString(url, "UTF-8");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputSource is = new InputSource(
        new ByteArrayInputStream(
          xmlResponse.getBytes("UTF-8")));
      return builder.parse(is).getFirstChild();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  static Node query(String query) {
    SolrServerBean serverBean = SolrServerBean.get();
    return query(serverBean.getSolrServerCore(), query);
  }

  /**
   * Return the number of rows matching the query.
   *
   * @param query The query
   */
  static Double getNumFound(String collection, String query, String... filterQuery) {
    try {
      SolrQuery solrQuery = new SolrQuery();
      solrQuery.setQuery(query);
      if (filterQuery != null) {
        solrQuery.setFilterQueries(filterQuery);
      }
      solrQuery.setRows(0);

      SolrServerBean serverBean = SolrServerBean.get();
      SolrClient solrServer = serverBean.getServer();

      QueryResponse solrResponse = solrServer.query(collection, solrQuery);
      return (double) solrResponse.getResults().getNumFound();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  static Double getNumFound(String query, String... filterQuery) {
    SolrServerBean serverBean = SolrServerBean.get();
    return getNumFound(serverBean.getSolrServerCore(), query, filterQuery);
  }

  /**
   * Get stats for a field.
   */
  static Double getStats(
    String collection, String query, String[] filterQuery, String statsField, String stats) {
    try {
      SolrQuery solrQuery = new SolrQuery();
      solrQuery.setQuery(query);
      if (filterQuery != null) {
        solrQuery.setFilterQueries(filterQuery);
      }
      solrQuery.setRows(0);
      solrQuery.setGetFieldStatistics(true);
      solrQuery.setGetFieldStatistics(statsField);

      SolrServerBean serverBean = SolrServerBean.get();
      SolrClient solrServer = serverBean.getServer();

      QueryResponse solrResponse = solrServer.query(collection, solrQuery);
      FieldStatsInfo fieldStatsInfo = solrResponse.getFieldStatsInfo().get(statsField);

      if (fieldStatsInfo != null) {
        if ("min".equals(stats)) {
          return (Double) fieldStatsInfo.getMin();
        } else if ("max".equals(stats)) {
          return (Double) fieldStatsInfo.getMax();
        } else if ("count".equals(stats)) {
          return fieldStatsInfo.getCount().doubleValue();
        } else if ("missing".equals(stats)) {
          return fieldStatsInfo.getMissing().doubleValue();
        } else if ("mean".equals(stats)) {
          return (Double) fieldStatsInfo.getMean();
        } else if ("sum".equals(stats)) {
          return (Double) fieldStatsInfo.getSum();
        } else if ("stddev".equals(stats)) {
          return fieldStatsInfo.getStddev();
        } else if ("countDistinct".equals(stats)) {
          return fieldStatsInfo.getCountDistinct().doubleValue();
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  static Double getStats(String query, String[] filterQuery,
                         String statsField, String stats) {
    SolrServerBean serverBean = SolrServerBean.get();
    return getStats(serverBean.getSolrServerCore(), query, filterQuery, statsField, stats);
  }

  /**
   * Analyze a field and a value against the index phase
   * and return the first value generated
   * by the {@see DEFAULT_FILTER_CLASS}.
   * <p>
   * The field tested MUST use a DEFAULT_FILTER_CLASS
   * in the analyzer chain.
   *
   * See {@see analyzeField}.
   * </p>
   *
   */
  static String analyzeField(String collection,
                             String fieldName,
                             String fieldValue) {
    return analyzeField(collection, fieldName, fieldValue, SolrRequestBean.PHASE_INDEX, SolrRequestBean.DEFAULT_FILTER_CLASS, 0);
  }

  static String analyzeField(String fieldName,
                             String fieldValue) {
    SolrServerBean serverBean = SolrServerBean.get();
    return analyzeField(serverBean.getSolrServerCore(), fieldName, fieldValue);
  }

  /**
   * Analyze a field and a value against the index
   * or query phase and return the first value generated
   * by the specified filterClass.
   * <p>
   * If an exception occured, the field value is returned.
   *
   * Equivalent to: {@linkplain http://localhost:8983/solr/analysis/field?analysis.fieldname=inspireTheme_syn&q=hoogte}
   *
   * TODO: Logger.
   * </p>
   * @param fieldName The field name
   * @param fieldValue    The field value to analyze
   * @param analysisPhaseName The analysis phase (ie. "index" or "query")
   * @param filterClass   The filter class the response should be extracted from
   * @param tokenPosition The position of the token to extract
   *
   * @return The analyzed string value if found or the field value if not found.
   */
  static String analyzeField(String collection,
                             String fieldName,
                             String fieldValue,
                             String analysisPhaseName,
                             String filterClass,
                             int tokenPosition) {

    try {
      SolrClient server = SolrServerBean.get().getServer();

      ModifiableSolrParams params = new ModifiableSolrParams();
      params.set(AnalysisParams.FIELD_NAME, fieldName);
      params.set(AnalysisParams.FIELD_VALUE, fieldValue);

      FieldAnalysisRequest request = new FieldAnalysisRequest();
      List<String> fieldNames = new ArrayList<String>();
      fieldNames.add(fieldName);
      request.setFieldNames(fieldNames);
      request.setFieldValue(fieldValue);

      FieldAnalysisResponse res = new FieldAnalysisResponse();
      try {
        res.setResponse(server.request(request, collection));
      } catch (SolrServerException exception) {
        exception.printStackTrace();
        return fieldValue;
      } catch (IOException exception) {
        exception.printStackTrace();
        return fieldValue;
      }
      FieldAnalysisResponse.Analysis analysis =
        res.getFieldNameAnalysis(fieldName);

      Iterable<AnalysisResponseBase.AnalysisPhase> phases =
        SolrRequestBean.PHASE_INDEX.equals(analysisPhaseName)
          ? analysis.getIndexPhases() : analysis.getQueryPhases();
      if (phases != null) {
        Iterator<AnalysisResponseBase.AnalysisPhase> iterator =
          phases.iterator();
        while (iterator.hasNext()) {
          AnalysisResponseBase.AnalysisPhase analysisPhase = iterator.next();
          if (analysisPhase.getClassName()
            .equals(filterClass)
            && analysisPhase.getTokens().size() > 0) {
            AnalysisResponseBase.TokenInfo token =
              analysisPhase.getTokens().get(tokenPosition);
            return token.getText();
          }
        }
      }
      return fieldValue;
    } catch (Exception exception) {
      exception.printStackTrace();
      return fieldValue;
    }
  }

  /**
   * Analyze a field.
   */
  static String analyzeField(String fieldName,
                             String fieldValue,
                             String analysisPhaseName,
                             String filterClass,
                             int tokenPosition) {
    SolrServerBean serverBean = SolrServerBean.get();
    return analyzeField(serverBean.getSolrServerCore(),
      fieldName, fieldValue, analysisPhaseName, filterClass, tokenPosition);
  }
}
