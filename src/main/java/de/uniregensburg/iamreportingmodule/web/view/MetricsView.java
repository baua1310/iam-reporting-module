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
import de.uniregensburg.iamreportingmodule.data.entity.InformationNeed;
import de.uniregensburg.iamreportingmodule.data.entity.Metric;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;

import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;

/**
 * View for displaying all metric configurations
 *
 * @author Julian Bauer
 */
@PageTitle("Metrics | IAM Reporting Modul")
@Route(value = "metrics", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class MetricsView extends VerticalLayout {

    private final Grid<Metric> grid = new Grid<>(Metric.class);
    private final MeasurableService service;

    private final Button addButton = new Button("Add metric"); // add button

    /**
     *
     * @param service
     */
    public MetricsView(MeasurableService service) {
        this.service = service;
        addClassName("metric-view"); // CSS class name
        setSizeFull(); // whole browser size

        configureGrid(); // configure grid

        add(grid); // add grid to view

        updateList(); // update data
    }


    /**
     * Updates list of items
     */
    private void updateList() {
        grid.setItems(service.findAllMetrics());
    }

    /**
     * Configures grid
     */
    private void configureGrid() {
        grid.addClassName("metric-grid"); // set css class name
        grid.setSizeFull(); // use full size available
        grid.setColumns("name"); // set primitiv columns: name
        grid.addColumn(metric -> metric.getInformationNeeds().stream().map(InformationNeed::getName).collect(Collectors.joining(", "))).setHeader("Information needs"); // set complex column: information needs
        grid.addColumn(metric -> metric.getFormula().getFormula()).setHeader("Formula"); // set complex column: frequency
        grid.getColumns().forEach(col -> col.setAutoWidth(true)); // auto resize

        grid.asSingleSelect().addValueChangeListener(e -> editMetric(e.getValue()));
    }

    /**
     * Forwards to page for adding metric configuration
     */
    private void addMetric() {
        getUI().ifPresent(ui -> ui.navigate("metrics/add"));
    }

    /**
     * Forwards to page for editing metric configuration
     *
     * @param metric
     */
    private void editMetric(Metric metric) {
        getUI().ifPresent(ui -> ui.navigate("metrics/edit/" + metric.getId()));
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(addButton);

        addButton.addClickListener(e -> addMetric()); // button action
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