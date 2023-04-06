package de.uniregensburg.iamreportingmodule.data.converter;

import com.vaadin.flow.data.binder.ErrorMessageProvider;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter of string to port and vice versa
 *
 * @author Julian Bauer
 */
public class StringToPortConverter implements Converter<String, Integer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ErrorMessageProvider errorMessageProvider;
    private int min;
    private int max;

    /**
     *
     * @param errorMessageProvider
     * @param min
     * @param max
     */
    public StringToPortConverter(ErrorMessageProvider errorMessageProvider, int min, int max) {
        this.errorMessageProvider = errorMessageProvider;
    }

    /**
     *
     * @param errorMessageProvider
     */
    public StringToPortConverter(ErrorMessageProvider errorMessageProvider) {
        this.min = 1;
        this.max = 65535;
        this.errorMessageProvider = errorMessageProvider;
    }

    /**
     *
     * @param errorMessage
     */
    public StringToPortConverter(String errorMessage) {
        this(valueContext -> errorMessage);
    }

    /**
     *
     * @param context
     * @return
     */
    private String getErrorMessage(ValueContext context) {
        return errorMessageProvider.apply(context);
    }

    /**
     * Converts string to port
     *
     * @param fieldValue
     * @param context
     * @return
     */
    @Override
    public Result<Integer> convertToModel(String fieldValue, ValueContext context) {
        logger.info("Converting string to port");
        // check string
        if (fieldValue == null) {
            logger.info("Field value is null");
            return Result.error(getErrorMessage(context));
        } else {
            // create port
            Integer port = Integer.valueOf(fieldValue);
            // check port
            if (port >= min && port <= max) {
                // return port
                return Result.ok(port);
            } else {
                return Result.error(getErrorMessage(context));
            }
        }
    }

    /**
     * Converts port to string
     *
     * @param port
     * @param context
     * @return
     */
    @Override
    public String convertToPresentation(Integer port, ValueContext context) {
        logger.info("Converting port to string");
        // check port
        if (port == null) {
            logger.info("Port is null");
            return "";
        } else {
            // return string
            return port.toString();
        }
    }
}
