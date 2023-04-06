package de.uniregensburg.iamreportingmodule.data.validator;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.exception.FormulaException;
import de.uniregensburg.iamreportingmodule.core.util.FormulaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator for formulas
 *
 * @author Julian Bauer
 */
public class FormulaValidator implements Validator<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FormulaUtil util;

    /**
     *
     * @param service
     */
    public FormulaValidator(MeasurableService service) {
        this.util = new FormulaUtil(service);
    }

    /**
     * Validates formula
     *
     * @param value the first function argument
     * @param context the second function argument
     * @return
     */
    @Override
    public ValidationResult apply(String value, ValueContext context) {
        // check formula
        if (value == null) {
            logger.info("Provided formula is null");
            return ValidationResult.error("Formula is null");
        }
        try {
            // validate formula
            String formula = util.replaceVariablesWithMeasurableValues(value);
            Expression expression = new Expression(formula);
            expression.validate();
            // return ok
            return ValidationResult.ok();
        } catch (FormulaException e) {
            logger.info("Formula exception: " + e.getMessage());
            // return error
            return ValidationResult.error(e.getMessage());
        } catch (ParseException e) {
            logger.info("Parse exception: " + e.getMessage());
            // return error
            return ValidationResult.error(e.getMessage());
        }
    }
}
