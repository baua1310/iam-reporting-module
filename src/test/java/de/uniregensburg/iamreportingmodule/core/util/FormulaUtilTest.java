package de.uniregensburg.iamreportingmodule.core.util;

import com.ezylang.evalex.BaseException;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import de.uniregensburg.iamreportingmodule.core.exception.FormulaException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.data.entity.Metric;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests formula util
 *
 * @author Julian Bauer
 */
@ActiveProfiles(profiles = "local")
@RunWith(SpringRunner.class)
@SpringBootTest
public class FormulaUtilTest {

    @Autowired
    private MeasurableService service;
    private FormulaUtil util;

    /**
     * Initializes formula util
     */
    @Before
    public void setupData() {
        this.util = new FormulaUtil(service);
    }

    /**
     * Tests metric calculation
     *
     * @throws FormulaException
     */
    @Test
    public void testMetricCalculation() throws FormulaException {
        Metric metric = null;
        for (Metric m : service.findAllMetrics()) {
            if (m.getLabel().equals("identicalPasswordsPercentage")) {
                metric = m;
                break;
            }
        }

        assert metric != null;
        Result result = util.calculate(metric.getFormula().getFormula());

        BigDecimal expected = BigDecimal.valueOf(0.023);
        BigDecimal actual = result.getValue();

        Assert.assertEquals(expected, actual);
    }

    /**
     * Tests basic math formula
     *
     * @throws EvaluationException
     * @throws ParseException
     */
    @Test
    public void testBasicMathFormula() throws EvaluationException, ParseException {
        Expression expression = new Expression("1 + 2 / (4 * SQRT(4))");
        EvaluationValue result = expression.evaluate();

        Assert.assertEquals(new BigDecimal("1.25"), result.getNumberValue());
    }

    /**
     * Tests basic math formula with variables I
     *
     * @throws EvaluationException
     * @throws ParseException
     */
    @Test
    public void testBasicMathFormulaUsingVariables() throws EvaluationException, ParseException {
        Expression expression = new Expression("(a + b) * (a - b)");
        EvaluationValue result = expression
                .with("a", 3.5)
                .with("b", 2.5)
                .evaluate();

        Assert.assertEquals(new BigDecimal("6"), result.getNumberValue());
    }

    /**
     * Tests basic math formula with variables II
     *
     * @throws EvaluationException
     * @throws ParseException
     */
    @Test
    public void testBasicMathFormulaUsingVariables2() throws EvaluationException, ParseException {
        Expression expression = new Expression("(a + b) * (a - b)");

        Map<String, Object> values = new HashMap<>();
        values.put("a", 3.5);
        values.put("b", 2.5);

        EvaluationValue result = expression.withValues(values).evaluate();

        Assert.assertEquals(new BigDecimal("6"), result.getNumberValue());
    }

    /**
     * Tests basic math formula with variables III
     *
     * @throws EvaluationException
     * @throws ParseException
     */
    @Test
    public void testBasicMathFormulaUsingCustomVariables3() throws EvaluationException, ParseException, FormulaException {
        String formulaWithVariables = "({{identicalPasswords}} + 3)";
        String formula = util.replaceVariablesWithMeasurableValues(formulaWithVariables);

        Expression expression = new Expression(formula);

        EvaluationValue result = expression.evaluate();

        Assert.assertEquals(new BigDecimal("26"), result.getNumberValue());
    }

    /**
     * Tests uppercase variable
     *
     * @throws EvaluationException
     * @throws ParseException
     */
    @Test
    public void testUppercaseVariable() {
        String formula = "({{UPPERCASE}} + 1) * (2 - 3)";

        Exception exception = Assertions.assertThrows(FormulaException.class, () -> util.replaceVariablesWithMeasurableValues(formula));

        String expectedMessage = "Variable does not match lower camel case";
        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Tests division by zero
     */
    @Test
    public void testDivisionByZero() {
        String formula = "1 / 0";

        Expression expression = new Expression(formula);

        Exception exception = Assertions.assertThrows(BaseException.class, expression::evaluate);

        String expectedMessage = "Division by zero";
        String actualMessage = exception.getMessage();

        Assert.assertEquals(expectedMessage, actualMessage);
    }

    /**
     * Tests brace validation I
     */
    @Test
    public void testBraceValidation1() {
        String formula = "({{identicalPasswords}} + 1) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertTrue(condition);
    }

    /**
     * Tests brace validation II
     */
    @Test
    public void testBraceValidation2() {
        String formula = "({{identicalPasswords}} + }}weakPasswords{{) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertFalse(condition);
    }

    /**
     * Tests brace validation III
     */
    @Test
    public void testBraceValidation3() {
        String formula = "({identicalPasswords}} + 1) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertFalse(condition);
    }

    /**
     * Tests brace validation IV
     */
    @Test
    public void testBraceValidation4() {
        String formula = "({{identicalPasswords} + 1) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertFalse(condition);
    }

    /**
     * Tests brace validation V
     */
    @Test
    public void testBraceValidation5() {
        String formula = "({{identicalPasswords}} + {{weakPasswords) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertFalse(condition);
    }

    /**
     * Tests brace validation VI
     */
    @Test
    public void testBraceValidation6() {
        String formula = "({{identicalPasswords}} + weakPasswords}}) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertFalse(condition);
    }

    /**
     * Tests brace validation VII
     */
    @Test
    public void testBraceValidation7() {
        String formula = "({{identicalPasswords}} + {{weakPasswords}) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertFalse(condition);
    }

    /**
     * Tests brace validation VIII
     */
    @Test
    public void testBraceValidation8() {
        String formula = "({{identicalPasswords}} + {{weakPasswords}}) * (2 - 3)";
        boolean condition = util.validateBraces(formula);
        Assert.assertTrue(condition);
    }

}
