package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.DatabaseDataSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for database datasources
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface DatabaseDataSourceRepository extends JpaRepository<DatabaseDataSource, UUID> {}
