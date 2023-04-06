package de.uniregensburg.iamreportingmodule.data.entity;

import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Abstract entity containing attribute id (UUID).
 * All other entities inherit this entity.
 *
 * @author Julian Bauer
 */
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    private UUID id;

    /**
     * Returns id
     *
     * @return
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets id
     *
     * @param id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns hash code
     *
     * @return
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    /**
     * Returns if two objects are equal
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity)) {
            return false; // null or other class
        }
        AbstractEntity other = (AbstractEntity) obj;

        if (id != null) {
            return id.equals(other.id);
        }
        return super.equals(other);
    }
}
