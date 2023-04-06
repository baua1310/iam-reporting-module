package de.uniregensburg.iamreportingmodule.data.entity;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity audience extends entity group
 * Attributes: members (Set<User>), measurables (Set<Measurable>)
 *
 * @author Julian Bauer
 */
@Entity
public class Audience extends Group {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "audience_memberships",
            joinColumns = @JoinColumn(name = "audience_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @ManyToMany(mappedBy = "audiences")
    @Nullable
    private Set<Measurable> measurables = new HashSet<>();

    /**
     * Returns members
     *
     * @return
     */
    public Set<User> getMembers() {
        return members;
    }

    /**
     * Sets members
     *
     * @param members
     */
    public void setMembers(Set<User> members) {
        this.members = members;
    }

    /**
     * Returns measurables
     *
     * @return
     */
    @Nullable
    public Set<Measurable> getMeasurables() {
        return measurables;
    }

    /**
     * Sets measurables
     *
     * @param measurables
     */
    public void setMeasurables(@Nullable Set<Measurable> measurables) {
        this.measurables = measurables;
    }
}
