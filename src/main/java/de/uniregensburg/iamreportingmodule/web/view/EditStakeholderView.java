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
import de.uniregensburg.iamreportingmodule.core.service.GroupService;
import de.uniregensburg.iamreportingmodule.core.service.UserService;
import de.uniregensburg.iamreportingmodule.data.entity.Stakeholder;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.GroupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;

/**
 * View for editing stakeholder configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Edit Stakeholder | IAM Reporting Modul")
@Route(value = "stakeholders/edit", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class EditStakeholderView extends VerticalLayout implements HasUrlParameter<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Stakeholder stakeholder = null;
    private final GroupForm form;
    private String id;

    private final GroupService groupService;

    /**
     *
     * @param groupService
     * @param userService
     */
    public EditStakeholderView(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        form = new GroupForm(userService);
        addClassName("stakeholder-edit"); // CSS class name
        setSizeFull(); // whole browser size
    }

    /**
     * Initializes view
     */
    private void init() {
        if (stakeholder == null) {
            logger.info("Stakeholder is null");
            add(new Text("Cannot find stakeholder with id " + id));
        } else {
            configureForm();
            add(form);
        }
    }

    /**
     * Configures form
     */
    private void configureForm() {
        form.addListener(GroupForm.SaveEvent.class, this::saveStakeholder);
        form.addListener(GroupForm.CloseEvent.class, this::close);
        form.addListener(GroupForm.DeleteEvent.class, this::deleteStakeholder);

        form.setGroup(stakeholder);
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
        logger.info("Searching stakeholder with id " + id);
        try {
            stakeholder = groupService.findStakeholderById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            logger.info("Error finding stakeholder");
            logger.info(e.getMessage());
        }
        init();
    }

    /**
     * Handles the save event: saves stakeholder configuration and forwards to overview
     *
     * @param event
     */
    private void saveStakeholder(GroupForm.SaveEvent event) {
        logger.info("Save stakeholder event");
        try {
            groupService.saveGroup(event.getGroup());
            new SuccessNotification("Success", "Stakeholder with name " + event.getGroup().getName() + " saved successfully." ).open();
            form.getUI().ifPresent(ui -> ui.navigate("stakeholders"));
        } catch (SaveEntityException e) {
            new ErrorNotification("Cannot save stakeholder", e.getMessage()).open();
        }
    }

    /**
     * Handles the delete event: shows confirmation dialog, deletes stakeholder configuration and forwards to overview
     *
     * @param event
     */
    private void deleteStakeholder(GroupForm.DeleteEvent event) {
        logger.info("Delete stakeholder event");
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete stakeholder");
        dialog.setText("Are you sure you want to delete stakeholder " + stakeholder.getName() + "?");
        dialog.setCancelable(true);
        dialog.addConfirmListener(e -> {
            try {
                groupService.deleteGroup(event.getGroup());
                new SuccessNotification("Success", "Stakeholder with name " + event.getGroup().getName() + " deleted successfully." ).open();
                form.getUI().ifPresent(ui -> ui.navigate("stakeholders"));
            } catch (DeleteEntityException ex) {
                new ErrorNotification("Cannot delete stakeholder", ex.getMessage()).open();
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
    private void close(GroupForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("stakeholders"));
    }
}
