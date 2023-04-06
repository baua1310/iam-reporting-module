package de.uniregensburg.iamreportingmodule.data.entity;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract entity data source
 * Attributes: name (String), description (int), type (DataSourceType), measurements (List<Measurement>)
 *
 * @author Julian Bauer
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DataSource extends AbstractEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DataSourceType type;

    @OneToMany(mappedBy = "dataSource", fetch = FetchType.EAGER)
    @Nullable
    private List<Measurement> measurements = new ArrayList<>();

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
     * Returns type
     *
     * @return
     */
    public DataSourceType getType() {
        return type;
    }

    /**
     * Sets type
     *
     * @param type
     */
    public void setType(DataSourceType type) {
        this.type = type;
    }

    /**
     * Returns measurements
     *
     * @return
     */
    public List<Measurement> getMeasurements() {
        return measurements;
    }

    /**
     * Sets measurements
     *
     * @param measurements
     */
    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    /**
     * Checks before deleting datasource if it is in use by any measurements
     *
     * @throws RuntimeException
     */
    @PreRemove
    public void checkMeasurementAssociationBeforeRemoval() {
        if (!this.measurements.isEmpty()) {
            throw new RuntimeException("Cannot remove a datasource that is used by measurements.");
        }
    }
}
