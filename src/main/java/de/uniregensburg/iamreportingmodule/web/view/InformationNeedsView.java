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
import de.uniregensburg.iamreportingmodule.core.service.InformationNeedService;

import javax.annotation.security.RolesAllowed;

/**
 * View for displaying all information need configurations
 *
 * @author Julian Bauer
 */
@PageTitle("Information Needs | IAM Reporting Modul")
@Route(value = "informationneeds", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class InformationNeedsView extends VerticalLayout {

    private final Grid<InformationNeed> grid = new Grid<>(InformationNeed.class);
    private final InformationNeedService service;

    private final Button addButton = new Button("Add information need"); // add button

    /**
     *
     * @param service
     */
    public InformationNeedsView(InformationNeedService service) {
        this.service = service;
        addClassName("informationneed-view"); // CSS class name
        setSizeFull(); // whole browser size

        configureGrid(); // configure grid

        add(grid); // add grid to view

        updateList(); // update data
    }

    /**
     * Configures grid
     */
    private void configureGrid() {
        grid.addClassName("informationneed-grid"); // set css class name
        grid.setSizeFull(); // use full size available
        grid.setColumns("name", "description"); // set primitiv columns: name, description

        grid.getColumns().forEach(col -> col.setAutoWidth(true)); // auto resize

        grid.asSingleSelect().addValueChangeListener(e -> editInformationNeed(e.getValue()));
    }

    /**
     * Updates list of items
     */
    private void updateList() {
        grid.setItems(service.findAllInformationNeeds());
    }

    /**
     * Forwards to page for adding information need configuration
     */
    private void addInformationNeed() {
        getUI().ifPresent(ui -> ui.navigate("informationneeds/add"));
    }

    /**
     * Forwards to page for editing information need configuration
     *
     * @param informationNeed
     */
    private void editInformationNeed(InformationNeed informationNeed) {
        getUI().ifPresent(ui -> ui.navigate("informationneeds/edit/" + informationNeed.getId()));
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(addButton);

        addButton.addClickListener(e -> addInformationNeed()); // button action
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