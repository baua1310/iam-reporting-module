package de.uniregensburg.iamreportingmodule.data.entity;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity group
 * Attributes: name (String), measurables (Set<Measurable>)
 *
 * @author Julian Bauer
 */
@Entity
public class InformationNeed extends AbstractEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @ManyToMany(mappedBy = "informationNeeds")
    @Nullable
    private Set<Measurable> measurables = new HashSet<>();

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
     * Returns name
     * Overrides toString method
     *
     * @return
     */
    @Override
    public String toString() {
        return getName();
    }
}
