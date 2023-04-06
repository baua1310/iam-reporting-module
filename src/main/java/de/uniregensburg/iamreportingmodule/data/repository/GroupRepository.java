package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for groups
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface GroupRepository extends JpaRepository<Group, UUID> {
}
