package de.uniregensburg.iamreportingmodule.core.exception;

/**
 * Exception for scheduling jobs
 *
 * @author Julian Bauer
 */
public class JobSchedulingException extends Exception {

    /**
     *
     * @param message
     */
    public JobSchedulingException(String message) {
        super(message);
    }

}