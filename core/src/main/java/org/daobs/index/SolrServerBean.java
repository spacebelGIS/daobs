package org.daobs.index;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * Create a bean providing a connection to the
 * Solr server.
 *
 * Created by francois on 30/09/14.
 */
public class SolrServerBean implements InitializingBean {

    private static SolrServerBean instance;

    private SolrServer server;
    private String solrServerUrl;
    private String solrServerCore;
    private String solrServerUsername;
    private String solrServerPassword;
    private boolean connectionChecked = false;

    /**
     * The first time this method is called, ping the
     * server to check connection status.
     *
     * @return  The Solr server instance.
     */
    public SolrServer getServer() throws Exception {
        if (!connectionChecked) {
            this.ping();
            connectionChecked = true;
        }
        return server;
    }

    public SolrServerBean setServer(SolrServer server) {
        this.server = server;
        return this;
    }

    /**
     * Connect to the Solr server, ping the server
     * to check connection and set the instance.
     *
     * @throws Exception
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
                server = new HttpSolrServer(this.solrServerUrl, httpClient);
            } else {
                server = new HttpSolrServer(this.solrServerUrl);
            }

            instance = this;
        } else {
            throw new Exception(String.format("No Solr server URL defined in %s. " +
                    "Check bean configuration.", this.solrServerUrl));
        }
    }

    /**
     * Ping the Solr server.
     *
     * @throws Exception
     */
    private void ping() throws Exception {
        try {
            server.ping();
        } catch (Exception e) {
            throw new Exception(
                    String.format("Failed to ping Solr server at URL %s. " +
                        "Check bean configuration.",
                        this.solrServerUrl),
                    e);
        }
    }

    /**
     *
     * @return  Return the bean instance
     */
    public static SolrServerBean get() {
        return instance;
    }

    /**
     *
     * @return Return the Solr server URL
     */
    public String getSolrServerUrl() {
        return solrServerUrl;
    }

    /**
     *
     * @param solrServerUrl The Solr server URL
     */
    public SolrServerBean setSolrServerUrl(String solrServerUrl) {
        this.solrServerUrl = solrServerUrl;
        return this;
    }

    /**
     *
     * @return Return the Solr core to connect to
     */
    public String getSolrServerCore() {
        return solrServerCore;
    }

    /**
     *
     * @param solrServerCore The Solr core to connect to
     */
    public SolrServerBean setSolrServerCore(String solrServerCore) {
        this.solrServerCore = solrServerCore;
        return this;
    }

    /**
     *
     * @return Return Solr server username for credentials
     */
    public String getSolrServerUsername() {
        return solrServerUsername;
    }

    /**
     *
     * @param solrServerUsername    The Solr server credentials username
     */
    public SolrServerBean setSolrServerUsername(String solrServerUsername) {
        this.solrServerUsername = solrServerUsername;
        return this;
    }

    /**
     *
     * @return Return Solr server password for credentials
     */
    public String getSolrServerPassword() {
        return solrServerPassword;
    }

    /**
     *
     * @param solrServerPassword    The Solr server credentials password
     */
    public SolrServerBean setSolrServerPassword(String solrServerPassword) {
        this.solrServerPassword = solrServerPassword;
        return this;
    }
}
