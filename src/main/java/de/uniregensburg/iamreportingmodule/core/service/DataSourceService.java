package de.uniregensburg.iamreportingmodule.core.service;

import de.uniregensburg.iamreportingmodule.core.exception.DeleteEntityException;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import de.uniregensburg.iamreportingmodule.data.repository.DatabaseDataSourceRepository;
import de.uniregensburg.iamreportingmodule.data.repository.FileDataSourceRepository;
import de.uniregensburg.iamreportingmodule.data.repository.ManualDataSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for persisting datasources
 *
 * @author Julian Bauer
 */
@Service
public class DataSourceService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ManualDataSourceRepository manualDataSourceRepository;
    private final DatabaseDataSourceRepository databaseDataSourceRepository;
    private final FileDataSourceRepository fileDataSourceRepository;

    /**
     *
     * @param manualDataSourceRepository
     * @param databaseDataSourceRepository
     * @param fileDataSourceRepository
     */
    public DataSourceService(ManualDataSourceRepository manualDataSourceRepository, DatabaseDataSourceRepository databaseDataSourceRepository, FileDataSourceRepository fileDataSourceRepository) {
        this.manualDataSourceRepository = manualDataSourceRepository;
        this.databaseDataSourceRepository = databaseDataSourceRepository;
        this.fileDataSourceRepository = fileDataSourceRepository;
    }

    /**
     * Returns all datasources
     *
     * @return
     */
    public List<DataSource> findAllDataSources() {
        logger.info("Returning all datasources");
        // get datasources of all three types
        List<DataSource> dataSources = new ArrayList<>();
        dataSources.addAll(manualDataSourceRepository.findAll());
        dataSources.addAll(databaseDataSourceRepository.findAll());
        dataSources.addAll(fileDataSourceRepository.findAll());
        // return all datasources sorted by name
        return dataSources.stream().sorted(Comparator.comparing(DataSource::getName)).collect(Collectors.toList());
    }

    /**
     * Saves a datasource
     *
     * @param dataSource
     * @throws SaveEntityException
     */
    public void saveDataSource(DataSource dataSource) throws SaveEntityException {
        logger.info("Saving datasource");
        // check datasource
        if (dataSource == null) {
            logger.info("Datasource is null");
            throw new SaveEntityException("No datasource provided");
        }
        // save datasource depending on type
        DataSourceType type = dataSource.getType();
        try {
            if (type.equals(DataSourceType.MANUAL)) {
                ManualDataSource manualDataSource = (ManualDataSource) dataSource;
                manualDataSourceRepository.save(manualDataSource);
            } else if (type.equals(DataSourceType.DATABASE)) {
                DatabaseDataSource databaseDataSource = (DatabaseDataSource) dataSource;
                databaseDataSourceRepository.save(databaseDataSource);
            } else if (type.equals(DataSourceType.FILE)) {
                FileDataSource fileDataSource = (FileDataSource) dataSource;
                fileDataSourceRepository.save(fileDataSource);
            } else {
                logger.info("Saving datasource type " + type + " not implemented yet");
                throw new SaveEntityException("Saving datasource type " + type + " not implemented yet");
            }
            logger.info("Datasource saved");
        } catch (Exception e) {
            logger.info("Error while saving datasource: " + e.getMessage());
            throw new SaveEntityException("Error while saving datasource");
        }
    }

    /**
     * Deletes a datasource
     *
     * @param dataSource
     * @throws DeleteEntityException
     */
    public void deleteDataSource(DataSource dataSource) throws DeleteEntityException {
        logger.info("Deleting datasource");
        // check datasource
        if (dataSource == null) {
            logger.info("Datasource is null");
            return;
        }
        // delete datasource depending on type
        DataSourceType type = dataSource.getType();
        try {
            if (type.equals(DataSourceType.MANUAL)) {
                ManualDataSource manualDataSource = (ManualDataSource) dataSource;
                manualDataSourceRepository.delete(manualDataSource);
            } else if (type.equals(DataSourceType.DATABASE)) {
                DatabaseDataSource databaseDataSource = (DatabaseDataSource) dataSource;
                databaseDataSourceRepository.delete(databaseDataSource);
            } else if (type.equals(DataSourceType.FILE)) {
                FileDataSource fileDataSource = (FileDataSource) dataSource;
                fileDataSourceRepository.delete(fileDataSource);
            } else {
                logger.info("Deleting datasource type " + type + " not implemented yet");
                throw new SaveEntityException("Deleting datasource type " + type + " not implemented yet");
            }
            logger.info("Datasource deleted");
        } catch (Exception ex) {
            logger.info("Cannot delete datasource: " + ex.getMessage());
            throw new DeleteEntityException("Datasource is probably used in one or more measurements");
        }
    }

    /**
     * Returns manual datasource by id
     *
     * @param id
     * @return
     */
    public ManualDataSource findManualDataSourceById(UUID id) {
        logger.info("Searching manual datasource with id " + id);
        // get datasource
        Optional<ManualDataSource> dataSource = manualDataSourceRepository.findById(id);

        // check if datasource exists
        if (dataSource.isEmpty()) {
            logger.info("Manual datasource not found");
            return null;
        }

        logger.info("Manual datasource found");
        // return datasource
        return dataSource.get();
    }

    /**
     * Returns database datasource by id
     *
     * @param id
     * @return
     */
    public DatabaseDataSource findDatabaseDataSourceById(UUID id) {
        logger.info("Searching database datasource with id " + id);
        // get datasource
        Optional<DatabaseDataSource> dataSource = databaseDataSourceRepository.findById(id);

        // check if datasource exists
        if (dataSource.isEmpty()) {
            logger.info("Database datasource not found");
            return null;
        }

        logger.info("Database datasource found");
        // return datasource
        return dataSource.get();
    }

    /**
     * Returns file datasource by id
     *
     * @param id
     * @return
     */
    public FileDataSource findFileDataSourceById(UUID id) {
        logger.info("Searching file datasource with id " + id);
        // get datasource
        Optional<FileDataSource> dataSource = fileDataSourceRepository.findById(id);

        // check if datasource exists
        if (dataSource.isEmpty()) {
            logger.info("File datasource not found");
            return null;
        }

        logger.info("File datasource found");
        // return datasource
        return dataSource.get();
    }
}
