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
import de.uniregensburg.iamreportingmodule.core.service.InformationNeedService;
import de.uniregensburg.iamreportingmodule.data.entity.InformationNeed;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.InformationNeedForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;

/**
 * View for editing information need configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Edit Information Need | IAM Reporting Modul")
@Route(value = "informationneeds/edit", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class EditInformationNeedView extends VerticalLayout implements HasUrlParameter<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private InformationNeed informationNeed = null;
    private final InformationNeedForm form = new InformationNeedForm();
    private String id;
    private final InformationNeedService informationNeedService;

    /**
     *
     * @param informationNeedService
     */
    public EditInformationNeedView(InformationNeedService informationNeedService) {
        this.informationNeedService = informationNeedService;
        addClassName("informationneed-edit"); // CSS class name
        setSizeFull(); // whole browser size
    }

    /**
     * Initializes view
     */
    private void init() {
        if (informationNeed == null) {
            logger.info("Information need is null");
            add(new Text("Cannot find information need with id " + id));
        } else {
            configureForm();
            add(form);
        }
    }

    /**
     * Configures form
     */
    private void configureForm() {
        form.addListener(InformationNeedForm.SaveEvent.class, this::saveInformationNeed);
        form.addListener(InformationNeedForm.CloseEvent.class, this::close);
        form.addListener(InformationNeedForm.DeleteEvent.class, this::deleteInformationNeed);

        form.setInformationNeed(informationNeed);
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
        logger.info("Searching information need with id " + id);
        try {
            informationNeed = informationNeedService.findInformationNeedById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            logger.info("Error finding information need");
            logger.info(e.getMessage());
        }
        init();
    }

    /**
     * Handles the save event: saves information need configuration and forwards to overview
     *
     * @param event
     */
    private void saveInformationNeed(InformationNeedForm.SaveEvent event) {
        logger.info("Save information need event");
        try {
            informationNeedService.saveInformationNeed(event.getInformationNeed());
            new SuccessNotification("Success", "Information need with name " + event.getInformationNeed().getName() + " saved successfully." ).open();
            form.getUI().ifPresent(ui -> ui.navigate("informationneeds"));
        } catch (SaveEntityException e) {
            new ErrorNotification("Cannot save information need", e.getMessage()).open();
        }
    }

    /**
     * Handles the delete event: shows confirmation dialog, deletes information need configuration and
     * forwards to overview
     *
     * @param event
     */
    private void deleteInformationNeed(InformationNeedForm.DeleteEvent event) {
        logger.info("Delete information need event");
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete information need");
        dialog.setText("Are you sure you want to delete information need " + informationNeed.getName() + "?");
        dialog.setCancelable(true);
        dialog.addConfirmListener(e -> {
            try {
                informationNeedService.deleteInformationNeed(event.getInformationNeed());
                new SuccessNotification("Success", "Information need with name " + event.getInformationNeed().getName() + " deleted successfully." ).open();
                form.getUI().ifPresent(ui -> ui.navigate("informationneeds"));
            } catch (DeleteEntityException ex) {
                new ErrorNotification("Cannot delete information need", ex.getMessage()).open();
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
    private void close(InformationNeedForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("informationneeds"));
    }
}
