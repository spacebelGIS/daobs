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

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.daobs.index.SolrServerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

/**
 * A simple dashboard loader.
 * Created by francois on 03/02/15.
 */
public class DashboardLoader {

  /**
   * Sample dashboard file format should be.
   */
  public static final String dashboardSampleFilePattern = "([A-Z]*)-.*.json";
  private static final Pattern p = Pattern.compile(dashboardSampleFilePattern);

  @Resource(name = "dataSolrServer")
  SolrServerBean server;

  private String collection;

  /**
   * Load all JSON files matching the fileFilter
   * in the directory provided.
   *
   */
  public Map<String, List<String>> load(String directory, String fileFilter) {
    if (fileFilter.contains("..")) {
      throw new SecurityException(
          "No dashboard can be loaded when the file matching pattern contains '..'.");
    }

    Map<String, List<String>> report = new HashMap<>();
    List<String> success = new ArrayList<String>();
    List<String> errors = new ArrayList<String>();

    try (DirectoryStream<Path> directoryStream =
           Files.newDirectoryStream(Paths.get(directory), fileFilter)) {
      for (Path path : directoryStream) {
        if (path != null) {
          Path pathFileName = path.getFileName();
          if (pathFileName != null) {
            try {
              loadData(path.toString());
              success.add(pathFileName.toString());
            } catch (Exception exception) {
              errors.add(String.format(
                  "Failed to load %s. Error is %s",
                  pathFileName,
                  exception.getMessage()));
            }
          }
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    report.put("success", success);
    report.put("errors", errors);

    return report;
  }

  /**
   * Load a JSON file to the Solr core.
   * id and title fields are populated with the
   * dashboard title property.
   *
   */
  public void loadData(String fileToLoad) throws Exception {
    File file = new File(fileToLoad);

    String json = com.google.common.io.Files.toString(file, Charsets.UTF_8);

    SolrClient client = server.getServer();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode dashboardConfig = mapper.readTree(json);

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", dashboardConfig.get("title").asText());
    doc.addField("title", dashboardConfig.get("title").asText());
    doc.addField("type", "dashboard");
    doc.addField("user", "guest");
    doc.addField("group", "guest");
    doc.addField("dashboard", json);
    client.add(collection, doc);
    client.commit(collection);
  }


  /**
   * Browse the folder for resources and return a sorted list of values.
   *
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
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return listOfDashboards;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }
}
