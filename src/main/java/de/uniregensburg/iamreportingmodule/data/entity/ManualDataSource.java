package de.uniregensburg.iamreportingmodule.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entity file data source extends data source
 * Attributes: value (BigDecimal)
 *
 * @author Julian Bauer
 */
@Entity
public class ManualDataSource extends DataSource {

    @NotNull
    // name 'value' is reserved, see https://docs.oracle.com/cd/B19306_01/appdev.102/b14261/reservewords.htm
    // https://coderanch.com/t/638935/databases/Hibernate-setting-decimal-points-BigDecimal
    @Column(name = "value_", precision = 20, scale = 10, columnDefinition="DECIMAL(20,10)")
    private BigDecimal value = BigDecimal.ZERO;

    /**
     *
     */
    public ManualDataSource() {
        setType(DataSourceType.MANUAL);
    }

    /**
     * Returns value
     *
     * @return
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets value
     *
     * @param value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
