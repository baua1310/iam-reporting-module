package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.data.entity.Measurement;

import javax.annotation.security.RolesAllowed;

/**
 * View for displaying all measurement configurations
 *
 * @author Julian Bauer
 */
@PageTitle("Measurements | IAM Reporting Modul")
@Route(value = "measurements", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class MeasurementsView extends VerticalLayout {

    private final Grid<Measurement> grid = new Grid<>(Measurement.class);
    private final MeasurableService service;

    private final Button addButton = new Button("Add measurement"); // add button

    /**
     *
     * @param service
     */
    public MeasurementsView(MeasurableService service) {
        this.service = service;
        addClassName("measurement-view"); // CSS class name
        setSizeFull(); // whole browser size

        configureGrid(); // configure grid

        add(grid); // add grid to view

        updateList(); // update data
    }

    /**
     * Updates list of items
     */
    private void updateList() {
        grid.setItems(service.findAllMeasurements());
    }

    /**
     * Configures grid
     */
    private void configureGrid() {
        grid.addClassName("measurement-grid"); // set css class name
        grid.setSizeFull(); // use full size available
        grid.setColumns("name"); // set primitiv columns: name
        grid.addColumn(measurement -> measurement.getDataSource().getName()).setHeader("Datasource"); // set complex column: datasource
        grid.addColumn(measurement -> {
            if (measurement.getFormulas() == null) {
                return 0;
            }
            return measurement.getFormulas().size();
        }).setHeader("Metrics");
        grid.getColumns().forEach(col -> col.setAutoWidth(true)); // auto resize

        grid.asSingleSelect().addValueChangeListener(e -> editMeasurement(e.getValue()));
    }

    /**
     * Forwards to page for adding measurement configuration
     */
    private void addMeasurement() {
        getUI().ifPresent(ui -> ui.navigate("measurements/add"));
    }

    /**
     * Forwards to page for editing measurement configuration
     *
     * @param measurement
     */
    private void editMeasurement(Measurement measurement) {
        getUI().ifPresent(ui -> ui.navigate("measurements/edit/" + measurement.getId()));
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(addButton);

        addButton.addClickListener(e -> addMeasurement()); // button action
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // primary button
    }

    /**
     * Adds button to navbar on attach event
     *
     * @param attachEvent
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Add menu bar
        MainLayout ml = MainLayout.getInstance();
        if (ml != null) {
            initNavbarButtons(ml.getNavbarButtons());
        }
    }

    /**
     * Removes buttons from navbar on detach event
     *
     * @param detachEvent
     */
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Remove menu bar
        MainLayout ml = MainLayout.getInstance();
        if (ml != null) {
            ml.getNavbarButtons().removeAll();
        }

        super.onDetach(detachEvent);
    }

}