package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.core.service.DataSourceService;
import de.uniregensburg.iamreportingmodule.data.entity.DatabaseDataSource;
import de.uniregensburg.iamreportingmodule.data.entity.Dbms;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.DatabaseDataSourceForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.List;

/**
 * View for adding database datasource configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Add database datasource | IAM Reporting Modul")
@Route(value = "datasources/database/add", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AddDatabaseDataSourceView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Dbms> dbmsTypeList = List.of(Dbms.POSTGRESQL);
    private final DataSourceService service;
    private DatabaseDataSourceForm form;
    private final DatabaseDataSource dataSource = new DatabaseDataSource();

    /**
     *
     * @param service
     */
    public AddDatabaseDataSourceView(DataSourceService service) {
        this.service = service;
        addClassName("datasource-add"); // CSS class name
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
        form = new DatabaseDataSourceForm(dbmsTypeList);

        form.addListener(DatabaseDataSourceForm.SaveEvent.class, this::saveDataSource);
        form.addListener(DatabaseDataSourceForm.CloseEvent.class, this::close);

        form.setDataSource(dataSource);

        form.showDelete(false); // hide delete button
    }

    /**
     * Handles the save event: saves database datasource configuration and forwards to overview
     *
     * @param event
     */
    private void saveDataSource(DatabaseDataSourceForm.SaveEvent event) {
        logger.info("Save datasource event");
        try {
            service.saveDataSource(event.getDataSource());
            new SuccessNotification("Success", "Datasource with name " + event.getDataSource().getName() + " saved successfully." ).open();
            form.getUI().ifPresent(ui -> ui.navigate("datasources"));
        } catch (SaveEntityException e) {
            new ErrorNotification("Cannot save datasource", e.getMessage()).open();
        }
    }

    /**
     * Handles the close event: forwards to overview
     *
     * @param event
     */
    private void close(DatabaseDataSourceForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("datasources"));
    }
}