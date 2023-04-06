package de.uniregensburg.iamreportingmodule.web.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import de.uniregensburg.iamreportingmodule.core.service.UserService;
import de.uniregensburg.iamreportingmodule.data.entity.Group;
import de.uniregensburg.iamreportingmodule.data.entity.User;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.view.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Form for editing a group
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/list/ContactForm.java
 *
 * @author Julian Bauer
 */
public class GroupForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Group group;
    private final Binder<Group> binder = new BeanValidationBinder<>(Group.class);
    private final UserService userService;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private final TextField name = new TextField("Name");
    private final MultiSelectComboBox<User> members = new MultiSelectComboBox<>("Members");

    /**
     * 
     * @param userService
     */
    public GroupForm(UserService userService) {
        this.userService = userService;
        init();
    }

    /**
     * Initializes form
     */
    private void init() {
        addClassName("group-form");

        // configure form binders
        binder.bindInstanceFields(this);

        // configure form components
        initMembers();

        // add components to layout
        add(name, members);
    }

    /**
     * Initializes multiselect combo box with users
     */
    private void initMembers() {
        members.setItems(userService.findAllUsers());
        members.setItemLabelGenerator(User::getFullName);
        members.setPlaceholder("Select members");
        members.addSelectionListener(e -> group.setMembers(e.getAllSelectedItems()));
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(delete, cancel, save);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, group)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
    }

    /**
     * Shows or hides delete button
     *
     * @param show
     */
    public void showDelete(boolean show) {
        delete.setVisible(show);
    }

    /**
     * Validates and saves group
     */
    private void validateAndSave() {
        if (binder.validate().hasErrors()) {
            new ErrorNotification("Form contains errors").open();
        } else {
            try {
                binder.writeBean(group);
                fireEvent(new SaveEvent(this, group));
            } catch (ValidationException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Fills form based on group
     *
     * @param group
     */
    public void setGroup(Group group) {
        this.group = group;
        binder.readBean(group);
        members.select(group.getMembers());
    }

    /**
     * Event definition
     */
    public static abstract class GroupFormEvent extends ComponentEvent<GroupForm> {
        private final Group group;

        public GroupFormEvent(GroupForm source, Group group) {
            super(source, false);
            this.group = group;
        }

        public Group getGroup() {
            return group;
        }
    }

    /**
     * Save event
     */
    public static class SaveEvent extends GroupFormEvent {
        SaveEvent(GroupForm source, Group group) {
            super(source, group);
        }
    }

    /**
     * Delete event
     */
    public static class DeleteEvent extends GroupFormEvent {
        DeleteEvent(GroupForm source, Group group) {
            super(source, group);
        }
    }

    /**
     * Close event
     */
    public static class CloseEvent extends GroupFormEvent {
        CloseEvent(GroupForm source) {
            super(source, null);
        }
    }

    /**
     * Event listener
     *
     * @param eventType
     * @param listener
     * @return
     * @param <T>
     */
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
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
