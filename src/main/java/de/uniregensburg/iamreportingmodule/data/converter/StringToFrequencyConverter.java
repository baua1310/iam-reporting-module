package de.uniregensburg.iamreportingmodule.data.converter;

import com.vaadin.flow.data.binder.ErrorMessageProvider;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import de.uniregensburg.iamreportingmodule.data.entity.Frequency;
import de.uniregensburg.iamreportingmodule.data.entity.Measurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.format.DateTimeParseException;

/**
 * Converter of string to frequency and vice versa
 *
 * @author Julian Bauer
 */
public class StringToFrequencyConverter implements Converter<String, Frequency> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ErrorMessageProvider errorMessageProvider;
    private final Measurable measurable;

    /**
     *
     * @param errorMessageProvider
     * @param measurable
     */
    public StringToFrequencyConverter(ErrorMessageProvider errorMessageProvider, Measurable measurable) {
        this.errorMessageProvider = errorMessageProvider;
        this.measurable = measurable;
    }

    /**
     *
     * @param errorMessage
     * @param measurable
     */
    public StringToFrequencyConverter(String errorMessage, Measurable measurable) {
        this(valueContext -> errorMessage, measurable);
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
     * Converts string to frequency
     *
     * @param fieldValue
     * @param context
     * @return
     */
    @Override
    public Result<Frequency> convertToModel(String fieldValue, ValueContext context) {
        logger.info("Converting string to frequency");
        // check string
        if (fieldValue == null) {
            logger.info("Field value is null");
            return Result.error(getErrorMessage(context));
        } else {
            // create frequency object
            try {
                Duration duration = Duration.parse(fieldValue);
                Frequency frequency = new Frequency(duration);
                logger.info("Duration: " + duration.toString());
                if (measurable != null) {
                    if (measurable.getFrequency() != null) {
                        frequency = measurable.getFrequency();
                        frequency.setDuration(duration);
                    }
                }
                // return frequency
                return Result.ok(frequency);
            } catch (DateTimeParseException e) {
                logger.info(e.getMessage());
                return Result.error(getErrorMessage(context));
            }
        }
    }

    /**
     * Converts frequency to string
     *
     * @param frequency
     * @param context
     * @return
     */
    @Override
    public String convertToPresentation(Frequency frequency, ValueContext context) {
        logger.info("Converting frequency to string");
        // check frequency
        if (frequency == null) {
            logger.info("Frequency is null");
            return "";
        }
        Duration duration = frequency.getDuration();
        // check duration
        if (duration == null) {
            logger.info("Duration is null");
            return "";
        }
        // return string
        return duration.toString();
    }
}
