package de.uniregensburg.iamreportingmodule.data.entity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Abstract entity group
 * Attributes: name (String), members (Set<User>)
 *
 * @author Julian Bauer
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Group extends AbstractEntity {

    @NotBlank
    private String name;

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
     * Returns members
     *
     * @return
     */
    @Transient
    public abstract Set<User> getMembers();

    /**
     * Sets members
     *
     * @param members
     */
    @Transient
    public abstract void setMembers(Set<User> members);
}