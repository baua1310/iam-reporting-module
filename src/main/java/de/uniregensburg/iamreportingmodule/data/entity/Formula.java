package de.uniregensburg.iamreportingmodule.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity formula
 * Attributes: measurables (Set<Measurable>), formula (String), metric (Metric)
 *
 * @author Julian Bauer
 */
@Entity
public class Formula extends AbstractEntity {

    @ManyToMany
    @JoinTable(
            name = "formulas_measurables",
            joinColumns = @JoinColumn(name = "formula_id"),
            inverseJoinColumns = @JoinColumn(name = "measureable_id")
    )
    private Set<Measurable> measurables = new HashSet<>();

    @NotBlank
    private String formula;

    @NotNull
    @OneToOne(mappedBy = "formula")
    private Metric metric;

    /**
     *
     */
    public Formula() {}

    /**
     *
     * @param metric
     */
    public Formula(Metric metric) {
        this.metric = metric;
    }

    /**
     *
     * @param formula
     */
    public Formula(String formula) {
        this.formula = formula;
    }

    /**
     * Returns measurables
     *
     * @return
     */
    public Set<Measurable> getMeasurables() {
        return measurables;
    }

    /**
     * Sets measurables
     *
     * @param measurables
     */
    public void setMeasurables(Set<Measurable> measurables) {
        this.measurables = measurables;
    }

    /**
     * Returns formula
     *
     * @return
     */
    public String getFormula() {
        return formula;
    }

    /**
     * Sets formula
     *
     * @param formula
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * Returns metric
     *
     * @return
     */
    public Metric getMetric() {
        return metric;
    }

    /**
     * Sets metric
     *
     * @param metric
     */
    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}
