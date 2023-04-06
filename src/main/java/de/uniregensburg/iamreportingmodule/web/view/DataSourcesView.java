package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.service.DataSourceService;
import de.uniregensburg.iamreportingmodule.data.entity.DataSource;
import de.uniregensburg.iamreportingmodule.data.entity.DataSourceType;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;

/**
 * View for displaying all datasource configurations
 *
 * @author Julian Bauer
 */
@PageTitle("Datasources | IAM Reporting Modul")
@Route(value = "datasources", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class DataSourcesView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Grid<DataSource> grid = new Grid<>(DataSource.class);
    private final DataSourceService service;

    private final Button addButton = new Button("Add datasource"); // add button

    /**
     *
     * @param service
     */
    public DataSourcesView(DataSourceService service) {
        this.service = service;
        addClassName("datasource-view"); // CSS class name
        setSizeFull(); // whole browser size

        configureGrid(); // configure grid

        add(grid); // add grid to view

        updateList(); // update data
    }

    /**
     * Configures grid
     */
    private void configureGrid() {
        grid.addClassName("datasource-grid"); // set css class name
        grid.setSizeFull(); // use full size available
        grid.setColumns("name"); // set primitiv columns: name
        grid.addColumn(DataSource::getType).setHeader("Type");
        grid.addColumn(dataSource -> {
            if (dataSource.getMeasurements() == null) {
                return 0;
            }
            return dataSource.getMeasurements().size();
        }).setHeader("Measurements");

        grid.getColumns().forEach(col -> col.setAutoWidth(true)); // auto resize

        grid.asSingleSelect().addValueChangeListener(e -> editDataSource(e.getValue()));
    }

    /**
     * Updates list of items
     */
    private void updateList() {
        grid.setItems(service.findAllDataSources());
    }

    /**
     * Forwards to page for adding datasource configuration after selection of type of datasource
     */
    private void addDataSource() {
        logger.info("New datasource");

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New datasource");

        ComboBox<DataSourceType> type = new ComboBox<>("Type");
        type.setItems(DataSourceType.MANUAL, DataSourceType.DATABASE, DataSourceType.FILE);
        type.setPlaceholder("Select type");

        VerticalLayout dialogLayout = new VerticalLayout(type);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        dialog.add(dialogLayout);

        Button confirmButton = new Button("Add", e -> {
            if (type.getValue() == null) {
                new ErrorNotification("No type selected").open();
            } else {
                if (type.getValue().equals(DataSourceType.MANUAL)) {
                    dialog.close();
                    getUI().ifPresent(ui -> ui.navigate("datasources/manual/add"));
                } else if (type.getValue().equals(DataSourceType.DATABASE)) {
                    dialog.close();
                    getUI().ifPresent(ui -> ui.navigate("datasources/database/add"));
                } else if (type.getValue().equals(DataSourceType.FILE)) {
                    dialog.close();
                    getUI().ifPresent(ui -> ui.navigate("datasources/file/add"));
                } else {
                    new ErrorNotification("Not implemented yet").open();
                }
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> {
            dialog.close();
            logger.info("Creation canceled");
        });

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(confirmButton);

        dialog.open();
    }

    /**
     * Forwards to page for editing datasource configuration
     *
     * @param dataSource
     */
    private void editDataSource(DataSource dataSource) {
        DataSourceType type = dataSource.getType();
        if (type.equals(DataSourceType.MANUAL)) {
            getUI().ifPresent(ui -> ui.navigate("datasources/manual/edit/" + dataSource.getId()));
        } else if (type.equals(DataSourceType.DATABASE)) {
            getUI().ifPresent(ui -> ui.navigate("datasources/database/edit/" + dataSource.getId()));
        } else if (type.equals(DataSourceType.FILE)) {
            getUI().ifPresent(ui -> ui.navigate("datasources/file/edit/" + dataSource.getId()));
        } else {
            new ErrorNotification("Not implemented yet").open();
        }
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(addButton);

        addButton.addClickListener(e -> addDataSource()); // button action
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
