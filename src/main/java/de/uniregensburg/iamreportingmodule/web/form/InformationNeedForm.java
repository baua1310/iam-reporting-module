package de.uniregensburg.iamreportingmodule.web.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import de.uniregensburg.iamreportingmodule.data.entity.InformationNeed;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.view.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Form for editing information need
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/list/ContactForm.java
 *
 * @author Julian Bauer
 */
public class InformationNeedForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private InformationNeed informationNeed;
    private final Binder<InformationNeed> binder = new BeanValidationBinder<>(InformationNeed.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private final TextField name = new TextField("Name");
    private final TextField description = new TextField("Description");

    /**
     *
     */
    public InformationNeedForm() {
        init();
    }

    /**
     * Initializes form
     */
    private void init() {
        addClassName("informationneed-form");

        // configure form binders
        binder.bindInstanceFields(this);

        // add components to layout
        add(name, description);
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
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, informationNeed)));
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
     * Validates and saves information need
     */
    private void validateAndSave() {
        if (binder.validate().hasErrors()) {
            new ErrorNotification("Form contains errors").open();
        } else {
            try {
                binder.writeBean(informationNeed);
                fireEvent(new SaveEvent(this, informationNeed));
            } catch (ValidationException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Fills form based on information need
     *
     * @param informationNeed
     */
    public void setInformationNeed(InformationNeed informationNeed) {
        this.informationNeed = informationNeed;
        binder.readBean(informationNeed);
    }

    /**
     * Event definition
     */
    public static abstract class InformationNeedFormEvent extends ComponentEvent<InformationNeedForm> {
        private final InformationNeed informationNeed;

        public InformationNeedFormEvent(InformationNeedForm source, InformationNeed informationNeed) {
            super(source, false);
            this.informationNeed = informationNeed;
        }

        public InformationNeed getInformationNeed() {
            return informationNeed;
        }
    }

    /**
     * Save event
     */
    public static class SaveEvent extends InformationNeedFormEvent {
        SaveEvent(InformationNeedForm source, InformationNeed informationNeed) {
            super(source, informationNeed);
        }
    }

    /**
     * Delete event
     */
    public static class DeleteEvent extends InformationNeedFormEvent {
        DeleteEvent(InformationNeedForm source, InformationNeed informationNeed) {
            super(source, informationNeed);
        }
    }

    /**
     * Close event
     */
    public static class CloseEvent extends InformationNeedFormEvent {
        CloseEvent(InformationNeedForm source) {
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
