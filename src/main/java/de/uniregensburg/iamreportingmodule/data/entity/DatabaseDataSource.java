package de.uniregensburg.iamreportingmodule.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Entity database data source extends data source
 * Attributes: host (String), port (int), database (String), dbmsType (Dbms), username (String), password (String)
 *
 * @author Julian Bauer
 */
@Entity
public class DatabaseDataSource extends DataSource {

    /**
     *
     */
    public DatabaseDataSource() {
        setType(DataSourceType.DATABASE);
    }

    /**
     *
     * @param dbmsType
     */
    public DatabaseDataSource(Dbms dbmsType) {
        setType(DataSourceType.DATABASE);
        this.dbmsType = dbmsType;
    }

    @NotBlank
    private String host;

    @NotNull
    private int port;

    @NotBlank
    private String database;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Dbms dbmsType;

    @NotNull
    private String username;

    @NotNull
    private String password;

    /**
     * Returns host
     *
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets host
     *
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Returns port
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets port
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns database
     *
     * @return
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Sets database
     *
     * @param database
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * Returns dbms type
     *
     * @return
     */
    public Dbms getDbmsType() {
        return dbmsType;
    }

    /**
     * Sets dbms type
     *
     * @param dbmsType
     */
    public void setDbmsType(Dbms dbmsType) {
        this.dbmsType = dbmsType;
    }

    /**
     * Returns username
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns password
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
