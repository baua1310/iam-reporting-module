package de.uniregensburg.iamreportingmodule.core.util;

import de.uniregensburg.iamreportingmodule.core.exception.DatabaseException;
import de.uniregensburg.iamreportingmodule.data.entity.DatabaseDataSource;
import de.uniregensburg.iamreportingmodule.data.entity.Dbms;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Properties;

/**
 * Utility to execute database queries
 *
 * @author Julian Bauer
 */
public class DatabaseUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DatabaseDataSource databaseDataSource;

    /**
     *
     * @param databaseDataSource
     */
    public DatabaseUtil(DatabaseDataSource databaseDataSource) {
        this.databaseDataSource = databaseDataSource;
    }

    /**
     * Tests database connection
     *
     * @return
     * @throws DatabaseException
     */
    public boolean testConnection() throws DatabaseException {
        boolean success;

        logger.info("Testing database connection");

        // try connecting to database
        try (Connection conn = getConnection()) {
            success = (conn != null);
        } catch (SQLException e) {
            logger.info("Connection failed");
            logger.info(e.getMessage());
            throw new DatabaseException(e.getMessage());
        }

        if (success) {
            logger.info("Connection established");
        } else {
            logger.info("Connection failed");
        }

        // return connection result
        return success;
    }

    /**
     * Returns database connection
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        // get and return database connection by dbms type
        if (databaseDataSource.getDbmsType().equals(Dbms.POSTGRESQL)) {
            return getPostgresqlConnection();
        } else {
            logger.info("Method not implemented yet for " + databaseDataSource.getDbmsType());
            return null; // not implemented yet
        }
    }

    /**
     * Returns postgresql database connection
     *
     * @return
     * @throws SQLException
     */
    private Connection getPostgresqlConnection() throws SQLException {
        // setup jdbc postgresql connection url: host, port and database
        String url = "jdbc:" +
                "postgresql://" +
                databaseDataSource.getHost() +
                ":" + databaseDataSource.getPort() +
                "/" + databaseDataSource.getDatabase();

        // append credentials: username and password
        Properties props = new Properties();
        if (!databaseDataSource.getUsername().isEmpty()) {
            props.setProperty("user", databaseDataSource.getUsername());
        }
        if (!databaseDataSource.getPassword().isEmpty()) {
            props.setProperty("password", databaseDataSource.getPassword());
        }

        logger.info("Returning connection url " + url + " and properties username " + databaseDataSource.getUsername()
                + " and password ******* (not logged)");

        // return postgresql connection
        return DriverManager.getConnection(url, props);
    }

    /**
     * Returns measurement result of executing sql query
     *
     * @param query
     * @return
     * @throws DatabaseException
     */
    public Result measure(String query) throws DatabaseException {
        logger.info("Executing query " + query);
        // open connection
        try (Statement stmt = getConnection().createStatement()) {
            // execute query
            ResultSet rs = stmt.executeQuery(query);
            // get value
            rs.next();
            BigDecimal value = rs.getBigDecimal(1);
            logger.info("Query result: " + value);
            // return result
            return new Result(value);
        } catch (SQLException e) {
            logger.info(e.toString());
            throw new DatabaseException(e.getMessage());
        }
    }

}
