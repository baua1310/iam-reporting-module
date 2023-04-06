package de.uniregensburg.iamreportingmodule.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity measurement extends measurable
 * Attributes: dataSource (DataSource), attributes (Map<String, String>)
 *
 * @author Julian Bauer
 */
@Entity
public class Measurement extends Measurable {

    @ManyToOne
    @JoinColumn(name = "datasource_id")
    @NotNull
    private DataSource dataSource;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="key_") // key reserved -> key_
    @Column(name="value_") // value reserved -> value_
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Returns data source
     * @return
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets data source
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns attributes
     *
     * @return
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Sets attributes
     *
     * @param attributes
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
