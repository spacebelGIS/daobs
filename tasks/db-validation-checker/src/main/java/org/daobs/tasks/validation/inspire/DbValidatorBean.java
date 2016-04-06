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

package org.daobs.tasks.validation.inspire;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

/**
 * Database validator bean.
 * @author csachot
 */
public class DbValidatorBean {

  /**
   * Metatadata id key in the exchange header.
   */
  private static final String METADATA_ID_KEY = "documentIdentifier";
  /**
   * DbValidatorClient that queries the database to get the metadata's validation report.
   */
  private DbValidatorClient dbValidatorClient;

  /**
   * Constructor.
   *
   * @param postgresServiceValidatorClient DbValidatorClient
   *                                       that queries the database to get
   *                                       the metadata's validation report
   */
  public DbValidatorBean(DbValidatorClient postgresServiceValidatorClient) {
    this.dbValidatorClient = postgresServiceValidatorClient;
  }

  /**
   * Get the input message body and validate
   * it against the INSPIRE validation service.
   * The output body contains the validation report.
   * Headers are propagated.
   *
   *
   */
  public void validateBody(
      @Header(METADATA_ID_KEY) String metadataId,
      Exchange exchange) {
    ValidationReport report = null;
    try {
      report = dbValidatorClient.validate(metadataId);
    } catch (DataAccessException | SQLException e1) {
      e1.printStackTrace();
    }
    exchange.getOut().setBody(report);
    exchange.getOut().setHeaders(exchange.getIn().getHeaders());
  }
}
