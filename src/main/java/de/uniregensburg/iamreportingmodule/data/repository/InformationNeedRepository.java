package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.InformationNeed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for information needs
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface InformationNeedRepository extends JpaRepository<InformationNeed, UUID> {}
