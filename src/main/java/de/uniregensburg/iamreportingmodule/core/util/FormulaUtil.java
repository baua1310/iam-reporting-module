package de.uniregensburg.iamreportingmodule.core.util;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import de.uniregensburg.iamreportingmodule.core.exception.FormulaException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.data.entity.Measurable;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility to validate and calculate formulas
 *
 * @author Julian Bauer
 */
public class FormulaUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeasurableService service;

    /**
     *
     * @param service
     */
    public FormulaUtil(MeasurableService service) {
        this.service = service;
    }

    /**
     * Calculates results based on formula
     *
     * @param formula
     * @return
     * @throws FormulaException
     */
    public Result calculate(String formula) throws FormulaException {
        logger.info("Calculating " + formula);
        // replace variables with values
        formula = replaceVariablesWithMeasurableValues(formula);
        // evaluate formula
        Expression expression = new Expression(formula);
        try {
            EvaluationValue value = expression.evaluate();
            logger.info("Evaluation value: " + value.getNumberValue());
            // return result
            return new Result(value.getNumberValue());
        } catch (EvaluationException | ParseException e) {
            logger.info("Failed to calculate: " + e.getMessage());
            throw new FormulaException(e.getMessage());
        }
    }

    /**
     * Validates braces of formula:
     * variables are composed of two opening braces {{,
     * the label of the measurable
     * and two closing braces }}
     *
     * @param formula
     * @return
     */
    public boolean validateBraces(String formula) {
        logger.info("Validating brackets of formula: " + formula);
        // init counter
        int openBraces = 0;
        // init variable last character
        char lastChar = Character.MIN_VALUE;
        // iterate over all characters of the formula
        for (char c : formula.toCharArray()) {
            // if not first round
            if (lastChar != Character.MIN_VALUE) {
                // first opening brace not followed by second opening brace
                if (lastChar == '{' && openBraces < 2 && c != '{') {
                    logger.info("Error: Opening brace '{' not followed by second opening bracket '{'");
                    return false;
                }
                // first closing brace not followed by second closing brace
                if (lastChar == '}' && openBraces > 0 && c != '}') {
                    logger.info("Error: Closing brace '}' not followed by second closing bracket '}'");
                    return false;
                }
            }
            // opening brace
            if (c == '{') {
                // more than 2 braces opened
                if (openBraces > 2) {
                    logger.info("Error: More than two opening brace '{");
                    return false;
                } else {
                    // increment
                    openBraces++;
                }
            }
            // closing brace
            if (c == '}') {
                // no opened brace
                if (openBraces < 1) {
                    logger.info("Error: Closing bracket '}' but no brace are opened'");
                    return false;
                } else {
                    // decrement
                    openBraces--;
                }
            }
            // set last character
            lastChar = c;
        }
        // iteration finished
        // check if all braces are closed
        if (openBraces != 0) {
            logger.info("Not all brace closed");
            return false;
        }
        return true;
    }

    /**
     * Replaces variables with the latest result of measurable
     *
     * @param formula
     * @return
     * @throws FormulaException
     */
    public String replaceVariablesWithMeasurableValues(String formula) throws FormulaException {
        logger.info("Replacing variables of formula with measurable values");
        logger.info("Formula: " + formula);
        // check if formula contains a variable and braces are set correctly
        if (validateBraces(formula) && containsVariable(formula)) {
            // get label
            String variable = getFirstVariable(formula);
            // find measurables by label
            List<Measurable> measurables = service.findAllMeasurablesByLabel(variable);
            // check if measurable exists
            if (measurables.isEmpty()) {
                throw new FormulaException("No measurables found with label: " + variable);
            }
            // get first measurable, in theory only list should contain only one measurable because a label is unique
            Measurable measurable = measurables.get(0);
            // get the latest result of measurable
            Result result = service.findLatestResultByMeasurable(measurable);
            // check if result exists
            if (result == null) {
                throw new FormulaException("No results of " + measurable.getName() + " available for calculation");
            }

            // replace variable with result value in formula
            int start = getStartIndexOfFirstVariable(formula);
            String substring = formula.substring(start + 2); // get substring after {{
            int end = getEndIndexOfFirstVariable(substring);
            String newFormula = formula.substring(0, start) + result.getValue() + substring.substring(end + 2);
            // recursive method call with new formula
            return replaceVariablesWithMeasurableValues(newFormula);
        }
        // return formula with replaced variables by result values
        return formula;
    }

    /**
     * Returns first variable in formula
     *
     * @param formula
     * @return
     * @throws FormulaException
     */
    private String getFirstVariable(String formula) throws FormulaException {
        logger.info("Get variable");
        // check if a variable contains in formula
        if (containsVariable(formula)) {
            // get start and end index
            int start = getStartIndexOfFirstVariable(formula);
            int end = getEndIndexOfFirstVariable(formula);
            // get variable substring
            String variable = formula.substring(start + 2, end); // get variable name
            logger.info("Variable: " + variable);
            // return variable
            return variable;
        } else {
            return null;
        }
    }

    /**
     * Returns start index of first variable in formula
     *
     * @param formula
     * @return
     */
    private int getStartIndexOfFirstVariable(String formula) {
        logger.info("Get index of start of variable");
        int start = formula.indexOf("{{"); // get start index of variable
        logger.info("Index of start: " + start);
        return start;
    }

    /**
     * Returns end index of first variable in formula
     *
     * @param formula
     * @return
     */
    private int getEndIndexOfFirstVariable(String formula) {
        logger.info("Get index of end of variable");
        int end = formula.indexOf("}}"); // get end index of variable
        logger.info("Index of end: " + end);
        return end;
    }

    /**
     * Check if formula contains a variable
     *
     * @param formula
     * @return
     * @throws FormulaException
     */
    private boolean containsVariable(String formula) throws FormulaException {
        logger.info("Checking if formula contains variable");
        int start = getStartIndexOfFirstVariable(formula);
        if (start == -1)  { // variable start definition does not exist
            logger.info("No variable start definition");
            return false;
        } else { // variable start definition exists
            int end = getEndIndexOfFirstVariable(formula);
            if (end == -1) { // variable end definition does not exist
                logger.info("No variable end definition");
                return false;
            } else { // variable end definition exists
                String variable = formula.substring(start + 2, end); // get variable name
                String pattern = "[a-z]+((\\d)|([A-Z0-9][a-z0-9]+))*([A-Z])?"; // regex expression for lower camel case
                if (variable.matches(pattern)) { // variable name is lower camel case
                    logger.info("Variable found");
                    return true;
                } else { // variable name not is lower camel case
                    logger.info("Variable does not match lower camel case");
                    throw new FormulaException("Variable does not match lower camel case");
                }
            }
        }
    }

    /**
     * Returns all measurables referenced by formula
     *
     * @param formula
     * @return
     * @throws FormulaException
     */
    public Set<Measurable> getMeasurables(String formula) throws FormulaException {
        Set<Measurable> measurables = new HashSet<>();
        logger.info("Getting referenced measurables of formula");
        logger.info("Formula: " + formula);
        // while formula a variable and braces are set correctly
        while (validateBraces(formula) && containsVariable(formula)) {
            // get first variable
            String variable = getFirstVariable(formula);
            // find measurable by label / variable
            List<Measurable> m = service.findAllMeasurablesByLabel(variable);
            // check if measurable exists
            if (m.isEmpty()) {
                throw new FormulaException("No measurables found with label: " + variable);
            }
            // get first measurable, in theory only list should contain only one measurable because a label is unique
            Measurable measurable = m.get(0);
            // add measurable to set
            measurables.add(measurable);
            // shorten formula: new start index is after current variable
            int start = getStartIndexOfFirstVariable(formula);
            String substring = formula.substring(start + 2); // get substring after {{
            int end = getEndIndexOfFirstVariable(substring);
            formula = formula.substring(0, start) + 1 + substring.substring(end + 2);
        }
        // return all measurables
        return measurables;
    }
}
