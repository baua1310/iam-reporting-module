package de.uniregensburg.iamreportingmodule.web.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import de.uniregensburg.iamreportingmodule.core.exception.DatabaseException;
import de.uniregensburg.iamreportingmodule.core.util.DatabaseUtil;
import de.uniregensburg.iamreportingmodule.data.converter.StringToPortConverter;
import de.uniregensburg.iamreportingmodule.data.entity.DatabaseDataSource;
import de.uniregensburg.iamreportingmodule.data.entity.Dbms;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.view.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Form for editing database datasource
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/list/ContactForm.java
 *
 * @author Julian Bauer
 */
public class DatabaseDataSourceForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseDataSource dataSource;
    private final Binder<DatabaseDataSource> binder = new BeanValidationBinder<>(DatabaseDataSource.class);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button testConnection = new Button("Test connection");

    private final TextField name = new TextField("Name");
    private final TextField description = new TextField("Description");
    private final ComboBox<Dbms> dbmsType = new ComboBox<>("Database management system");
    private final TextField host = new TextField("Host");
    private final TextField port = new TextField("Port");
    private final TextField database = new TextField("Database");
    private final TextField username = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");

    /**
     *
     * @param dbmsTypeList
     */
    public DatabaseDataSourceForm(List<Dbms> dbmsTypeList) {
        init(dbmsTypeList);
    }

    /**
     * Initializes form
     *
     * @param dbmsTypeList list of dbms types
     */
    private void init(List<Dbms> dbmsTypeList) {
        addClassName("datasource-form");

        // configure form binders
        binder.forField(port)
                .withConverter(
                        new StringToPortConverter("Not in range from 1 to 65535"))
                .bind(DatabaseDataSource::getPort,DatabaseDataSource::setPort);
        binder.bindInstanceFields(this);

        // configure form components
        configureComboBox(dbmsTypeList);

        // add components to layout
        add(name, description, dbmsType, host, port, database, username, password);
    }

    /**
     * Tests connection based on form inputs
     *
     * @param silent hide notifications
     * @return
     */
    private boolean testConnection(boolean silent) {
        boolean invalid = dbmsType.isInvalid() || dbmsType.isEmpty() || host.isInvalid() || host.isEmpty() || port.isInvalid() || port.isEmpty() || database.isInvalid() || database.isEmpty() || username.isInvalid() || password.isInvalid();
        if (invalid) {
            if (dbmsType.isInvalid() || dbmsType.isEmpty()) {
                if (!silent) {
                    new ErrorNotification("Database management system invalid").open();
                }
            }
            if (host.isInvalid() || host.isEmpty()) {
                if (!silent) {
                    new ErrorNotification("Host invalid").open();
                }
            }
            if (port.isInvalid() || port.isEmpty()) {
                if (!silent) {
                    new ErrorNotification("Port invalid").open();
                }
            }
            if (database.isInvalid() || database.isEmpty()) {
                if (!silent) {
                    new ErrorNotification("Database invalid").open();
                }
            }
            if (username.isInvalid()) {
                if (!silent) {
                    new ErrorNotification("Username invalid").open();
                }
            }
            if (password.isInvalid()) {
                if (!silent) {
                    new ErrorNotification("Password invalid").open();
                }
            }
            return false;
        } else { // valid
            DatabaseDataSource testDataSource = new DatabaseDataSource(Dbms.POSTGRESQL);
            testDataSource.setHost(host.getValue());
            testDataSource.setPort(Integer.parseInt(port.getValue()));
            testDataSource.setDatabase(database.getValue());
            testDataSource.setUsername(username.getValue());
            testDataSource.setPassword(password.getValue());
            DatabaseUtil databaseUtil = new DatabaseUtil(testDataSource);
            try {
                if (databaseUtil.testConnection()) {
                    logger.info("Connection successful");
                    if (!silent) {
                        new SuccessNotification("Connection successful").open();
                    }
                    return true;
                } else {
                    logger.info("Connection failed");
                    if (!silent) {
                        new ErrorNotification("Connection failed").open();
                    }
                    return false;
                }
            } catch (DatabaseException ex) {
                logger.info("Connection failed " + ex.getMessage());
                if (!silent) {
                    new ErrorNotification("Connection failed").open();
                }
                return false;
            }
        }
    }

    /**
     * Configures combo box with dbms types
     *
     * @param dbmsTypeList
     */
    private void configureComboBox(List<Dbms> dbmsTypeList) {
        dbmsType.setItems(dbmsTypeList);
        dbmsType.setPlaceholder("Select database management system");
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(delete, cancel, testConnection, save);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, dataSource)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
        testConnection.addClickListener(event -> testConnection(false));

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
     * Validates and saves database datasource
     */
    private void validateAndSave() {
        if (binder.validate().hasErrors()) {
            new ErrorNotification("Form contains errors").open();
        } else if (testConnection(false)) {
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
    public void setDataSource(DatabaseDataSource dataSource) {
        this.dataSource = dataSource;
        binder.readBean(dataSource);
    }

    /**
     * Event definition
     */
    public static abstract class DatabaseDataSourceFormEvent extends ComponentEvent<DatabaseDataSourceForm> {
        private final DatabaseDataSource dataSource;

        public DatabaseDataSourceFormEvent(DatabaseDataSourceForm source, DatabaseDataSource dataSource) {
            super(source, false);
            this.dataSource = dataSource;
        }

        public DatabaseDataSource getDataSource() {
            return dataSource;
        }
    }

    /**
     * Save event
     */
    public static class SaveEvent extends DatabaseDataSourceFormEvent {
        SaveEvent(DatabaseDataSourceForm source, DatabaseDataSource dataSource) {
            super(source, dataSource);
        }
    }

    /**
     * Delete event
     */
    public static class DeleteEvent extends DatabaseDataSourceFormEvent {
        DeleteEvent(DatabaseDataSourceForm source, DatabaseDataSource dataSource) {
            super(source, dataSource);
        }
    }

    /**
     * Close event
     */
    public static class CloseEvent extends DatabaseDataSourceFormEvent {
        CloseEvent(DatabaseDataSourceForm source) {
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
