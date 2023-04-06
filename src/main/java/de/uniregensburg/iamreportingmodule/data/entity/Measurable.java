package de.uniregensburg.iamreportingmodule.data.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract entity measurable
 * Attributes: name (String), description (String), results (List<Result>), audiences (Set<Audience>),
 * stakeholders (Set<Stakeholders>), informationNeeds (Set<InformationNeeds>), scale (Scale), unit (Unit),
 * frequency (Frequency), formulas (Set<Formula>), label (String),
 *
 * @author Julian Bauer
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Measurable extends AbstractEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    private String description;

    @OneToMany(mappedBy = "measurable")
    @Nullable
    private List<Result> results = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "measurables_audiences",
            joinColumns = @JoinColumn(name = "measurable_id"),
            inverseJoinColumns = @JoinColumn(name = "audience_id")
    )
    private Set<Audience> audiences = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "measurables_stakeholders",
            joinColumns = @JoinColumn(name = "measurable_id"),
            inverseJoinColumns = @JoinColumn(name = "stakeholder_id")
    )
    private Set<Stakeholder> stakeholders = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "measurables_informationneeds",
            joinColumns = @JoinColumn(name = "measurable_id"),
            inverseJoinColumns = @JoinColumn(name = "informationneed_id")
    )
    private Set<InformationNeed> informationNeeds = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Scale scale;

    @Enumerated(EnumType.STRING)
    private Unit unit;

    @OneToOne
    @JoinColumn(name = "frequency_id")
    @Cascade(CascadeType.ALL)
    private Frequency frequency = new Frequency(this);

    @ManyToMany(mappedBy = "measurables", fetch = FetchType.EAGER)
    @Nullable
    private Set<Formula> formulas = new HashSet<>();

    @NotBlank
    @Column(unique = true)
    @Pattern(regexp = "[a-z]+((\\d)|([A-Z0-9][a-z0-9]+))*([A-Z])?", message = "label must be lower camel case")
    private String label;

    /**
     * Returns name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns description
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns results
     *
     * @return
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * Sets results
     *
     * @param results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

    /**
     * Returns audiences
     *
     * @return
     */
    public Set<Audience> getAudiences() {
        return audiences;
    }

    /**
     * Sets audiences
     *
     * @param audiences
     */
    public void setAudiences(Set<Audience> audiences) {
        this.audiences = audiences;
    }

    /**
     * Returns stakeholders
     *
     * @return
     */
    public Set<Stakeholder> getStakeholders() {
        return stakeholders;
    }

    /**
     * Sets stakeholders
     *
     * @param stakeholders
     */
    public void setStakeholders(Set<Stakeholder> stakeholders) {
        this.stakeholders = stakeholders;
    }

    /**
     * Returns information needs
     *
     * @return
     */
    public Set<InformationNeed> getInformationNeeds() {
        return informationNeeds;
    }

    /**
     * Sets information needs
     *
     * @param informationNeeds
     */
    public void setInformationNeeds(Set<InformationNeed> informationNeeds) {
        this.informationNeeds = informationNeeds;
    }

    /**
     * Returns scale
     *
     * @return
     */
    public Scale getScale() {
        return scale;
    }

    /**
     * Sets scale
     *
     * @param scale
     */
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    /**
     * Returns unit
     *
     * @return
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets unit
     *
     * @param unit
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Returns frequency
     *
     * @return
     */
    public Frequency getFrequency() {
        return frequency;
    }

    /**
     * Sets frequency
     *
     * @param frequency
     */
    public void setFrequency(Frequency frequency) {
        frequency.setMeasurable(this);
        this.frequency = frequency;
    }

    /**
     * Returns formulas
     *
     * @return
     */
    public Set<Formula> getFormulas() {
        return formulas;
    }

    /**
     * Sets formulas
     *
     * @param formulas
     */
    public void setFormulas(Set<Formula> formulas) {
        this.formulas = formulas;
    }

    /**
     * Returns label
     *
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Checks before deleting measurable if it part of any formulas
     *
     * @throws RuntimeException
     */
    @PreRemove
    public void checkFormulaAssociationBeforeRemoval() {
        if (!this.formulas.isEmpty()) {
            throw new RuntimeException("Cannot remove a measurable that is part of formulas.");
        }
    }
}
