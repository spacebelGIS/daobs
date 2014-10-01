package org.daobs.index;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.common.params.AnalysisParams;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to run Solr requests like field analysis.
 *
 * Created by francois on 30/09/14.
 */
public class SolrRequestBean {
    private static String PHASE_INDEX = "index";
    private static String PHASE_QUERY = "query";
    private static String DEFAULT_FILTER_CLASS = "org.apache.lucene.analysis.synonym.SynonymFilter";

    /**
     * Analyze a field and a value against the index phase
     * and return the first value generated
     * by the {@see DEFAULT_FILTER_CLASS}.
     *
     * The field tested MUST use a DEFAULT_FILTER_CLASS
     * in the analyzer chain.
     *
     * See {@see analyzeField}.
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static String analyzeField(String fieldName,
                                      String fieldValue) {
        return analyzeField(fieldName, fieldValue, PHASE_INDEX, DEFAULT_FILTER_CLASS, 0);
    }

    /**
     * Analyze a field and a value against the index
     * or query phase and return the first value generated
     * by the specified filterClass.
     *
     * If an exception occured, the field value is returned.
     *
     * Equivalent to: {@linkplain http://localhost:8983/solr/analysis/field?analysis.fieldname=inspireTheme_syn&q=hoogte}
     *
     * TODO: Logger.
     *
     * @param fieldName The field name
     * @param fieldValue    The field value to analyze
     * @param analysisPhaseName The analysis phase (ie. "index" or "query")
     * @param filterClass   The filter class the response should be extracted from
     * @param tokenPosition The position of the token to extract
     *
     * @return  The analyzed string value if found or the field value if not found.
     */
    public static String analyzeField(String fieldName,
                                      String fieldValue,
                                      String analysisPhaseName,
                                      String filterClass,
                                      int tokenPosition) {

        try {
            SolrServer server = SolrServerBean.get().getServer();

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
                res.setResponse(server.request(request));
            } catch (SolrServerException e) {
                e.printStackTrace();
                return fieldValue;
            } catch (IOException e) {
                e.printStackTrace();
                return fieldValue;
            }
            FieldAnalysisResponse.Analysis analysis =
                    res.getFieldNameAnalysis(fieldName);

            Iterable<AnalysisResponseBase.AnalysisPhase> phases =
                    PHASE_INDEX.equals(analysisPhaseName) ?
                            analysis.getIndexPhases() : analysis.getQueryPhases();
            if (phases != null) {
                Iterator<AnalysisResponseBase.AnalysisPhase> iterator =
                        phases.iterator();
                while (iterator.hasNext()) {
                    AnalysisResponseBase.AnalysisPhase analysisPhase = iterator.next();
                    if (analysisPhase.getClassName()
                            .equals(filterClass) &&
                            analysisPhase.getTokens().size() > 0) {
                        AnalysisResponseBase.TokenInfo token =
                                analysisPhase.getTokens().get(tokenPosition);
                        return token.getText();
                    }
                }
            }
            return fieldValue;
        } catch (Exception e) {
            e.printStackTrace();
            return fieldValue;
        }
    }
}
