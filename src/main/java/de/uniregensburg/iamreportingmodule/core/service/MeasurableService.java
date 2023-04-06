package de.uniregensburg.iamreportingmodule.core.service;

import de.uniregensburg.iamreportingmodule.core.exception.DeleteEntityException;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import de.uniregensburg.iamreportingmodule.data.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for persisting measureables (metrics and measurements)
 *
 * @author  Julian Bauer
 */
@Service
public class MeasurableService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeasurementRepository measurementRepository;
    private final MetricRepository metricRepository;
    private final StakeholderRepository stakeholderRepository;
    private final AudienceRepository audienceRepository;
    private final InformationNeedRepository informationNeedRepository;
    private final ManualDataSourceRepository manualDataSourceRepository;
    private final DatabaseDataSourceRepository databaseDataSourceRepository;
    private final ResultRepository resultRepository;
    private final JobSchedulingService jobSchedulingService;
    private final FileDataSourceRepository fileDataSourceRepository;

    /**
     *
     * @param measurementRepository
     * @param metricRepository
     * @param stakeholderRepository
     * @param audienceRepository
     * @param informationNeedRepository
     * @param manualDataSourceRepository
     * @param databaseDataSourceRepository
     * @param resultRepository
     * @param jobSchedulingService
     * @param fileDataSourceRepository
     */
    public MeasurableService(MeasurementRepository measurementRepository, MetricRepository metricRepository, StakeholderRepository stakeholderRepository, AudienceRepository audienceRepository, InformationNeedRepository informationNeedRepository, ManualDataSourceRepository manualDataSourceRepository, DatabaseDataSourceRepository databaseDataSourceRepository,
                             ResultRepository resultRepository, JobSchedulingService jobSchedulingService,
                             FileDataSourceRepository fileDataSourceRepository) {
        this.measurementRepository = measurementRepository;
        this.metricRepository = metricRepository;
        this.stakeholderRepository = stakeholderRepository;
        this.audienceRepository = audienceRepository;
        this.informationNeedRepository = informationNeedRepository;
        this.manualDataSourceRepository = manualDataSourceRepository;
        this.databaseDataSourceRepository = databaseDataSourceRepository;
        this.resultRepository = resultRepository;
        this.jobSchedulingService = jobSchedulingService;
        this.fileDataSourceRepository = fileDataSourceRepository;
    }

    /**
     * Returns all measurements
     *
     * @return
     */
    public List<Measurement> findAllMeasurements() {
        logger.info("Returning all measurements");
        // get and return all measurements
        return measurementRepository.findAll();
    }

    /**
     * Saves a measurement
     *
     * @param measurement
     * @throws SaveEntityException
     */
    public void saveMeasurement(Measurement measurement) throws SaveEntityException {
        logger.info("Saving measurement");
        // check measurement
        if (measurement == null) {
            logger.info("Measurement is null");
            throw new SaveEntityException("No measurement provided");
        }
        try {
            // save measurement
            measurementRepository.save(measurement);
            logger.info("Measurement saved");
            // schedule job
            jobSchedulingService.measureMeasurement(measurement);
            logger.info("Measuring of measurement scheduled");
        } catch (Exception e) {
            logger.info("Error while saving measurement: " + e.getMessage());
            throw new SaveEntityException("Error while saving measurement");
        }
    }

    /**
     * Deletes a measurement
     *
     * @param measurement
     * @throws DeleteEntityException
     */
    public void deleteMeasurement(Measurement measurement) throws DeleteEntityException {
        logger.info("Deleting measurement");
        // check measurement
        if (measurement == null) {
            logger.info("Measurement is null");
            throw new DeleteEntityException("Measurement is null");
        }
        try {
            // delete measurement
            measurementRepository.delete(measurement);
            // stop job
            jobSchedulingService.stopMeasurement(measurement);
            logger.info("Measurement deleted");
        } catch (Exception ex) {
            logger.info("Cannot delete measurement: " + ex.getMessage());
            throw new DeleteEntityException("Measurement is probably used in one or more formulas");
        }
    }

    /**
     * Returns a measurement by id
     *
     * @param id
     * @return
     */
    public Measurement findMeasurementById(UUID id) {
        logger.info("Searching measurement with id " + id);
        // search measurement by id
        Optional<Measurement> measurement = measurementRepository.findById(id);

        // check measurement
        if (measurement.isEmpty()) {
            logger.info("Measurement not found");
            return null;
        }

        logger.info("Measurement found");
        // return measurement
        return measurement.get();
    }

    /**
     * Returns all metrics
     *
     * @return
     */
    public List<Metric> findAllMetrics() {
        logger.info("Returning all metrics");
        // get and return all metrics
        return metricRepository.findAll();
    }

    /**
     * Saves a metric
     *
     * @param metric
     * @throws SaveEntityException
     */
    public void saveMetric(Metric metric) throws SaveEntityException {
        logger.info("Saving metric");
        // check metric
        if (metric == null) {
            logger.info("metric is null");
            throw new SaveEntityException("No metric provided");
        }
        try {
            // save metric
            metricRepository.save(metric);
            logger.info("Metric saved");
            // schedule job
            jobSchedulingService.calculateMetric(metric);
            logger.info("Calculation of metric scheduled");
        } catch (Exception e) {
            logger.info("Error while saving metric: " + e.getMessage());
            throw new SaveEntityException("Error while saving metric");
        }
    }

    /**
     * Deletes a metric
     *
     * @param metric
     * @throws DeleteEntityException
     */
    public void deleteMetric(Metric metric) throws DeleteEntityException {
        logger.info("Deleting metric");
        // check metric
        if (metric == null) {
            logger.info("Metric is null");
            throw new DeleteEntityException("Metric is null");
        }
        try {
            // delete metric
            metricRepository.delete(metric);
            // stop job
            jobSchedulingService.stopCalculation(metric);
            logger.info("Metric deleted");
        } catch (Exception ex) {
            logger.info("Cannot delete metric: " + ex.getMessage());
            throw new DeleteEntityException("Metric is probably used in one or more formulas");
        }
    }

    /**
     * Returns an metric by id
     *
     * @param id
     * @return
     */
    public Metric findMetricById(UUID id) {
        logger.info("Searching metric with id " + id);
        // search metric by id
        Optional<Metric> metric = metricRepository.findById(id);

        // check metric
        if (metric.isEmpty()) {
            logger.info("Metric not found");
            return null;
        }

        logger.info("Metric found");
        // return metric
        return metric.get();
    }

    /**
     * Returns all stakeholders
     *
     * @return
     */
    public List<Stakeholder> findAllStakeholders() {
        logger.info("Returning all stakeholders");
        // get and return all
        return stakeholderRepository.findAll();
    }

    /**
     * Returns all information needs
     *
     * @return
     */
    public List<InformationNeed> findAllInformationNeeds() {
        logger.info("Returning all information needs");
        // get and return all
        return informationNeedRepository.findAll();
    }

    /**
     * Returns all audiences
     *
     * @return
     */
    public List<Audience> findAllAudiences() {
        logger.info("Returning all audiences");
        // get and return all
        return audienceRepository.findAll();
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
     * Returns measureables by label
     *
     * @param label
     * @return
     */
    public List<Measurable> findAllMeasurablesByLabel(String label) {
        logger.info("Returning all measurables with label " + label);
        // search measurables by label
        List<Measurable> measurables = new ArrayList<>();
        measurables.addAll(metricRepository.findAllByLabel(label));
        measurables.addAll(measurementRepository.findAllByLabel(label));
        // return measurables
        return measurables;
    }

    /**
     * Returns latest 10 results by measurable
     *
     * @param measurable
     * @return
     */
    public List<Result> findFirst10ResultsByMeasurableOrderByPointInTimeDesc(Measurable measurable) {
        logger.info("Returning all results of measureable " + measurable.getName());
        // search and return latest 10 results by measurable
        return resultRepository.findFirst10ByMeasurableOrderByPointInTimeDesc(measurable);
    }

    /**
     * Returns latest result by measurable
     *
     * @param measurable
     * @return
     */
    public Result findLatestResultByMeasurable(Measurable measurable) {
        logger.info("Returning latest result of measureable " + measurable.getName());
        // search and return latest result by measurable
        return resultRepository.findFirstByMeasurableOrderByPointInTimeDesc(measurable);
    }

    /**
     * Saves result
     *
     * @param result
     */
    public void saveResult(Result result) {
        logger.info("Saving result");
        // check result
        if (result == null) {
            logger.info("Result is null");
            return;
        }

        // save result
        resultRepository.save(result);
        logger.info("Result saved");
    }

    /**
     * Returns metrics by information need and audiences
     * @param informationNeed
     * @param audiences
     * @return
     */
    public List<Metric> findMetricsByInformationNeedAndAudiences(InformationNeed informationNeed, Set<Audience> audiences) {
        logger.info("Returning all metrics with information need " + informationNeed);
        // search and return metrics by information needs and audiences
        return metricRepository.findAllByInformationNeedsInAndAudiencesIn(Set.of(informationNeed), audiences);
    }

    /**
     * Returns measurable by id
     *
     * @param id
     * @return
     */
    public Measurable findMeasurableById(UUID id) {
        // search metric by id
        Optional<Metric> metric = metricRepository.findById(id);
        // search measurement by id
        Optional<Measurement> measurement = measurementRepository.findById(id);

        // check if metric exists
        if (metric.isPresent()) {
            logger.info("Metric found");
            // return metric
            return metric.get();
        }
        // check if measurement exists
        if (measurement.isPresent()) {
            logger.info("Measurement found");
            // return measurement
            return measurement.get();
        }

        logger.info("Measurable not found");
        // nothing found, return null
        return null;
    }
}