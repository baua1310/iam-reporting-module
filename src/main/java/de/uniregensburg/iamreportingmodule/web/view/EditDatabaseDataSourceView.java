package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.exception.DeleteEntityException;
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
import java.util.UUID;

/**
 * View for editing database datasource configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Edit database datasource | IAM Reporting Modul")
@Route(value = "datasources/database/edit", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class EditDatabaseDataSourceView extends VerticalLayout implements HasUrlParameter<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Dbms> dbmsTypeList = List.of(Dbms.POSTGRESQL);
    private final DataSourceService service;
    private final DatabaseDataSourceForm form = new DatabaseDataSourceForm(dbmsTypeList);
    private DatabaseDataSource dataSource = null;
    private String id;

    /**
     *
     * @param service
     */
    public EditDatabaseDataSourceView(DataSourceService service) {
        this.service = service;
        addClassName("datasource-edit"); // CSS class name
        setSizeFull(); // whole browser size
    }

    /**
     * Initializes view
     */
    private void init() {
        if (dataSource == null) {
            logger.info("Data Source is null");
            add(new Text("Cannot find datasource with id " + id));
        } else {
            configureForm();
            add(form);
        }
    }

    /**
     * Searches for url parameter id and sets database datasource configuration
     *
     * @param event
     * @param id
     */
    @Override
    public void setParameter(BeforeEvent event, String id) {
        this.id = id;
        logger.info("Searching manual datasource id " + id);
        try {
            dataSource = service.findDatabaseDataSourceById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            logger.info("Error finding manual datasource");
            logger.info(e.getMessage());
        }
        init();
    }

    /**
     * Configures form
     */
    private void configureForm() {
        form.addListener(DatabaseDataSourceForm.SaveEvent.class, this::saveDataSource);
        form.addListener(DatabaseDataSourceForm.CloseEvent.class, this::close);
        form.addListener(DatabaseDataSourceForm.DeleteEvent.class, this::deleteDataSource);

        form.setDataSource(dataSource);
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

    /**
     * Handles the delete event: shows confirmation dialog, deletes database datasource configuration and
     * forwards to overview
     *
     * @param event
     */
    private void deleteDataSource(DatabaseDataSourceForm.DeleteEvent event) {
        logger.info("Delete database datasource event");
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete datasource");
        dialog.setText("Are you sure you want to delete datasource " + dataSource.getName() + "?");
        dialog.setCancelable(true);
        dialog.addConfirmListener(e -> {try {
            service.deleteDataSource(event.getDataSource());
            new SuccessNotification("Success", "Datasource with name " + event.getDataSource().getName() + " deleted successfully." ).open();
            form.getUI().ifPresent(ui -> ui.navigate("datasources"));
        } catch (DeleteEntityException ex) {
            new ErrorNotification("Cannot delete datasource", ex.getMessage()).open();
        }
        });
        dialog.addCancelListener(e -> logger.info("Deletion canceled"));
        dialog.open();
    }
}