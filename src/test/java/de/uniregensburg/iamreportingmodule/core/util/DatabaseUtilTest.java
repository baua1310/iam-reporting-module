package de.uniregensburg.iamreportingmodule.core.util;

import de.uniregensburg.iamreportingmodule.core.exception.DatabaseException;
import de.uniregensburg.iamreportingmodule.data.entity.DatabaseDataSource;
import de.uniregensburg.iamreportingmodule.data.entity.Dbms;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests database util: connection to postgresql database and execution of simple select statement
 * <p>
 * Requirements:
 * <p>
 * Postgresql database:
 * host=localhost, port=5432, database=test, username=test, password=Test123!
 * <p>
 * Tables: accounts
 * CREATE TABLE users (username character varying(50) NOT NULL);
 * <p>
 * Data:
 * INSERT INTO users (username) VALUES ('user');
 * INSERT INTO users (username) VALUES ('admin');
 * <p>
 * Rights:
 * GRANT ALL ON TABLE public.users TO test;
 *
 * @author Julian Bauer
 *
 */
public class DatabaseUtilTest {
    private DatabaseUtil databaseUtil;

    /**
     * Initializes database util
     */
    @Before
    public void setupData() {
        DatabaseDataSource postgresqlDataSource = new DatabaseDataSource();
        postgresqlDataSource.setDbmsType(Dbms.POSTGRESQL);
        postgresqlDataSource.setHost("localhost");
        postgresqlDataSource.setPort(5432);
        postgresqlDataSource.setDatabase("test");
        postgresqlDataSource.setUsername("test");
        postgresqlDataSource.setPassword("Test123!");
        databaseUtil = new DatabaseUtil(postgresqlDataSource);
    }

    /**
     * Tests database connection
     *
     * @throws DatabaseException
     */
    @Test
    public void dbConnection() throws DatabaseException {
        Assert.assertTrue(databaseUtil.testConnection());
    }

    /**
     * Tests executing sql queries: counts rows in table users
     *
     * @throws DatabaseException
     */
    @Test
    public void countAccounts() throws DatabaseException {
        String query = "SELECT COUNT(*) FROM users;";
        Result result = databaseUtil.measure(query);
        BigDecimal expected = new BigDecimal(2);
        BigDecimal actual = result.getValue();
        Assert.assertEquals(expected,actual);
    }
}
