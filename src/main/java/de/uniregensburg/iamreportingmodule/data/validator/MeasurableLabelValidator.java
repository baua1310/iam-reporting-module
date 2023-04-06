package de.uniregensburg.iamreportingmodule.data.validator;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import de.uniregensburg.iamreportingmodule.data.entity.Measurable;
import de.uniregensburg.iamreportingmodule.data.entity.Measurement;
import de.uniregensburg.iamreportingmodule.data.entity.Metric;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Validator for labels of measurables
 *
 * @author Julian Bauer
 */
public class MeasurableLabelValidator implements Validator<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeasurableService service;
    private final Measurable measurable;

    /**
     *
     * @param service
     * @param measurable
     */
    public MeasurableLabelValidator(MeasurableService service, Measurable measurable) {
        this.measurable = measurable;
        this.service = service;
    }

    /**
     * Validates label of measurable
     *
     * @param value the first function argument
     * @param context the second function argument
     * @return
     */
    @Override
    public ValidationResult apply(String value, ValueContext context) {
        // check label
        if (value == null) {
            logger.info("Provided label is null");
            return ValidationResult.error("Label is null");
        }
        // check if measurable is not null
        if (measurable != null) {
            logger.info("Checking if label for validation is equal to current label of measurable");
            // check if label equals
            if (value.equals(measurable.getLabel())) {
                logger.info("Provided label equals current label");
                // return ok
                return ValidationResult.ok();
            } else {
                logger.info("Provided label differs current label");
            }
        }
        logger.info("Collecting labels of all measurables");
        // adding all already used labels to set
        Set<String> labels = new HashSet<>();
        for (Measurement measurement : service.findAllMeasurements()) {
            labels.add(measurement.getLabel());
        }
        for (Metric metric : service.findAllMetrics()) {
            labels.add(metric.getLabel());
        }
        logger.info("Checking if label for validation with value " + value + " is unique");
        // checking if label is unique
        if (labels.contains(value)) {
            logger.info("Label is not unique");
            // return error: not unique
            return ValidationResult.error("Label not unique");
        } else {
            // return ok: unique
            logger.info("Label is unique");
            return ValidationResult.ok();
        }
    }
}
