package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.data.entity.Metric;
import de.uniregensburg.iamreportingmodule.data.entity.Scale;
import de.uniregensburg.iamreportingmodule.data.entity.Unit;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.MetricForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;

/**
 * View for adding metric configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Add Metric | IAM Reporting Modul")
@Route(value = "metrics/add", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AddMetricView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MeasurableService service;
    private MetricForm form;
    private final Metric metric = new Metric();

    /**
     *
     * @param service
     */
    public AddMetricView(MeasurableService service) {
        this.service = service;
        addClassName("metric-add"); // CSS class name
        setSizeFull(); // whole browser size

        // configure form
        configureForm();

        // add form to layout
        add(form);
    }

    /**
     * Configures form
     */
    private void configureForm() {
        List<Scale> scales = Arrays.asList(Scale.values());
        List<Unit> units = Arrays.asList(Unit.values());
        form = new MetricForm(scales, units, service);

        form.addListener(MetricForm.SaveEvent.class, this::saveMetric);
        form.addListener(MetricForm.CloseEvent.class, this::close);

        form.setMetric(metric);

        form.showDelete(false); // hide delete button
    }

    /**
     * Handles the save event: saves metric configuration and forwards to overview
     *
     * @param event
     */
    private void saveMetric(MetricForm.SaveEvent event) {
        logger.info("Save metric event");
        try {
            service.saveMetric(event.getMetric());
            new SuccessNotification("Success", "Metric with name " + event.getMetric().getName() + " saved successfully." ).open();
            form.getUI().ifPresent(ui -> ui.navigate("metrics"));
        } catch (SaveEntityException e) {
            new ErrorNotification("Cannot save metric", e.getMessage()).open();
        }
    }

    /**
     * Handles the close event: forwards to overview
     *
     * @param event
     */
    private void close(MetricForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("metrics"));
    }
}
