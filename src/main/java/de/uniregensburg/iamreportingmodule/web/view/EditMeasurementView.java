package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.uniregensburg.iamreportingmodule.core.exception.DeleteEntityException;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.data.entity.Measurement;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import de.uniregensburg.iamreportingmodule.data.entity.Scale;
import de.uniregensburg.iamreportingmodule.data.entity.Unit;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.MeasurementForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * View for editing measurement configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Edit Measurement | IAM Reporting Modul")
@Route(value = "measurements/edit", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class EditMeasurementView extends VerticalLayout implements HasUrlParameter<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Measurement measurement = null;
    private MeasurementForm form;
    private String id;
    private final MeasurableService service;
    private final Grid<Result> grid = new Grid<>(Result.class);
    private final Text warningText = new Text("There is no data to display");
    private final Div warning = new Div();

    /**
     *
     * @param service
     */
    public EditMeasurementView(MeasurableService service) {
        this.service = service;
        addClassName("measurement-edit"); // CSS class name
        setSizeFull(); // whole browser size
    }

    /**
     * Initializes view
     */
    private void init() {
        if (measurement == null) {
            logger.info("Measurement is null");
            add(new Text("Cannot find measurement with id " + id));
        } else {
            configureForm();
            configureGrid();
            configureWarning();

            H2 formHeading = new H2("Form");
            H2 gridHeading = new H2("Latest ten results");

            Div gridRoot = new Div();
            gridRoot.addClassName("grid-root");
            gridRoot.addClassName(LumoUtility.Position.RELATIVE);
            gridRoot.setSizeFull();

            gridRoot.add(grid, warning);

            add(formHeading, form, gridHeading, gridRoot);
            updateResults();
        }
    }

    /**
     * Updates results
     */
    private void updateResults() {
        List<Result> results = service.findFirst10ResultsByMeasurableOrderByPointInTimeDesc(measurement);
        logger.info("Results: " + results.size());
        grid.setItems(results);
        if (results.isEmpty()) {
            warning.removeClassName(LumoUtility.Display.HIDDEN);
        } else {
            warning.addClassName(LumoUtility.Display.HIDDEN);
        }
    }

    /**
     * Configures warning
     */
    private void configureWarning() {
        warning.addClassName("warning");
        warning.addClassName(LumoUtility.JustifyContent.CENTER);
        warning.addClassName(LumoUtility.AlignItems.CENTER);
        warning.addClassName(LumoUtility.FontSize.XLARGE);
        warning.getStyle().set("width", "100%"); // use full width available
        warning.getStyle().set("height", "420px"); // set height
        warning.addClassName(LumoUtility.Position.ABSOLUTE);
        warning.getStyle().set("top", "0px");
        warning.getStyle().set("left", "0px");
        warning.addClassName(LumoUtility.Display.FLEX);
        warning.add(warningText);
    }

    /**
     * Configures grid
     */
    private void configureGrid() {
        grid.setPageSize(10); // show only ten elements
        grid.addClassName("result-grid"); // set css class name
        grid.getStyle().set("width", "100%"); // use full width available
        grid.getStyle().set("height", "420px"); // set height
        grid.addClassName(LumoUtility.Position.ABSOLUTE);
        grid.getStyle().set("top", "0px");
        grid.getStyle().set("left", "0px");
        grid.removeAllColumns();
        grid.setAllRowsVisible(true);
        grid.addColumn(result -> result.getValue().stripTrailingZeros().toPlainString()).setHeader("Value").setKey("value");
        grid.addColumn(Result::getPointInTime).setHeader("Point in Time").setKey("pointInTime");
        Grid.Column<Result> pointInTime = grid.getColumnByKey("pointInTime");
        GridSortOrder<Result> order = new GridSortOrder<>(pointInTime, SortDirection.DESCENDING);
        grid.sort(List.of(order)); // set sorting
        grid.getColumns().forEach(col -> col.setAutoWidth(true)); // auto resize
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
        form.addListener(MeasurementForm.DeleteEvent.class, this::deleteMeasurement);

        form.setMeasurement(measurement);
    }

    /**
     * Searches for url parameter id and sets audience configuration
     *
     * @param event
     * @param id
     */
    @Override
    public void setParameter(BeforeEvent event, String id) {
        this.id = id;
        logger.info("Searching measurement with id " + id);
        try {
            measurement = service.findMeasurementById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            logger.info("Error finding measurement");
            logger.info(e.getMessage());
        }
        init();
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
     * Handles the delete event: shows confirmation dialog, deletes measurement configuration and forwards to overview
     *
     * @param event
     */
    private void deleteMeasurement(MeasurementForm.DeleteEvent event) {
        logger.info("Delete measurement event");
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete measurement");
        dialog.setText("Are you sure you want to delete measurement " + measurement.getName() + "?");
        dialog.setCancelable(true);
        dialog.addConfirmListener(e -> {
            try {
                service.deleteMeasurement(event.getMeasurement());
                new SuccessNotification("Success", "Measurement with name " + event.getMeasurement().getName() + " deleted successfully." ).open();
                form.getUI().ifPresent(ui -> ui.navigate("measurements"));
            } catch (DeleteEntityException ex) {
                new ErrorNotification("Cannot delete measurement", ex.getMessage()).open();
            }
        });
        dialog.addCancelListener(e -> logger.info("Deletion canceled"));
        dialog.open();
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
