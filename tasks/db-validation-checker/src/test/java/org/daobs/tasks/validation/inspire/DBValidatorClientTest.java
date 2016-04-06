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

import junit.framework.TestCase;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DBValidatorClientTest extends TestCase {

    /**
     * Propeties from "config.properties" file
     */
    private Properties props;
    /**
     * Postgres database service validator client
     */
    private DBValidatorClient validator;
    /**
     * Jdbc template to get metatadas uuids for tests
     */
    private JdbcTemplate template;
    /**
     * Postgres driver className
     */
    private static final String DB_DRIVER_CLASS_NAME = "org.postgresql.Driver";
    /**
     * Postgres geocatalogue database url
     */
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/catalog";
    /**
     * Postgres geocatalogue database username
     */
    private static final String DB_USERNAME = "www-data";
    /**
     * Postgres geocatalogue database password
     */
    private static final String DB_PASSWORD = "www-data";
    /**
     * SQL query to get a valid INSPIRE metadata
     */
    private static final String SQL_SELECT_VALID_METADATA_UUID =
            "select max(md.uuid) from geocat.md_validation_results mdvr, geocat.metadata md " +
                    "where mdv_rule_result = 'Métadonnée conforme' " +
                    "and mdv_rule_title='INSPIRE' " +
                    "and md.id = mdvr.mdv_md_id;";
    /**
     * SQL query to get an invalid INSPIRE metadata
     */
    private static final String SQL_SELECT_INVALID_METADATA_UUID =
            "select max(md.uuid) from geocat.md_validation_results mdvr, geocat.metadata md " +
                    "where mdv_rule_result != 'Métadonnée conforme' and " +
                        "mdv_rule_title='INSPIRE' and " +
                        "md.id = mdvr.mdv_md_id;";
    /**
     * Random unkown metadata uuid
     */
    private static final String UNKOWN_METADATA_UUID =
            "********-aabbccddeeff-112233445566-*********";


    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.props = loadProperties();
        DataSource dataSource = getDataSource();
        this.validator = new DBValidatorClient(dataSource,
                props.getProperty("task.db-validation-checker.db.validRuleResult"),
                props.getProperty("task.db-validation-checker.db.sql.selectMetadataValidationResultQuery"));
        this.template = new JdbcTemplate(dataSource);
    }


    /**
     * Get uuid of a valid INSPIRE metadata from database
     *
     * @return
     */
    public String getValidMetadataUUID() {
        String validMetadataUUID = null;
        List<String> result = template.query(SQL_SELECT_VALID_METADATA_UUID, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
        if (result.size() != 0) {
            validMetadataUUID = result.get(0);
        }
        return validMetadataUUID;
    }

    /**
     * Get uuid of an invalid INSPIRE metadata from database
     *
     * @return
     */
    public String getInvalidMetadataUUID() {
        String invalidMetadataUUID = null;
        List<String> result = template.query(SQL_SELECT_INVALID_METADATA_UUID, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
        if (result.size() != 0) {
            invalidMetadataUUID = result.get(0);
        }
        return invalidMetadataUUID;
    }

    /**
     * Get datasource
     *
     * @return Datasource
     * @throws IOException
     * @throws URISyntaxException
     */
    public DriverManagerDataSource getDataSource() throws IOException, URISyntaxException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        String driverClassName =
                props.containsKey("task.db-validation-checker.db.driverClassName") ?
                props.get("task.db-validation-checker.db.driverClassName").toString() :
                        DB_DRIVER_CLASS_NAME;
        String url =
                props.containsKey("task.db-validation-checker.db.url") ?
                props.get("task.db-validation-checker.db.url").toString() :
                        DB_URL;
        String username =
                props.containsKey("task.db-validation-checker.db.username") ?
                props.get("task.db-validation-checker.db.username").toString() :
                        DB_USERNAME;
        String password =
                props.containsKey("task.db-validation-checker.db.password") ?
                props.get("task.db-validation-checker.db.password").toString() :
                        DB_PASSWORD;

        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    /**
     * Get properties from config.properties file
     *
     * @return Properties file
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Properties loadProperties() throws IOException, URISyntaxException {
        Properties props = new Properties();
        URI configURI = Thread.currentThread()
                .getContextClassLoader()
                .getResource(System.getProperty("config.dir")).toURI();
        InputStream input = new FileInputStream(configURI.getPath());
        props.load(input);
        input.close();
        return props;
    }

    /**
     * Test to check validation of a valid metadata
     *
     * @throws DataAccessException
     * @throws SQLException
     */
    @org.junit.Test
    public void testValidMetadataValidation() throws DataAccessException, SQLException {
        String validMetadataUUID = getValidMetadataUUID();
        ValidationReport report = validator.validate(validMetadataUUID);
        assertNotNull(report);
        assertEquals(report.getStatus(), true);
        assertEquals(report.isAboveThreshold(), true);
    }

    /**
     * Test to check validation of an invalid metadata
     *
     * @throws DataAccessException
     * @throws SQLException
     */
    @org.junit.Test
    public void testInvalidMetadataValidation() throws DataAccessException, SQLException {
        String invalidMetadataUUID = getInvalidMetadataUUID();
        ValidationReport report = validator.validate(invalidMetadataUUID);
        assertNotNull(report);
        assertEquals(report.getStatus(), false);
        assertEquals(report.isAboveThreshold(), false);
    }


    /**
     * Tezst to check validation of an unknown metadata
     *
     * @throws DataAccessException
     * @throws SQLException
     */
    @org.junit.Test
    public void testUnknownMetadataValidation() throws DataAccessException, SQLException {
        ValidationReport report = validator.validate(UNKOWN_METADATA_UUID);
        assertNotNull(report);
        assertEquals(report.getStatus(), false);
        assertEquals(report.isAboveThreshold(), false);
    }


}