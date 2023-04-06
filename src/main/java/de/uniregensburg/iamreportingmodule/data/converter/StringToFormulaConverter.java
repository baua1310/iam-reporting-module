package de.uniregensburg.iamreportingmodule.data.converter;

import com.vaadin.flow.data.binder.ErrorMessageProvider;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import de.uniregensburg.iamreportingmodule.data.entity.Formula;
import de.uniregensburg.iamreportingmodule.data.entity.Metric;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.exception.FormulaException;
import de.uniregensburg.iamreportingmodule.core.util.FormulaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter of string to formula and vice versa
 *
 * @author Julian Bauer
 */
public class StringToFormulaConverter implements Converter<String, Formula> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ErrorMessageProvider errorMessageProvider;
    private final FormulaUtil util;
    private final Metric metric;

    /**
     *
     * @param errorMessageProvider
     * @param service
     * @param metric
     */
    public StringToFormulaConverter(ErrorMessageProvider errorMessageProvider, MeasurableService service, Metric metric) {
        this.errorMessageProvider = errorMessageProvider;
        this.util = new FormulaUtil(service);
        this.metric = metric;
    }

    /**
     *
     * @param errorMessage
     * @param service
     * @param metric
     */
    public StringToFormulaConverter(String errorMessage, MeasurableService service, Metric metric) {
        this(valueContext -> errorMessage, service, metric);
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
     * Converts string to formula
     *
     * @param fieldValue
     * @param context
     * @return
     */
    @Override
    public Result<Formula> convertToModel(String fieldValue, ValueContext context) {
        logger.info("Converting string to formula");
        // check string
        if (fieldValue == null) {
            logger.info("Field value is null");
            return Result.error(getErrorMessage(context));
        } else {
            // create formula object
            Formula formula = new Formula(fieldValue);
            if (metric != null) {
                if (metric.getFormula() != null) {
                    formula = metric.getFormula();
                    formula.setFormula(fieldValue);
                }
            }
            try {
                formula.setMeasurables(util.getMeasurables(fieldValue));
            } catch (FormulaException e) {
                logger.info(e.getMessage());
                return Result.error(e.getMessage());
            }
            // return formula
            return Result.ok(formula);
        }
    }

    /**
     * Converts formula to string
     *
     * @param formula
     * @param context
     * @return
     */
    @Override
    public String convertToPresentation(Formula formula, ValueContext context) {
        logger.info("Converting formula to string");
        // check formula
        if (formula == null) {
            logger.info("Formula is null");
            return "";
        } else {
            // return string
            return formula.getFormula();
        }
    }
}
