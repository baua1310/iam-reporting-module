package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.core.service.DataSourceService;
import de.uniregensburg.iamreportingmodule.data.entity.ManualDataSource;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.ManualDataSourceForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;

/**
 * View for adding manual datasource configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Add manual datasource | IAM Reporting Modul")
@Route(value = "datasources/manual/add", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AddManualDataSourceView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DataSourceService service;
    private final ManualDataSourceForm form = new ManualDataSourceForm();
    private final ManualDataSource dataSource = new ManualDataSource();

    /**
     *
     * @param service
     */
    public AddManualDataSourceView(DataSourceService service) {
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
        form.addListener(ManualDataSourceForm.SaveEvent.class, this::saveDataSource);
        form.addListener(ManualDataSourceForm.CloseEvent.class, this::close);

        form.setDataSource(dataSource);

        form.showDelete(false); // hide delete button
    }

    /**
     * Handles the save event: saves manual datasource configuration and forwards to overview
     *
     * @param event
     */
    private void saveDataSource(ManualDataSourceForm.SaveEvent event) {
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
    private void close(ManualDataSourceForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("datasources"));
    }
}