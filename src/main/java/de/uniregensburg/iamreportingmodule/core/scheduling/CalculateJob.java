package de.uniregensburg.iamreportingmodule.core.scheduling;

import de.uniregensburg.iamreportingmodule.core.exception.FormulaException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.util.FormulaUtil;
import de.uniregensburg.iamreportingmodule.data.entity.Formula;
import de.uniregensburg.iamreportingmodule.data.entity.Metric;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Quartz job definition for calculating metrics
 *
 * @author Julian Bauer
 */
@Component
public class CalculateJob extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeasurableService service;
    private final FormulaUtil util;

    /**
     *
     * @param service
     */
    CalculateJob(MeasurableService service) {
        this.service = service;
        this.util = new FormulaUtil(service);
    }

    /**
     * Overrides job execution definition
     *
     * @param   context   add id of metric (String metricId) to JobDataMap
     * @throws  JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("Starting job execution");
        // get id of metric
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String metricId = jobDataMap.getString("metricId");
        if (metricId.isBlank()) {
            logger.info("No metric id provided");
            throw new JobExecutionException("No metric id provided");
        }
        // get metric
        Metric metric = service.findMetricById(UUID.fromString(metricId));
        if (metric == null) {
            logger.info("Metric is null");
            throw new JobExecutionException("Metric is null");
        }
        logger.info("Metric: " + metric.getName());

        // get formula
        logger.info("Checking formula");
        Formula formula = metric.getFormula();
        if (formula == null) {
            logger.info("Formula is null");
            throw new JobExecutionException("Formula is null");
        }
        if (formula.getFormula().isBlank()) {
            logger.info("Formula is blank");
            throw new JobExecutionException("Formula is blank");
        }
        logger.info("Formula: " + formula.getFormula());

        // calculate
        try {
            Result result = util.calculate(formula.getFormula());
            result.setMeasurable(metric);
            // save result
            service.saveResult(result);
        } catch (FormulaException e) {
            logger.info("Failed to calculate metric " + metric.getName() + ": " + e.getMessage());
        }
    }
}
