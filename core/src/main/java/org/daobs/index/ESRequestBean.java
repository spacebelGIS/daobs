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

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.w3c.dom.Node;

import java.io.IOException;

/**
 * Utility class to run Solr requests like field analysis.
 * Created by francois on 30/09/14.
 */
public class ESRequestBean {
  private static String PHASE_INDEX = "index";
  private static String PHASE_QUERY = "query";
  private static String DEFAULT_FILTER_CLASS = "org.apache.lucene.analysis.synonym.SynonymFilter";

  public static String deleteByQuery(String index, String query, int scrollSize) throws Exception {
    ESClientBean client = ESClientBean.get();
    SearchResponse scrollResponse = client.getClient()
      .prepareSearch(index)
      .setQuery(QueryBuilders.queryStringQuery(query))
      .setScroll(new TimeValue(60000))
      .setSize(scrollSize)
      .execute().actionGet();

    BulkRequestBuilder brb = client.getClient().prepareBulk();
    while (true) {
      for(SearchHit hit : scrollResponse.getHits()) {
        brb.add(new DeleteRequest(index, hit.getType(), hit.getId()));
      }
      scrollResponse = client.getClient()
        .prepareSearchScroll(scrollResponse.getScrollId())
        .setScroll(new TimeValue(60000))
        .execute().actionGet();
      if (scrollResponse.getHits().getHits().length == 0) {
        break;
      }
    }

    if (brb.numberOfActions() > 0) {
      BulkResponse result = brb.execute().actionGet();
      if (result.hasFailures()) {
        throw new IOException(result.buildFailureMessage());
      } else {
        return String.format(
          "{\"msg\": \"%d records removed.\"}", brb.numberOfActions());
      }
    }
    return String.format("No match found for query ''.", query);
  }
  /**
   * Query solr over HTTP.
   */
  public static Node query(String collection, String query) {
//    try {
//      SolrServerBean serverBean = SolrServerBean.get();
//      URL url = new URL(serverBean.getSolrServerUrl() + collection + query);
//      String xmlResponse = IOUtils.toString(url, "UTF-8");
//      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//      DocumentBuilder builder = factory.newDocumentBuilder();
//      InputSource is = new InputSource(
//        new ByteArrayInputStream(
//          xmlResponse.getBytes("UTF-8")));
//      return builder.parse(is).getFirstChild();
//    } catch (Exception exception) {
//      exception.printStackTrace();
//    }
    return null;
  }

  public static Node query(String query) {
    ESClientBean client = ESClientBean.get();
    return query(client.getCollection(), query);
  }

  /**
   * Return the number of rows matching the query.
   *
   * @param query The query
   */
  public static Double getNumFound(String collection, String query, String... filterQuery) {
    try {

      ESClientBean client = ESClientBean.get();
      SearchResponse response = client.getClient()
        .prepareSearch(collection)
        .setQuery(QueryBuilders.queryStringQuery(query))
//        .setPostFilter(QueryBuilders.simpleQueryStringQuery(filterQuery[0]))
        .setFrom(0)
        .setSize(0)
        .setExplain(true)
        .execute()
        .actionGet();

      return (double) response.getHits().getTotalHits();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  public static Double getNumFound(String query, String... filterQuery) {
    ESClientBean client = ESClientBean.get();
    return getNumFound(client.getCollection(), query, filterQuery);
  }

  /**
   * Get stats for a field.
   */
  public static Double getStats(
    String collection, String query, String[] filterQuery, String statsField, String stats) {

    ESClientBean client = ESClientBean.get();
    try {
      SearchResponse response = client.getClient()
        .prepareSearch(collection)
        .setQuery(QueryBuilders.queryStringQuery(query))
  //        .setPostFilter(QueryBuilders.simpleQueryStringQuery(filterQuery[0]))
        .setFrom(0)
        .setSize(0)
        .addDocValueField(statsField)
        .setExplain(true)
        .execute()
        .actionGet();

      // TODO
      return null;
    } catch (Exception e) {
      e.printStackTrace();
    }


//      solrQuery.setGetFieldStatistics(true);
//      solrQuery.setGetFieldStatistics(statsField);
//
//      SolrServerBean serverBean = SolrServerBean.get();
//      SolrClient solrServer = serverBean.getServer();
//
//      QueryResponse solrResponse = solrServer.query(collection, solrQuery);
//      FieldStatsInfo fieldStatsInfo = solrResponse.getFieldStatsInfo().get(statsField);
//
//      if (fieldStatsInfo != null) {
//        if ("min".equals(stats)) {
//          return (Double) fieldStatsInfo.getMin();
//        } else if ("max".equals(stats)) {
//          return (Double) fieldStatsInfo.getMax();
//        } else if ("count".equals(stats)) {
//          return fieldStatsInfo.getCount().doubleValue();
//        } else if ("missing".equals(stats)) {
//          return fieldStatsInfo.getMissing().doubleValue();
//        } else if ("mean".equals(stats)) {
//          return (Double) fieldStatsInfo.getMean();
//        } else if ("sum".equals(stats)) {
//          return (Double) fieldStatsInfo.getSum();
//        } else if ("stddev".equals(stats)) {
//          return fieldStatsInfo.getStddev();
//        } else if ("countDistinct".equals(stats)) {
//          return fieldStatsInfo.getCountDistinct().doubleValue();
//        }
//      }
//    } catch (Exception exception) {
//      exception.printStackTrace();
//    }
    return null;
  }

  public static Double getStats(String query, String[] filterQuery,
                         String statsField, String stats) {
    ESClientBean client = ESClientBean.get();
    return getStats(client.getCollection(), query, filterQuery, statsField, stats);
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
  public static String analyzeField(String collection,
                             String fieldName,
                             String fieldValue) {
    return analyzeField(collection, fieldName, fieldValue, ESRequestBean.PHASE_INDEX, ESRequestBean.DEFAULT_FILTER_CLASS, 0);
  }

  public static String analyzeField(String fieldName,
                                    String fieldValue,
                                    String analysisPhaseName,
                                    String filterClass,
                                    int tokenPosition) {
    ESClientBean client = ESClientBean.get();
    return analyzeField(client.getCollection(), fieldName, fieldValue, ESRequestBean.PHASE_INDEX, ESRequestBean.DEFAULT_FILTER_CLASS, 0);
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
  public static String analyzeField(String collection,
                             String fieldName,
                             String fieldValue,
                             String analysisPhaseName,
                             String filterClass,
                             int tokenPosition) {

//    try {
//      SolrClient server = SolrServerBean.get().getServer();
//
//      ModifiableSolrParams params = new ModifiableSolrParams();
//      params.set(AnalysisParams.FIELD_NAME, fieldName);
//      params.set(AnalysisParams.FIELD_VALUE, fieldValue);
//
//      FieldAnalysisRequest request = new FieldAnalysisRequest();
//      List<String> fieldNames = new ArrayList<String>();
//      fieldNames.add(fieldName);
//      request.setFieldNames(fieldNames);
//      request.setFieldValue(fieldValue);
//
//      FieldAnalysisResponse res = new FieldAnalysisResponse();
//      try {
//        res.setResponse(server.request(request, collection));
//      } catch (SolrServerException exception) {
//        exception.printStackTrace();
//        return fieldValue;
//      } catch (IOException exception) {
//        exception.printStackTrace();
//        return fieldValue;
//      }
//      FieldAnalysisResponse.Analysis analysis =
//        res.getFieldNameAnalysis(fieldName);
//
//      Iterable<AnalysisResponseBase.AnalysisPhase> phases =
//        ESRequestBean.PHASE_INDEX.equals(analysisPhaseName)
//          ? analysis.getIndexPhases() : analysis.getQueryPhases();
//      if (phases != null) {
//        Iterator<AnalysisResponseBase.AnalysisPhase> iterator =
//          phases.iterator();
//        while (iterator.hasNext()) {
//          AnalysisResponseBase.AnalysisPhase analysisPhase = iterator.next();
//          if (analysisPhase.getClassName()
//            .equals(filterClass)
//            && analysisPhase.getTokens().size() > 0) {
//            AnalysisResponseBase.TokenInfo token =
//              analysisPhase.getTokens().get(tokenPosition);
//            return token.getText();
//          }
//        }
//      }
//      return fieldValue;
//    } catch (Exception exception) {
//      exception.printStackTrace();
//      return fieldValue;
//    }
    return null;
  }

  public static String analyzeField(String fieldName,
                             String fieldValue) {
    ESClientBean client = ESClientBean.get();
    return analyzeField(client.getCollection(), fieldName, fieldValue);
  }

}
