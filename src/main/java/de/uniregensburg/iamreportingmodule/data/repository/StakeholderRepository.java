package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.Stakeholder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for stakeholders
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface StakeholderRepository extends JpaRepository<Stakeholder, UUID> {}
