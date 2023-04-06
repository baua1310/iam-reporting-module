package de.uniregensburg.iamreportingmodule.data.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity result
 * Attributes: value (BigDecimal), pointInTime (Date), measurable (Measurable)
 *
 * @author Julian Bauer
 */
@Entity
public class Result extends AbstractEntity {

    @NotNull
    // name 'value' is reserved, see https://docs.oracle.com/cd/B19306_01/appdev.102/b14261/reservewords.htm
    // https://coderanch.com/t/638935/databases/Hibernate-setting-decimal-points-BigDecimal
    @Column(name = "value_", precision = 20, scale = 10, columnDefinition="DECIMAL(20,10)")
    private BigDecimal value;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date pointInTime;

    @ManyToOne
    @JoinColumn(name = "measurable_id")
    @NotNull
    private Measurable measurable;

    /**
     *
     */
    public Result() {
        this.pointInTime = new Date(System.currentTimeMillis());
    }

    /**
     *
     * @param value
     */
    public Result(BigDecimal value) {
        this();
        this.value = value;
    }

    /**
     * Returns measurable
     *
     * @return
     */
    public Measurable getMeasurable() {
        return measurable;
    }

    /**
     * Sets measurable
     *
     * @param measurable
     */
    public void setMeasurable(Measurable measurable) {
        this.measurable = measurable;
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

    /**
     * Returns point in time
     *
     * @return
     */
    public Date getPointInTime() {
        return pointInTime;
    }

    /**
     * Sets point in time
     *
     * @param pointInTime
     */
    public void setPointInTime(Date pointInTime) {
        this.pointInTime = pointInTime;
    }
}
