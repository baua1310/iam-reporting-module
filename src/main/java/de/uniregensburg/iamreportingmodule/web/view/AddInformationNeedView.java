package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.core.service.InformationNeedService;
import de.uniregensburg.iamreportingmodule.data.entity.InformationNeed;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.InformationNeedForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;

/**
 * View for adding information need configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Add Information Need | IAM Reporting Modul")
@Route(value = "informationneeds/add", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AddInformationNeedView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final InformationNeedService informationNeedService;
    private final InformationNeedForm form = new InformationNeedForm();
    private final InformationNeed informationNeed = new InformationNeed();

    /**
     *
     * @param informationNeedService
     */
    public AddInformationNeedView(InformationNeedService informationNeedService) {
        this.informationNeedService = informationNeedService;
        addClassName("informationneed-add"); // CSS class name
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
        form.addListener(InformationNeedForm.SaveEvent.class, this::saveInformationNeed);
        form.addListener(InformationNeedForm.CloseEvent.class, this::close);

        form.setInformationNeed(informationNeed);

        form.showDelete(false); // hide delete button
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
     * Handles the close event: forwards to overview
     *
     * @param event
     */
    private void close(InformationNeedForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("informationneeds"));
    }
}
