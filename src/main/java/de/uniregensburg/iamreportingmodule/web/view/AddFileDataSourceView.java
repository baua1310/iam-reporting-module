package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.data.entity.FileDataSource;
import de.uniregensburg.iamreportingmodule.core.service.DataSourceService;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.FileDataSourceForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;

/**
 * View for adding file datasource configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Add file datasource | IAM Reporting Modul")
@Route(value = "datasources/file/add", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AddFileDataSourceView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DataSourceService service;
    private final FileDataSourceForm form = new FileDataSourceForm();
    private final FileDataSource dataSource = new FileDataSource();

    /**
     *
     * @param service
     */
    public AddFileDataSourceView(DataSourceService service) {
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
        form.addListener(FileDataSourceForm.SaveEvent.class, this::saveDataSource);
        form.addListener(FileDataSourceForm.CloseEvent.class, this::close);

        form.setDataSource(dataSource);

        form.showDelete(false); // hide delete button
    }

    /**
     * Handles the save event: saves file datasource configuration and forwards to overview
     *
     * @param event
     */
    private void saveDataSource(FileDataSourceForm.SaveEvent event) {
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
    private void close(FileDataSourceForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("datasources"));
    }
}