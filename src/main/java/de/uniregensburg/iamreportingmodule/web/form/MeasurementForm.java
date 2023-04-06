package de.uniregensburg.iamreportingmodule.web.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.BeanValidator;
import com.vaadin.flow.shared.Registration;
import de.uniregensburg.iamreportingmodule.core.exception.DatabaseException;
import de.uniregensburg.iamreportingmodule.core.exception.FileException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.util.CsvUtil;
import de.uniregensburg.iamreportingmodule.core.util.DatabaseUtil;
import de.uniregensburg.iamreportingmodule.data.converter.StringToFrequencyConverter;
import de.uniregensburg.iamreportingmodule.data.entity.Unit;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import de.uniregensburg.iamreportingmodule.data.validator.MeasurableLabelValidator;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.view.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Form for editing a measurement
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/list/ContactForm.java
 *
 * @author Julian Bauer
 */
public class MeasurementForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Measurement measurement;
    private final Binder<Measurement> binder = new BeanValidationBinder<>(Measurement.class);
    private final MeasurableService service;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button test = new Button("Test");

    private final TextField name = new TextField("Name");
    private final TextField description = new TextField("Description");
    private final TextField label = new TextField("Label");
    private final MultiSelectComboBox<Stakeholder> stakeholders = new MultiSelectComboBox<>("Stakeholders");
    private final MultiSelectComboBox<Audience> audiences = new MultiSelectComboBox<>("Audiences");
    private final MultiSelectComboBox<InformationNeed> informationNeeds = new MultiSelectComboBox<>("Information Needs");
    private final ComboBox<Scale> scale = new ComboBox<>("Scale");
    private final ComboBox<Unit> unit = new ComboBox<>("Unit");
    private final TextField frequency = new TextField("Frequency");
    private final ComboBox<DataSource> dataSources = new ComboBox<>("Data Source");
    private final TextArea sqlQuery = new TextArea("SQL Query");
    private final Div db = new Div();
    private final TextField csvColumnName = new TextField("Column name");
    private final TextField csvColumnIndex = new TextField("Column index");
    private final TextField csvDelimiter = new TextField("Delimiter");
    private final Checkbox csvHeader = new Checkbox("Heading");
    private final FormLayout csv = new FormLayout();
    ComboBox<CsvAggregationMethod> csvAggregationMethod = new ComboBox<>("Aggregation method");

    /**
     *
     * @param scales
     * @param units
     * @param service
     */
    public MeasurementForm(List<Scale> scales, List<Unit> units, MeasurableService service) {
        this.service = service;
        init(scales, units);
    }

    /**
     * Initializes form
     * 
     * @param scales
     * @param units
     */
    private void init(List<Scale> scales, List<Unit> units) {
        addClassName("measurement-form");

        // configure form binders
        updateBinders(measurement);
        binder.bindInstanceFields(this);

        // configure form components
        scale.setItems(scales);
        unit.setItems(units);
        initStakeholders();
        initAudiences();
        initInformationNeeds();
        initDataSources();
        initCsv();

        // add database specific components
        Paragraph dbParagrapth = new Paragraph("Database specific attributes");
        sqlQuery.setWidthFull();
        db.add(dbParagrapth, sqlQuery);

        // add csv file specific components
        Paragraph csvParagrapth = new Paragraph("CSV specific attributes");
        csv.add(csvParagrapth, csvHeader, csvColumnName, csvColumnIndex, csvDelimiter, csvAggregationMethod);

        // add components to layout
        add(name, description, label, scale, unit, frequency, stakeholders, audiences, informationNeeds, dataSources, db, csv);
    }

    /**
     * Initializes csv components
     */
    private void initCsv() {
        csvHeader.addValueChangeListener(event -> {
            if (event.getValue()) {
                csvColumnName.setVisible(true);
                csvColumnIndex.setVisible(false);
                csvColumnIndex.clear();
            } else {
                csvColumnName.setVisible(false);
                csvColumnIndex.setVisible(true);
                csvColumnName.clear();
            }
        });
        csvHeader.setValue(false);
        csvColumnName.setVisible(false);
        csvAggregationMethod.setItems(List.of(CsvAggregationMethod.COUNT, CsvAggregationMethod.SUM, CsvAggregationMethod.AVERAGE, CsvAggregationMethod.MEDIAN, CsvAggregationMethod.MAXIMUM, CsvAggregationMethod.MINIMUM));
    }

    /**
     * Initializes data sources components
     */
    private void initDataSources() {
        setDbVisible(false);
        setCsvVisible(false);
        dataSources.setItems(service.findAllDataSources());
        dataSources.setItemLabelGenerator(DataSource::getName);
        dataSources.setPlaceholder("Select datasource");
        dataSources.addValueChangeListener(e -> {
            measurement.setDataSource(e.getValue());
            if (e.getValue() != null) {
                if (e.getOldValue() != null) {
                    if (e.getValue() == e.getOldValue()) {
                        return;
                    }
                }
                setDbVisible(false);
                setCsvVisible(false);
                DataSourceType type = e.getValue().getType();
                if (DataSourceType.DATABASE.equals(type)) { // Database
                    setDbVisible(true);
                }
                if (DataSourceType.FILE.equals(type)) { // File
                    FileDataSource fileDataSource = (FileDataSource) e.getValue();
                    if (FileType.CSV.equals(fileDataSource.getFileType())) { // CSV
                        setCsvVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Shows or hides database specific fields
     *
     * @param visible
     */
    private void setDbVisible(boolean visible) {
        db.setVisible(visible);
        if (!visible) {
            sqlQuery.clear();
        }
    }

    /**
     * Shows or hides csv file specific fields
     *
     * @param visible
     */
    private void setCsvVisible(boolean visible) {
        csv.setVisible(visible);
        if (!visible) {
            csvHeader.clear();
            csvDelimiter.clear();
            csvAggregationMethod.clear();
            csvColumnIndex.clear();
            csvColumnName.clear();
        }
    }

    /**
     * Initializes stakeholders field
     */
    private void initStakeholders() {
        stakeholders.setItems(service.findAllStakeholders());
        stakeholders.setItemLabelGenerator(Stakeholder::getName);
        stakeholders.setPlaceholder("Select stakeholders");
        stakeholders.addSelectionListener(e -> measurement.setStakeholders(e.getAllSelectedItems()));
    }

    /**
     * Initializes audiences fields
     */
    private void initAudiences() {
        audiences.setItems(service.findAllAudiences());
        audiences.setItemLabelGenerator(Audience::getName);
        audiences.setPlaceholder("Select audiences");
        audiences.addSelectionListener(e -> measurement.setAudiences(e.getAllSelectedItems()));
    }

    /**
     * Initializes information needs fields
     */
    private void initInformationNeeds() {
        informationNeeds.setItems(service.findAllInformationNeeds());
        informationNeeds.setItemLabelGenerator(InformationNeed::getName);
        informationNeeds.setPlaceholder("Select information needs");
        informationNeeds.addSelectionListener(e -> measurement.setInformationNeeds(e.getAllSelectedItems()));
    }

    /**
     * Initializes buttons in navbar
     *
     * @param navbarButtons
     */
    private void initNavbarButtons(HorizontalLayout navbarButtons) {
        navbarButtons.add(delete, cancel, test, save);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, measurement)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
        test.addClickListener(event -> testConfiguration(false));

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
     * Tests configuration based on form inputs
     *
     * @param silent hide notifications
     * @return
     */
    private boolean testConfiguration(boolean silent) {
        logger.info("Testing configuration");
        DataSource dataSource = dataSources.getValue();
        if (dataSource == null) {
            logger.info("No datasource selected");
            if (!silent) {
                new ErrorNotification("No datasource selected").open();
            }
            return false;
        }
        DataSourceType type = dataSource.getType();
        if (DataSourceType.MANUAL.equals(type)) {
            logger.info("Manual datasource");
            ManualDataSource manualDataSource = (ManualDataSource) dataSource;
            return testManualConfiguration(manualDataSource, silent);
        }
        if (DataSourceType.DATABASE.equals(type)) {
            logger.info("Database datasource");
            DatabaseDataSource databaseDataSource = (DatabaseDataSource) dataSource;
            return testDatabaseConfiguration(databaseDataSource, silent);
        }
        if (DataSourceType.FILE.equals(type)) {
            logger.info("File datasource");
            FileDataSource fileDataSource = (FileDataSource) dataSource;
            FileType fileType = fileDataSource.getFileType();
            if (FileType.CSV.equals(fileType)) {
                return testCsvConfiguration(fileDataSource, silent);
            }
        }
        return false;
    }

    /**
     * Tests csv configuration based on form inputs
     *
     * @param fileDataSource
     * @param silent hide notifications
     * @return
     */
    private boolean testCsvConfiguration(FileDataSource fileDataSource, boolean silent) {
        logger.info("Testing file datasource");
        HashMap<String, String> attributes = new HashMap<>();
        boolean csvHeaderAttribute = csvHeader.getValue();
        attributes.put("csvHeader", String.valueOf(csvHeaderAttribute));
        if (csvHeaderAttribute) {
            String csvColumnNameAttribute = csvColumnName.getValue();
            if (csvColumnNameAttribute.isBlank()) {
                logger.info("Column name is blank");
                if (!silent) {
                    new ErrorNotification("Column name is blank").open();
                }
                return false;
            }
            attributes.put("csvColumnName", csvColumnNameAttribute);
        } else {
            String csvColumnIndexAttribute = csvColumnIndex.getValue();
            if (csvColumnIndexAttribute.isBlank()) {
                logger.info("Column index is blank");
                if (!silent) {
                    new ErrorNotification("Column index is blank").open();
                }
                return false;
            }
            attributes.put("csvColumnIndex", csvColumnIndexAttribute);
        }
        String csvDelimiterAttribute = csvDelimiter.getValue();
        if (csvDelimiterAttribute.isBlank()) {
            logger.info("Delimiter is blank");
            if (!silent) {
                new ErrorNotification("Delimiter is blank").open();
            }
            return false;
        }
        attributes.put("csvDelimiter", csvDelimiterAttribute);
        if (csvAggregationMethod.getValue() == null) {
            logger.info("No aggregation method selected");
            if (!silent) {
                new ErrorNotification("No aggregation method selected").open();
            }
            return false;
        }
        String csvAggregationMethodAttribute = csvAggregationMethod.getValue().name();
        attributes.put("csvAggregationMethod", csvAggregationMethodAttribute);
        CsvUtil csvUtil = new CsvUtil(fileDataSource);
        try {
            Result result = csvUtil.measure(attributes);
            logger.info("Measurement successful: " + result.getValue());
            if (!silent) {
                new SuccessNotification("Measurement successful: " + result.getValue().stripTrailingZeros().toPlainString()).open();
            }
            return true;
        } catch (FileException e) {
            logger.info("Measurement failed: " + e.getMessage());
            if (!silent) {
                new ErrorNotification("Measurement failed: " + e.getMessage()).open();
            }
            return false;
        }
    }

    /**
     * Tests database configuration based on form inputs
     *
     * @param databaseDataSource
     * @param silent hide notifications
     * @return
     */
    private boolean testDatabaseConfiguration(DatabaseDataSource databaseDataSource, boolean silent) {
        logger.info("Testing database datasource");
        if (sqlQuery.getValue().isBlank()) {
            logger.info("Query is blank");
            if (!silent) {
                new ErrorNotification("Query is blank").open();
            }
            return false;
        }
        DatabaseUtil databaseUtil = new DatabaseUtil(databaseDataSource);
        try {
            Result result = databaseUtil.measure(sqlQuery.getValue());
            logger.info("Query successful: " + result.getValue());
            if (!silent) {
                new SuccessNotification("Query successful: " + result.getValue()).open();
            }
            return true;
        } catch (DatabaseException e) {
            logger.info("Query failed: " + e.getMessage());
            if (!silent) {
                new ErrorNotification("Query failed: " + e.getMessage()).open();
            }
            return false;
        }
    }

    /**
     * Tests manual datasource configuration based on form inputs
     * @param manualDataSource
     * @param silent hide notifications
     * @return
     */
    private boolean testManualConfiguration(ManualDataSource manualDataSource, boolean silent) {
        logger.info("Testing manual datasource");
        BigDecimal value = manualDataSource.getValue();
        if (value == null) {
            if (!silent) {
                new ErrorNotification("Manual value null").open();
            }
            logger.info("Manual value null");
            return false;
        }
        if (!silent) {
            new SuccessNotification("Manual value: " + value).open();
        }
        logger.info("Success - Manual value: " + value);
        return true;
    }

    /**
     * Validates and saves group
     */
    private void validateAndSave() {
        if (binder.validate().hasErrors()) {
            new ErrorNotification("Form contains errors").open();
        } else if (testConfiguration(false)) {
            try {
                binder.writeBean(measurement);
                updateAttributes();
                fireEvent(new SaveEvent(this, measurement));
            } catch (ValidationException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Updates attributes
     * Automatic binding with form binder not possible for attributes as they are optional
     */
    private void updateAttributes() {
        logger.info("Adding attributes");
        Map<String, String> attributes = measurement.getAttributes();
        DataSource dataSource = measurement.getDataSource();
        if (dataSource == null) {
            logger.info("Datasource is null");
            return;
        }
        DataSourceType type = dataSource.getType();
        if (type == null) {
            logger.info("Datasource type is null");
            return;
        }
        if (DataSourceType.DATABASE.equals(type)) {
            attributes.put("sqlQuery", sqlQuery.getValue());
        }
        if (DataSourceType.FILE.equals(type)) {
            FileDataSource fileDataSource = (FileDataSource) dataSource;
            FileType fileType = fileDataSource.getFileType();
            if (FileType.CSV.equals(fileType)) {
                boolean heading = csvHeader.getValue();
                attributes.put("csvHeader", String.valueOf(heading));
                if (heading) {
                    attributes.put("csvColumnName", csvColumnName.getValue());
                } else {
                    attributes.put("csvColumnIndex", csvColumnIndex.getValue());
                }
                attributes.put("csvDelimiter", csvDelimiter.getValue());
                if (csvAggregationMethod.getValue() != null) {
                    attributes.put("csvAggregationMethod", csvAggregationMethod.getValue().name());
                }
            }
        }
        measurement.setAttributes(attributes);
    }

    /**
     * Fills form based on measurement
     *
     * @param measurement
     */
    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
        updateBinders(measurement);
        binder.readBean(measurement);
        stakeholders.select(measurement.getStakeholders());
        audiences.select(measurement.getAudiences());
        informationNeeds.select(measurement.getInformationNeeds());
        dataSources.setValue(measurement.getDataSource());
        Map<String, String> attributes = measurement.getAttributes();
        String sqlQueryAttribute = attributes.get("sqlQuery");
        if (sqlQueryAttribute != null) {
            sqlQuery.setValue(sqlQueryAttribute);
        }
        String csvHeaderAttribute = attributes.get("csvHeader");
        if (csvHeaderAttribute != null) {
            boolean header = Boolean.parseBoolean(csvHeaderAttribute);
            csvHeader.setValue(header);
            if (header) {
                csvColumnName.setVisible(true);
                csvColumnIndex.setVisible(false);
            } else {
                csvColumnName.setVisible(false);
                csvColumnIndex.setVisible(true);
            }
        }
        String csvColumnNameAttribute = attributes.get("csvColumnName");
        if (csvColumnNameAttribute != null) {
            csvColumnName.setValue(csvColumnNameAttribute);
        }
        String csvColumnIndexAttribute = attributes.get("csvColumnIndex");
        if (csvColumnIndexAttribute != null) {
            csvColumnIndex.setValue(csvColumnIndexAttribute);
        }
        String csvDelimiterAttribute = attributes.get("csvDelimiter");
        if (csvDelimiterAttribute != null) {
            csvDelimiter.setValue(csvDelimiterAttribute);
        }
        String csvAggregationMethodAttribute = attributes.get("csvAggregationMethod");
        if (csvAggregationMethodAttribute != null) {
            csvAggregationMethod.setValue(CsvAggregationMethod.valueOf(csvAggregationMethodAttribute));
        }
    }

    /**
     * Updates form binders
     *
     * @param measurement
     */
    private void updateBinders(Measurement measurement) {
        binder.forField(label)
                .withValidator(new MeasurableLabelValidator(service, measurement))
                .withValidator(new BeanValidator(Measurement.class, "label"))
                .bind(Measurement::getLabel,Measurement::setLabel);
        binder.forField(frequency)
                .withConverter(
                        new StringToFrequencyConverter("Not a duration", measurement))
                .bind(Measurement::getFrequency,Measurement::setFrequency);
    }

    /**
     * Event definition
     */
    public static abstract class MeasurementFormEvent extends ComponentEvent<MeasurementForm> {
        private final Measurement measurement;

        public MeasurementFormEvent(MeasurementForm source, Measurement measurement) {
            super(source, false);
            this.measurement = measurement;
        }

        public Measurement getMeasurement() {
            return measurement;
        }
    }

    /**
     * Save event
     */
    public static class SaveEvent extends MeasurementFormEvent {
        SaveEvent(MeasurementForm source, Measurement measurement) {
            super(source, measurement);
        }
    }

    /**
     * Delete event
     */
    public static class DeleteEvent extends MeasurementFormEvent {
        DeleteEvent(MeasurementForm source, Measurement measurement) {
            super(source, measurement);
        }
    }

    /**
     * Close event
     */
    public static class CloseEvent extends MeasurementFormEvent {
        CloseEvent(MeasurementForm source) {
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
