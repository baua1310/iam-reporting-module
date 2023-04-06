package de.uniregensburg.iamreportingmodule.data.entity;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity user
 * Attributes: username (String), firstName (String), lastName (String), password (String), isAdmin (boolean),
 * audienceMemberships (Set<Audience>), stakeholderMemberships (Set<Stakeholder>)
 *
 * @author Julian Bauer
 */
@Entity
@Table(name="user_account") // name 'users' is reserved, see https://docs.oracle.com/cd/B19306_01/appdev.102/b14261/reservewords.htm
public class User extends AbstractEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String password;
    @NotNull
    private boolean admin = false;

    @ManyToMany(mappedBy = "members")
    @Nullable
    private Set<Audience> audienceMemberships = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @Nullable
    private Set<Stakeholder> stakeholderMemberships = new HashSet<>();

    /**
     * Returns username
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns first name
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns last name
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns full name consisting of first and last name
     *
     * @return
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns password
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns if user is admin
     *
     * @return
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Sets admin
     *
     * @param admin
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Returns audience memberships
     *
     * @return
     */
    @Nullable
    public Set<Audience> getAudienceMemberships() {
        return audienceMemberships;
    }

    /**
     * Sets audience memberships
     *
     * @param audienceMemberships
     */
    public void setAudienceMemberships(@Nullable Set<Audience> audienceMemberships) {
        this.audienceMemberships = audienceMemberships;
    }

    /**
     * Returns stakeholder memberships
     *
     * @return
     */
    @Nullable
    public Set<Stakeholder> getStakeholderMemberships() {
        return stakeholderMemberships;
    }

    /**
     * Sets stakeholder memberships
     *
     * @param stakeholderMemberships
     */
    public void setStakeholderMemberships(@Nullable Set<Stakeholder> stakeholderMemberships) {
        this.stakeholderMemberships = stakeholderMemberships;
    }
}
