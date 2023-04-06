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
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.shared.Registration;
import de.uniregensburg.iamreportingmodule.data.entity.ManualDataSource;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.view.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Form for editing manual datasource
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/list/ContactForm.java
 *
 * @author Julian Bauer
 */
public class ManualDataSourceForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ManualDataSource dataSource;
    private final Binder<ManualDataSource> binder = new BeanValidationBinder<>(ManualDataSource.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private final TextField name = new TextField("Name");
    private final TextField description = new TextField("Description");
    private final TextField value = new TextField("Value");

    /**
     *
     */
    public ManualDataSourceForm() {
        init();
    }

    /**
     * Initializes form
     */
    private void init() {
        addClassName("datasource-form");

        // configure form binders
        binder.forField(value)
                .withConverter(
                        new StringToBigDecimalConverter("Not a number"))
                .bind(ManualDataSource::getValue,ManualDataSource::setValue);
        binder.bindInstanceFields(this);

        // add components to layout
        add(name, description, value);
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
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, dataSource)));
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
     * Validates and saves manual datasource
     */
    private void validateAndSave() {
        if (binder.validate().hasErrors()) {
            new ErrorNotification("Form contains errors").open();
        } else {
            try {
                binder.writeBean(dataSource);
                fireEvent(new SaveEvent(this, dataSource));
            } catch (ValidationException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Fills form based on datasource
     *
     * @param dataSource
     */
    public void setDataSource(ManualDataSource dataSource) {
        this.dataSource = dataSource;
        binder.readBean(dataSource);
    }

    /**
     * Event definition
     */
    public static abstract class ManualDataSourceFormEvent extends ComponentEvent<ManualDataSourceForm> {
        private final ManualDataSource dataSource;

        public ManualDataSourceFormEvent(ManualDataSourceForm source, ManualDataSource dataSource) {
            super(source, false);
            this.dataSource = dataSource;
        }

        public ManualDataSource getDataSource() {
            return dataSource;
        }
    }

    /**
     * Save event
     */
    public static class SaveEvent extends ManualDataSourceFormEvent {
        SaveEvent(ManualDataSourceForm source, ManualDataSource dataSource) {
            super(source, dataSource);
        }
    }

    /**
     * Delete event
     */
    public static class DeleteEvent extends ManualDataSourceFormEvent {
        DeleteEvent(ManualDataSourceForm source, ManualDataSource dataSource) {
            super(source, dataSource);
        }
    }

    /**
     * Close event
     */
    public static class CloseEvent extends ManualDataSourceFormEvent {
        CloseEvent(ManualDataSourceForm source) {
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
