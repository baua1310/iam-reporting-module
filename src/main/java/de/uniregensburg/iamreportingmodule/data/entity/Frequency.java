package de.uniregensburg.iamreportingmodule.data.entity;

import org.hibernate.validator.constraints.time.DurationMin;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.Duration;

/**
 * Entity frequency
 * Attributes: duration (Duration), measurable (Measurable)
 *
 * @author Julian Bauer
 */
@Entity
public class Frequency extends AbstractEntity {
    @DurationMin(seconds = 1L)
    private Duration duration;

    @NotNull
    @OneToOne(mappedBy = "frequency")
    private Measurable measurable;

    /**
     *
     */
    public Frequency() {}

    /**
     *
     * @param measurable
     * @param duration
     */
    public Frequency(Measurable measurable, Duration duration) {
        this.measurable = measurable;
        this.duration = duration;
    }

    /**
     *
     * @param measurable
     */
    public Frequency(Measurable measurable) {
        this.measurable = measurable;
    }

    /**
     *
     * @param duration
     */
    public Frequency(Duration duration) {
        this.duration = duration;
    }

    /**
     * Returns duration
     *
     * @return
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Sets duration
     *
     * @param duration
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
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
}
