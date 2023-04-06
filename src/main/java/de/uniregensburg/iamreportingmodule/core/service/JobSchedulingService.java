package de.uniregensburg.iamreportingmodule.core.service;

import de.uniregensburg.iamreportingmodule.core.exception.JobSchedulingException;
import de.uniregensburg.iamreportingmodule.core.scheduling.CalculateJob;
import de.uniregensburg.iamreportingmodule.core.scheduling.MeasureJob;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service for scheduling quartz jobs
 *
 * @author Julian Bauer
 */
@Service
public class JobSchedulingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Scheduler scheduler;

    /**
     *
     * @param scheduler
     */
    public JobSchedulingService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Schedules job for calculation of metric
     *
     * @param metric
     * @throws JobSchedulingException
     */
    public void calculateMetric(Metric metric) throws JobSchedulingException {
        logger.info("Scheduling calculation of metric");
        // check metric
        if (metric == null) {
            logger.info("Metric is null");
            throw new JobSchedulingException("Metric is null");
        }
        logger.info("Metric: " + metric.getName());

        logger.info("Checking formula");
        // check formula
        Formula formula = metric.getFormula();
        if (formula == null) {
            logger.info("Formula is null");
            throw new JobSchedulingException("Formula is null");
        }
        if (formula.getFormula().isBlank()) {
            logger.info("Formula is blank");
            throw new JobSchedulingException("Formula is blank");
        }
        logger.info("Formula: " + formula.getFormula());

        logger.info("Checking if a calculation job for metric already exists");
        // check if job for metric already exists
        TriggerKey triggerKey = TriggerKey.triggerKey(metric.getId().toString(), "calculateMetrics");
        JobKey jobKey = JobKey.jobKey(metric.getId().toString(), "calculateMetrics");

        Trigger trigger = getTrigger(metric, triggerKey);

        if (checkJobAndTriggerExists(jobKey, triggerKey)) { // job and trigger exist
            // reschedule job
            rescheduleJob(trigger);
        } else { // job and trigger do not exist
            logger.info("Job and trigger do not already exist");

            logger.info("Adding metric to job data map");
            // add id of metric to job information
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("metricId", metric.getId().toString());

            logger.info("Creating job");
            // create job
            JobDetail job = JobBuilder.newJob(CalculateJob.class)
                    .withIdentity(jobKey)
                    .withDescription("Calculate metric " + metric)
                    .setJobData(jobDataMap)
                    .build();
            logger.info("Scheduling job");

            // schedule job
            scheduleJob(job, trigger);
        }

        logger.info("Calculation of metric scheduled");
    }

    /**
     * Schedules job for determination of measurement
     *
     * @param measurement
     * @throws JobSchedulingException
     */
    public void measureMeasurement(Measurement measurement) throws JobSchedulingException {
        logger.info("Scheduling measuring of measurement");
        // check measurement
        if (measurement == null) {
            logger.info("Measurement is null");
            throw new JobSchedulingException("Measurement is null");
        }
        logger.info("Measurement: " + measurement.getName());

        logger.info("Checking if a measuring job for measurement already exists");
        // check if job for measurement already exists
        TriggerKey triggerKey = TriggerKey.triggerKey(measurement.getId().toString(), "measureMeasurement");
        JobKey jobKey = JobKey.jobKey(measurement.getId().toString(), "measureMeasurement");

        Trigger trigger = getTrigger(measurement, triggerKey);

        if (checkJobAndTriggerExists(jobKey, triggerKey)) { // job and trigger exist
            // reschedule job
            rescheduleJob(trigger);
        } else { // job and trigger do not exist
            logger.info("Job and trigger do not already exist");

            logger.info("Adding measurement to job data map");
            // add id of metric to job information
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("measurementId", measurement.getId().toString());

            logger.info("Creating job");
            // create job
            JobDetail job = JobBuilder.newJob(MeasureJob.class)
                    .withIdentity(jobKey)
                    .withDescription("Measure measurement " + measurement)
                    .setJobData(jobDataMap)
                    .build();

            // schedule job
            scheduleJob(job, trigger);
        }

        logger.info("Measuring of measurement scheduled");
    }

    /**
     * Checks if job and trigger exists
     *
     * @param jobKey
     * @param triggerKey
     * @return
     * @throws JobSchedulingException
     */
    private boolean checkJobAndTriggerExists(JobKey jobKey, TriggerKey triggerKey) throws JobSchedulingException {
        try {
            return (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey));
        } catch (SchedulerException e) {
            logger.info("Error while checking if job exists");
            throw new JobSchedulingException(e.getMessage());
        }
    }

    /**
     * Reschedules job with new trigger
     *
     * @param trigger
     * @throws JobSchedulingException
     */
    private void rescheduleJob(Trigger trigger) throws JobSchedulingException {
        logger.info("Job and trigger exist");
        try {
            logger.info("Rescheduling existing job");
            scheduler.rescheduleJob(trigger.getKey(), trigger);
        } catch (SchedulerException e) {
            logger.info("Error while rescheduling existing job");
            throw new JobSchedulingException(e.getMessage());
        }
    }

    /**
     * Schedules job using trigger
     *
     * @param job
     * @param trigger
     * @throws JobSchedulingException
     */
    private void scheduleJob(JobDetail job, Trigger trigger) throws JobSchedulingException {
        logger.info("Scheduling job");
        try {
            // schedule job
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            logger.info("Scheduling of job failed: " + e.getMessage());
            throw new JobSchedulingException(e.getMessage());
        }
    }

    /**
     * Returns trigger for measurable
     *
     * @param measurable
     * @param triggerKey
     * @return
     * @throws JobSchedulingException
     */
    private Trigger getTrigger(Measurable measurable, TriggerKey triggerKey) throws JobSchedulingException {
        logger.info("Checking frequency");
        // check frequency
        Frequency frequency = measurable.getFrequency();
        if (frequency == null) {
            throw new JobSchedulingException("Frequency is null");
        }
        // check duration
        Duration duration = frequency.getDuration();
        if (duration == null) {
            throw new JobSchedulingException("Duration is null");
        }
        logger.info("Frequency: " + duration);

        logger.info("Creating trigger");
        // create trigger using duration
        return TriggerBuilder.newTrigger().startNow()
                .withIdentity(triggerKey)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .repeatForever()
                        .withIntervalInMilliseconds(duration.toMillis()))
                .build();
    }

    /**
     * Stops job for calculation of metric
     *
     * @param metric
     * @return
     * @throws JobSchedulingException
     */
    public boolean stopCalculation(Metric metric) throws JobSchedulingException {
        logger.info("Stop calculation of metric");
        if (metric == null) {
            logger.info("Metric is null");
            throw new JobSchedulingException("Metric is null");
        }
        logger.info("Metric: " + metric.getName());

        JobKey jobKey = JobKey.jobKey(metric.getId().toString(), "calculateMetrics");
        return deleteJob(jobKey);
    }

    /**
     * Stops job for determination of measurement
     *
     * @param measurement
     * @return
     * @throws JobSchedulingException
     */
    public boolean stopMeasurement(Measurement measurement) throws JobSchedulingException {
        logger.info("Stop measuring of measurement");
        if (measurement == null) {
            logger.info("Measurement is null");
            throw new JobSchedulingException("Measurement is null");
        }
        logger.info("Measurement: " + measurement.getName());

        JobKey jobKey = JobKey.jobKey(measurement.getId().toString(), "measureMeasurement");
        return deleteJob(jobKey);
    }

    /**
     * Deletes job
     *
     * @param jobKey
     * @return
     * @throws JobSchedulingException
     */
    private boolean deleteJob(JobKey jobKey) throws JobSchedulingException {
        logger.info("Deleting job");
        try {
            return scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            logger.info("Error while deleting job");
            throw new JobSchedulingException(e.getMessage());
        }
    }

}
