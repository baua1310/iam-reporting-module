package de.uniregensburg.iamreportingmodule.core.exception;

/**
 * Exception for deleting entities
 *
 * @author Julian Bauer
 */
public class DeleteEntityException extends Exception {

    /**
     *
     * @param message
     */
    public DeleteEntityException(String message) {
        super(message);
    }

}