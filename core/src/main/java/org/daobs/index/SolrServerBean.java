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

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * Create a bean providing a connection to the
 * Solr client.
 * Created by francois on 30/09/14.
 */
public class SolrServerBean implements InitializingBean {

  private static SolrServerBean instance;

  private SolrClient client;
  private String solrServerUrl;
  private String solrServerCore;
  private String solrServerUsername;
  private String solrServerPassword;
  private boolean connectionChecked = false;

  /**
   * Get Solr server.
   * @return Return the bean instance
   */
  public static SolrServerBean get() {
    return instance;
  }

  /**
   * The first time this method is called, ping the
   * client to check connection status.
   *
   * @return The Solr client instance.
   */
  public SolrClient getServer() throws Exception {
    if (!connectionChecked) {
      this.ping();
      connectionChecked = true;
    }
    return client;
  }

  public SolrServerBean setServer(SolrClient server) {
    this.client = server;
    return this;
  }

  /**
   * Connect to the Solr client, ping the client
   * to check connection and set the instance.
   *
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    if (solrServerUrl != null) {
      if (!StringUtils.isEmpty(solrServerUsername) && !StringUtils.isEmpty(solrServerPassword)) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(solrServerUsername, solrServerPassword));
        CloseableHttpClient httpClient =
            HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        client = new HttpSolrClient(this.solrServerUrl, httpClient);
      } else {
        client = new HttpSolrClient(this.solrServerUrl);
      }

      synchronized (SolrServerBean.class) {
        instance = this;
      }
    } else {
      throw new Exception(String.format("No Solr client URL defined in %s. "
          + "Check bean configuration.", this.solrServerUrl));
    }
  }

  /**
   * Ping the Solr client.
   *
   */
  private void ping() throws Exception {
    try {
      client.ping();
    } catch (Exception exception) {
      throw new Exception(
        String.format("Failed to ping Solr client at URL %s. "
            + "Check bean configuration.",
          this.solrServerUrl),
        exception);
    }
  }

  /**
   * Return the Solr client URL.
   */
  public String getSolrServerUrl() {
    return solrServerUrl;
  }

  /**
   * The Solr client URL.
   */
  public SolrServerBean setSolrServerUrl(String solrServerUrl) {
    this.solrServerUrl = solrServerUrl;
    return this;
  }

  /**
   * Return the Solr core to connect to.
   */
  public String getSolrServerCore() {
    return solrServerCore;
  }

  /**
   * The Solr core to connect to.
   */
  public SolrServerBean setSolrServerCore(String solrServerCore) {
    this.solrServerCore = solrServerCore;
    return this;
  }

  /**
   * Return Solr client username for credentials.
   */
  public String getSolrServerUsername() {
    return solrServerUsername;
  }

  /**
   * The Solr client credentials username.
   */
  public SolrServerBean setSolrServerUsername(String solrServerUsername) {
    this.solrServerUsername = solrServerUsername;
    return this;
  }

  /**
   * Return Solr client password for credentials.
   */
  public String getSolrServerPassword() {
    return solrServerPassword;
  }

  /**
   * The Solr client credentials password.
   */
  public SolrServerBean setSolrServerPassword(String solrServerPassword) {
    this.solrServerPassword = solrServerPassword;
    return this;
  }
}
