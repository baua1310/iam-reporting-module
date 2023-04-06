package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.data.entity.Measurement;
import de.uniregensburg.iamreportingmodule.data.entity.Scale;
import de.uniregensburg.iamreportingmodule.data.entity.Unit;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.MeasurementForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;

/**
 * View for adding measurement configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Add Measurement | IAM Reporting Modul")
@Route(value = "measurements/add", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AddMeasurementView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MeasurableService service;
    private MeasurementForm form;
    private final Measurement measurement = new Measurement();

    /**
     *
     * @param service
     */
    public AddMeasurementView(MeasurableService service) {
        this.service = service;
        addClassName("measurement-add"); // CSS class name
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
        form = new MeasurementForm(scales, units, service);

        form.addListener(MeasurementForm.SaveEvent.class, this::saveMeasurement);
        form.addListener(MeasurementForm.CloseEvent.class, this::close);

        form.setMeasurement(measurement);

        form.showDelete(false); // hide delete button
    }

    /**
     * Handles the save event: saves measurement configuration and forwards to overview
     *
     * @param event
     */
    private void saveMeasurement(MeasurementForm.SaveEvent event) {
        logger.info("Save measurement event");
        try {
            service.saveMeasurement(event.getMeasurement());
            new SuccessNotification("Success", "Measurement with name " + event.getMeasurement().getName() + " saved successfully." ).open();
            form.getUI().ifPresent(ui -> ui.navigate("measurements"));
        } catch (SaveEntityException e) {
            new ErrorNotification("Cannot save measurement", e.getMessage()).open();
        }
    }

    /**
     * Handles the close event: forwards to overview
     *
     * @param event
     */
    private void close(MeasurementForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("measurements"));
    }
}
