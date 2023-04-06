package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.core.exception.SaveEntityException;
import de.uniregensburg.iamreportingmodule.core.service.GroupService;
import de.uniregensburg.iamreportingmodule.core.service.UserService;
import de.uniregensburg.iamreportingmodule.data.entity.Audience;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.form.GroupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;

/**
 * View for adding audience configuration
 *
 * @author Julian Bauer
 */
@PageTitle("Add Audience | IAM Reporting Modul")
@Route(value = "audiences/add", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AddAudienceView extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final GroupService groupService;
    private final UserService userService;
    private GroupForm form;
    private final Audience audience = new Audience();

    /**
     *
     * @param groupService
     * @param userService
     */
    public AddAudienceView(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
        addClassName("audience-add"); // CSS class name
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
        form = new GroupForm(userService);

        form.addListener(GroupForm.SaveEvent.class, this::saveAudience);
        form.addListener(GroupForm.CloseEvent.class, this::close);

        form.setGroup(audience);

        form.showDelete(false); // hide delete button
    }

    /**
     * Handles the save event: saves audience configuration and forwards to overview
     *
     * @param event
     */
    private void saveAudience(GroupForm.SaveEvent event) {
        logger.info("Save audience event");
        try {
            groupService.saveGroup(event.getGroup());
            new SuccessNotification("Success", "Audience with name " + event.getGroup().getName() + " saved successfully." ).open();
            form.getUI().ifPresent(ui -> ui.navigate("audiences"));
        } catch (SaveEntityException e) {
            new ErrorNotification("Cannot save audience", e.getMessage()).open();
        }
    }

    /**
     * Handles the close event: forwards to overview
     *
     * @param event
     */
    private void close(GroupForm.CloseEvent event) {
        logger.info("Close event");
        form.getUI().ifPresent(ui -> ui.navigate("audiences"));
    }
}
