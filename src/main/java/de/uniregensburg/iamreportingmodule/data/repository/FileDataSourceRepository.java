package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.FileDataSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for file datasources
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface FileDataSourceRepository extends JpaRepository<FileDataSource, UUID> {}
