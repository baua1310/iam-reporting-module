package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.Audience;
import de.uniregensburg.iamreportingmodule.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Repository for audiences
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface AudienceRepository extends JpaRepository<Audience, UUID> {

    /**
     * Returns all audiences by members
     * @param members
     * @return
     */
    List<Audience> findAllByMembersIn(Set<User> members);
}
