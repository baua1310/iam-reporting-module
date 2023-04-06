package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.ManualDataSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for manual datasources
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface ManualDataSourceRepository extends JpaRepository<ManualDataSource, UUID> {}
