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

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetAddress;

/**
 * Create a bean providing a connection to ES
 * Created by francois on 30/09/14.
 */
public class ESClientBean implements InitializingBean {

  private static ESClientBean instance;

  private TransportClient client;
  private String serverUrl;
  private String collection;
  private String username;
  private String password;

  /**
   * Get Solr server.
   * @return Return the bean instance
   */
  public static ESClientBean get() {
    return instance;
  }

  /**
   * The first time this method is called, ping the
   * client to check connection status.
   *
   * @return The Solr client instance.
   */
  public TransportClient getClient() throws Exception {
    return client;
  }


  /**
   * Connect to the Solr client, ping the client
   * to check connection and set the instance.
   *
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    if (serverUrl != null) {
//      if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
//
//      } else {
//
//      }

      client = new PreBuiltTransportClient(Settings.EMPTY)
        .addTransportAddress(new InetSocketTransportAddress(
          InetAddress.getByName("127.0.0.1"), 9300));


      synchronized (ESClientBean.class) {
        instance = this;
      }
    } else {
      throw new Exception(String.format("No Solr client URL defined in %s. "
          + "Check bean configuration.", this.serverUrl));
    }
  }

  /**
   * Return the Solr client URL.
   */
  public String getServerUrl() {
    return serverUrl;
  }

  /**
   * The Solr client URL.
   */
  public ESClientBean setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
    return this;
  }

  /**
   * Return Solr client username for credentials.
   */
  public String getUsername() {
    return username;
  }

  /**
   * The Solr client credentials username.
   */
  public ESClientBean setUsername(String username) {
    this.username = username;
    return this;
  }

  /**
   * Return Solr client password for credentials.
   */
  public String getPassword() {
    return password;
  }

  /**
   * The Solr client credentials password.
   */
  public ESClientBean setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getCollection() {
    return collection;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }
}
