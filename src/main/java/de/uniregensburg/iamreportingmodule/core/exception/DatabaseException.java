package de.uniregensburg.iamreportingmodule.core.exception;

/**
 * Exception for database tasks
 *
 * @author Julian Bauer
 */
public class DatabaseException extends Exception {

    /**
     *
     * @param message
     */
    public DatabaseException(String message) {
        super(message);
    }

}
