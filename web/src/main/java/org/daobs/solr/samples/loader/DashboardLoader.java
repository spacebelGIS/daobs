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
package org.daobs.solr.samples.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple dashboard loader
 *
 * Created by francois on 03/02/15.
 */
public class DashboardLoader {

    /**
     * Sample dashboard file format should be
     */
    public static final String dashboardSampleFilePattern = "([A-Z]*)-.*.json";
    private static final Pattern p = Pattern.compile(dashboardSampleFilePattern);
    private String solrServerUsername;
    private String solrServerPassword;
    private String solrServerUrl;

    /**
     * Load all JSON files matching the fileFilter
     * in the directory provided.
     *
     * @param directory
     * @param fileFilter
     * @return
     */
    public Map<String, List<String>> load(String directory, String fileFilter) {
        if (fileFilter.contains("..")) {
            throw new SecurityException("No dashboard can be loaded when the file matching pattern contains '..'.");
        }

        Map<String, List<String>> report = new HashMap<>();
        report.put("success", new ArrayList<String>());
        report.put("errors", new ArrayList<String>());

        try (DirectoryStream<Path> directoryStream =
                 Files.newDirectoryStream(Paths.get(directory), fileFilter)) {
            for (Path path : directoryStream) {
                try {
                    loadData(path.toString());
                    report.get("success").add(path.getFileName().toString());
                } catch (Exception e) {
                    report.get("errors").add(
                        String.format("Failed to load %s. Error is %s",
                            path.getFileName().toString(),
                            e.getMessage()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }

    /**
     * Load a JSON file to the Solr core.
     * id and title fields are populated with the
     * dashboard title property.
     *
     *
     * @param fileToLoad
     * @return
     * @throws Exception
     */
    public String loadData(String fileToLoad) throws Exception {
        File file = new File(fileToLoad);

        String json = com.google.common.io.Files.toString(file, Charsets.UTF_8);

        HttpSolrClient server = null;
        if (!StringUtils.isEmpty(solrServerUsername) && !StringUtils.isEmpty(solrServerPassword)) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(solrServerUsername, solrServerPassword));
            CloseableHttpClient httpClient =
                HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
            server = new HttpSolrClient(solrServerUrl, httpClient);
        } else {
            server = new HttpSolrClient(solrServerUrl);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode dashboardConfig = mapper.readTree(json);

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", dashboardConfig.get("title").asText());
        doc.addField("title", dashboardConfig.get("title").asText());
        doc.addField("user", "guest");
        doc.addField("group", "guest");
        doc.addField("dashboard", json);
        server.add(doc);
        server.commit();

        return null;
    }

    public void setSolrServerUsername(String solrServerUsername) {
        this.solrServerUsername = solrServerUsername;
    }

    public void setSolrServerPassword(String solrServerPassword) {
        this.solrServerPassword = solrServerPassword;
    }

    public void setSolrServerUrl(String solrServerUrl) {
        this.solrServerUrl = solrServerUrl;
    }

    /**
     * Browse the folder for resources and return a sorted list of values.
     *
     * @param directory
     * @param aggregateByFilePattern
     * @return
     */
    public Set<String> getListOfResources(String directory, boolean aggregateByFilePattern) {
        Set<String> listOfDashboards = new TreeSet<>();
        try (DirectoryStream<Path> directoryStream =
                 Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                String fileName = path.toFile().getName();
                if (aggregateByFilePattern) {
                    Matcher matcher = p.matcher(fileName);
                    while (matcher.find()) {
                        if (!Strings.isNullOrEmpty(matcher.group(1))) {
                            listOfDashboards.add(matcher.group(1));
                        }
                    }
                } else {
                    listOfDashboards.add(fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfDashboards;
    }
}
