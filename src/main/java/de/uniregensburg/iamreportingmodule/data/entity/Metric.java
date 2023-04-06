package de.uniregensburg.iamreportingmodule.data.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entity metric extends measurable
 * Attributes: formula (Formula), targetValue (BigDecimal)
 *
 * @author Julian Bauer
 */
@Entity
public class Metric extends Measurable {

    @OneToOne
    @JoinColumn(name = "formula_id")
    @NotNull
    @Cascade(CascadeType.ALL)
    private Formula formula = new Formula(this);

    @NotNull
    // https://coderanch.com/t/638935/databases/Hibernate-setting-decimal-points-BigDecimal
    @Column(precision = 20, scale = 10, columnDefinition="DECIMAL(20,10)")
    private BigDecimal targetValue = BigDecimal.ZERO;

    /**
     * Returns formula
     *
     * @return
     */
    public Formula getFormula() {
        return formula;
    }

    /**
     * Sets formula
     *
     * @param formula
     */
    public void setFormula(Formula formula) {
        formula.setMetric(this);
        this.formula = formula;
    }

    /**
     * Returns target value
     *
     * @return
     */
    public BigDecimal getTargetValue() {
        return targetValue;
    }

    /**
     * Sets target value
     *
     * @param targetValue
     */
    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }
}
