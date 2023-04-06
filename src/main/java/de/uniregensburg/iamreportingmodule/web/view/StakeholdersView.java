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
import de.uniregensburg.iamreportingmodule.core.service.GroupService;
import de.uniregensburg.iamreportingmodule.data.entity.Stakeholder;
import de.uniregensburg.iamreportingmodule.data.entity.User;

import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;

/**
 * View for displaying all stakeholder configurations
 *
 * @author Julian Bauer
 */
@PageTitle("Stakeholders | IAM Reporting Modul")
@Route(value = "stakeholders", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class StakeholdersView extends VerticalLayout {

    private final Grid<Stakeholder> grid = new Grid<>(Stakeholder.class);
    private final GroupService service;

    private final Button addButton = new Button("Add stakeholder"); // add button

    /**
     *
     * @param service
     */
    public StakeholdersView(GroupService service) {
        this.service = service;
        addClassName("stakeholder-view"); // CSS class name
        setSizeFull(); // whole browser size

        configureGrid(); // configure grid

        add(grid); // add grid to view

        updateList(); // update data
    }

    /**
     * Configures grid
     */
    private void configureGrid() {
        grid.addClassName("stakeholder-grid"); // set css class name
        grid.setSizeFull(); // use full size available
        grid.setColumns("name"); // set primitive columns: name
        grid.addColumn(stakeholder -> stakeholder.getMembers().stream().map(User::getFullName).collect(Collectors.joining(", "))).setHeader("Members"); // set complex column: members

        grid.getColumns().forEach(col -> col.setAutoWidth(true)); // auto resize

        grid.asSingleSelect().addValueChangeListener(e -> editStakeholder(e.getValue()));
    }

    /**
     * Updates list of items
     */
    private void updateList() {
        grid.setItems(service.findAllStakeholders());
    }

    /**
     * Forwards to page for adding stakeholder configuration
     */
    private void addStakeholder() {
        getUI().ifPresent(ui -> ui.navigate("stakeholders/add"));
    }

    /**
     * Forwards to page for editing stakeholder configuration
     *
     * @param stakeholder
     */
    private void editStakeholder(Stakeholder stakeholder) {
        getUI().ifPresent(ui -> ui.navigate("stakeholders/edit/" + stakeholder.getId()));
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(addButton);

        addButton.addClickListener(e -> addStakeholder()); // button action
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
