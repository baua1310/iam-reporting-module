package de.uniregensburg.iamreportingmodule.core.scheduling;

import de.uniregensburg.iamreportingmodule.core.exception.DatabaseException;
import de.uniregensburg.iamreportingmodule.core.exception.FileException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.util.CsvUtil;
import de.uniregensburg.iamreportingmodule.core.util.DatabaseUtil;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Quartz job definition for determining measurements
 *
 * @author Julian Bauer
 */
@Component
public class MeasureJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeasurableService service;

    /**
     *
     * @param service
     */
    public MeasureJob(MeasurableService service) {
        this.service = service;
    }

    /**
     * Overrides job execution definition
     *
     * @param context   add id of measurement (String measurementId) to JobDataMap
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Starting job execution");
        // get id of measurement
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String measurementId = jobDataMap.getString("measurementId");
        if (measurementId.isBlank()) {
            logger.info("No measurement id provided");
            throw new JobExecutionException("No measurement id provided");
        }
        // get measurement
        Measurement measurement = service.findMeasurementById(UUID.fromString(measurementId));
        if (measurement == null) {
            logger.info("Measurement is null");
            throw new JobExecutionException("Measurement is null");
        }
        logger.info("Measurement: " + measurement.getName());

        // get datasource
        logger.info("Getting datasource");
        DataSource dataSource = measurement.getDataSource();
        if (dataSource == null) {
            logger.info("Datasource is null");
            throw new JobExecutionException("Datasource is null");
        }
        logger.info("Datasource: " + dataSource.getName());

        // get datasource type
        logger.info("Getting datasource type");
        DataSourceType type = dataSource.getType();
        if (type == null) {
            logger.info("Datasource type is null");
            throw new JobExecutionException("Datasource type is null");
        }

        // measure depending on datasource type
        Result result;
        if (DataSourceType.MANUAL.equals(type)) {
            logger.info("Manual datasource");
            ManualDataSource manualDataSource = (ManualDataSource) dataSource;
            result = measureManualDataSource(manualDataSource);
        } else if (DataSourceType.DATABASE.equals(type)) {
            logger.info("Database datasource");
            DatabaseDataSource databaseDataSource = (DatabaseDataSource) dataSource;
            result = measureDatabaseDataSource(databaseDataSource, measurement);
        } else if (DataSourceType.FILE.equals(type)) {
            logger.info("File datasource");
            FileDataSource fileDataSource = (FileDataSource) dataSource;
            FileType fileType = fileDataSource.getFileType();
            if (FileType.CSV.equals(fileType)) {
                result = measureCsvFileDataSource(fileDataSource, measurement);
            } else {
                throw new JobExecutionException("Unknown file type: " + fileType);
            }
        } else {
            throw new JobExecutionException("Unknown datasource type: " + type);
        }

        // save result
        result.setMeasurable(measurement);
        service.saveResult(result);
    }

    /**
     * Determines measurement in database using sql query
     *
     * @param databaseDataSource
     * @param measurement
     * @return
     * @throws JobExecutionException
     */
    private Result measureDatabaseDataSource(DatabaseDataSource databaseDataSource, Measurement measurement) throws JobExecutionException {
        // get sql query
        logger.info("Checking query");
        Map<String, String> attributes = measurement.getAttributes();
        String query = attributes.get("sqlQuery");
        if (query == null) {
            logger.info("Query is null");
            throw new JobExecutionException("Query is null");
        }
        if (query.isBlank()) {
            logger.info("Query is blank");
            throw new JobExecutionException("Query is blank");
        }
        logger.info("Query: " + query);

        // execute sql query
        logger.info("Measuring database datasource");
        DatabaseUtil databaseUtil = new DatabaseUtil(databaseDataSource);
        try {
            Result result = databaseUtil.measure(query);
            logger.info("Query successful: " + result.getValue());
            return result;
        } catch (DatabaseException e) {
            logger.info("Query failed: " + e.getMessage());
            throw new JobExecutionException("Query failed: " + e.getMessage());
        }
    }

    /**
     * Determines measurement in manual datasource
     *
     * @param manualDataSource
     * @return
     * @throws JobExecutionException
     */
    private Result measureManualDataSource(ManualDataSource manualDataSource) throws JobExecutionException {
        // get value
        logger.info("Measuring manual datasource");
        BigDecimal value = manualDataSource.getValue();
        if (value == null) {
            logger.info("Measured value is null");
            throw new JobExecutionException("Measured value is null");
        }
        logger.info("Measuring successful: " + value);
        return new Result(value);
    }

    /**
     * Determines measurement in csv file
     *
     * @param fileDataSource
     * @param measurement
     * @return
     * @throws JobExecutionException
     */
    private Result measureCsvFileDataSource(FileDataSource fileDataSource, Measurement measurement) throws JobExecutionException {
        logger.info("Measuring file datasource");
        // get attributes and csv util
        Map<String, String> attributes = measurement.getAttributes();
        logger.info("Measuring file datasource");
        CsvUtil util = new CsvUtil(fileDataSource);
        // get value using csv util and attributes
        try {
            Result result = util.measure(attributes);
            logger.info("Measurement successful: " + result.getValue());
            return result;
        } catch (FileException e) {
            logger.info("Measurement failed: " + e.getMessage());
            throw new JobExecutionException("Measurement failed: " + e.getMessage());
        }
    }
}
