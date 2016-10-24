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

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.fieldstats.FieldStatsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
      SearchRequestBuilder srb = client.getClient()
        .prepareSearch(collection)
        .setQuery(QueryBuilders.queryStringQuery(query));

      if (filterQuery != null) {
        String filters = "";
        for (String filter : filterQuery) {
          filters = filter + " ";
        }
        srb.setPostFilter(QueryBuilders.queryStringQuery(filters));
      }
      SearchResponse response =
        srb.setFrom(0)
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
      FieldStatsResponse response = client.getClient()
        .prepareFieldStats()
        .setFields(statsField)
        .setIndices(collection)
//        .setQuery(QueryBuilders.queryStringQuery(query))
  //        .setPostFilter(QueryBuilders.simpleQueryStringQuery(filterQuery[0]))
        .execute()
        .actionGet();
      // TODO: Subset to filter - We should probably use aggregates

      // TODO: Return correct stat info
      return (double)response.getAllFieldStats().get(statsField).getMaxValue();
    } catch (Exception e) {
      e.printStackTrace();
    }

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
    return null;
  }

  public static Double getStats(String query, String[] filterQuery,
                         String statsField, String stats) {
    ESClientBean client = ESClientBean.get();
    return getStats(client.getCollection(), query, filterQuery, statsField, stats);
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
   *
   * @return The analyzed string value if found or the field value if not found.
   */
  public static String analyzeField(String collection,
                             String analyzer,
                             String fieldValue) {

    ESClientBean client = ESClientBean.get();
    AnalyzeRequest request = (new AnalyzeRequest(collection).text(fieldValue)).analyzer(analyzer);
    try {
      List<AnalyzeResponse.AnalyzeToken> tokens =
        client.getClient().admin().indices().analyze(request).actionGet().getTokens();
      Iterator<AnalyzeResponse.AnalyzeToken> iterator = tokens.iterator();
      while (iterator.hasNext()) {
        AnalyzeResponse.AnalyzeToken t = iterator.next();
        if (t.getType().equals("SYNONYM")) {
          return t.getTerm();
        }
      }
    } catch (Exception e) {
      return fieldValue;
    }

    return null;
  }

  public static String analyzeField(String fieldName,
                             String fieldValue) {
    ESClientBean client = ESClientBean.get();
    return analyzeField(client.getCollection(), fieldName, fieldValue);
  }

}
