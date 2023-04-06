package de.uniregensburg.iamreportingmodule.web.form;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.validator.BeanValidator;
import com.vaadin.flow.shared.Registration;
import de.uniregensburg.iamreportingmodule.core.exception.FormulaException;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.util.FormulaUtil;
import de.uniregensburg.iamreportingmodule.data.converter.StringToFormulaConverter;
import de.uniregensburg.iamreportingmodule.data.converter.StringToFrequencyConverter;
import de.uniregensburg.iamreportingmodule.data.entity.Unit;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import de.uniregensburg.iamreportingmodule.data.validator.FormulaValidator;
import de.uniregensburg.iamreportingmodule.data.validator.MeasurableLabelValidator;
import de.uniregensburg.iamreportingmodule.web.component.notification.ErrorNotification;
import de.uniregensburg.iamreportingmodule.web.component.notification.SuccessNotification;
import de.uniregensburg.iamreportingmodule.web.view.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Form for editing a metric
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/list/ContactForm.java
 *
 * @author Julian Bauer
 */public class MetricForm extends FormLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Metric metric;
    private final Binder<Metric> binder = new BeanValidationBinder<>(Metric.class);
    private final MeasurableService service;
    private final FormulaUtil util;

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");
    private final Button test = new Button("Test formula");

    private final TextField name = new TextField("Name");
    private final TextField description = new TextField("Description");
    private final TextField label = new TextField("label");
    private final TextArea formula = new TextArea("Formula");
    private final TextField targetValue = new TextField("Target value");
    private final MultiSelectComboBox<Stakeholder> stakeholders = new MultiSelectComboBox<>("Stakeholders");
    private final MultiSelectComboBox<Audience> audiences = new MultiSelectComboBox<>("Audiences");
    private final MultiSelectComboBox<InformationNeed> informationNeeds = new MultiSelectComboBox<>("Information Needs");
    private final ComboBox<Scale> scale = new ComboBox<>("Scale");
    private final ComboBox<Unit> unit = new ComboBox<>("Unit");
    private final TextField frequency = new TextField("Frequency");

    /**
     *
     * @param scales
     * @param units
     * @param service
     */
    public MetricForm(List<Scale> scales, List<Unit> units, MeasurableService service) {
        this.service = service;
        this.util = new FormulaUtil(service);
        init(scales, units);
    }

    /**
     * Initializes form
     *
     * @param scales
     * @param units
     */
    private void init(List<Scale> scales, List<Unit> units) {
        addClassName("metric-form");

        // configure form binders
        updateBinders(metric);
        binder.forField(targetValue)
                .withConverter(
                        new StringToBigDecimalConverter("Not a number"))
                .bind(Metric::getTargetValue,Metric::setTargetValue);
        binder.bindInstanceFields(this);

        // configure form components
        scale.setItems(scales);
        unit.setItems(units);
        initStakeholders();
        initAudiences();
        initInformationNeeds();

        // add components to layout
        add(name, description, label, formula, targetValue, scale, unit, frequency, stakeholders, audiences, informationNeeds);
    }

    /**
     * Initializes stakeholders field
     */
    private void initStakeholders() {
        stakeholders.setItems(service.findAllStakeholders());
        stakeholders.setItemLabelGenerator(Stakeholder::getName);
        stakeholders.setPlaceholder("Select stakeholders");
        stakeholders.addSelectionListener(e -> metric.setStakeholders(e.getAllSelectedItems()));
    }

    /**
     * Initializes audiences fields
     */
    private void initAudiences() {
        audiences.setItems(service.findAllAudiences());
        audiences.setItemLabelGenerator(Audience::getName);
        audiences.setPlaceholder("Select audiences");
        audiences.addSelectionListener(e -> metric.setAudiences(e.getAllSelectedItems()));
    }

    /**
     * Initializes information needs fields
     */
    private void initInformationNeeds() {
        informationNeeds.setItems(service.findAllInformationNeeds());
        informationNeeds.setItemLabelGenerator(InformationNeed::getName);
        informationNeeds.setPlaceholder("Select information needs");
        informationNeeds.addSelectionListener(e -> metric.setInformationNeeds(e.getAllSelectedItems()));
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
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, metric)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
        test.addClickListener(event -> testFormula(false));

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
     * Tests formula based on form inputs
     *
     * @param silent hide notifications
     * @return
     */
    private boolean testFormula(boolean silent) {
        logger.info("Testing formula");
        String formulaString = formula.getValue();
        try {
            Result result = util.calculate(formulaString);
            new SuccessNotification("Calculation result: " + result.getValue().stripTrailingZeros().toPlainString()).open();
            return true;
        } catch (FormulaException e) {
            logger.info("Formula calculation failed: " + e.getMessage());
            if (!silent) {
                new ErrorNotification("Formula calculation failed: " + e.getMessage()).open();
            }
            return false;
        }
    }

    /**
     * Validates and saves group
     */
    private void validateAndSave() {
        if (binder.validate().hasErrors()) {
            new ErrorNotification("Form contains errors").open();
        } else if (testFormula(false)) {
            try {
                binder.writeBean(metric);
                fireEvent(new SaveEvent(this, metric));
            } catch (ValidationException e) {
                logger.info(e.getMessage());
            }
        }
    }

    /**
     * Fills form based on metric
     *
     * @param metric
     */
    public void setMetric(Metric metric) {
        this.metric = metric;
        updateBinders(metric);
        binder.readBean(metric);
        stakeholders.select(metric.getStakeholders());
        audiences.select(metric.getAudiences());
        informationNeeds.select(metric.getInformationNeeds());
    }


    /**
     * Updates form binders
     *
     * @param metric
     */
    private void updateBinders(Metric metric) {
        binder.forField(label)
                .withValidator(new MeasurableLabelValidator(service, metric))
                .withValidator(new BeanValidator(Metric.class, "label"))
                .bind(Metric::getLabel,Metric::setLabel);

        binder.forField(formula)
                .withNullRepresentation("")
                .withValidator(new FormulaValidator(service))
                .withValidator(new BeanValidator(Metric.class, "formula"))
                .withConverter(
                        new StringToFormulaConverter("Invalid formula", service, metric))
                .bind(Metric::getFormula,Metric::setFormula);
        binder.forField(frequency)
                .withConverter(
                        new StringToFrequencyConverter("Not a duration", metric))
                .bind(Metric::getFrequency,Metric::setFrequency);
    }

    /**
     * Event definition
     */
    public static abstract class MetricFormEvent extends ComponentEvent<MetricForm> {
        private final Metric metric;

        public MetricFormEvent(MetricForm source, Metric metric) {
            super(source, false);
            this.metric = metric;
        }

        public Metric getMetric() {
            return metric;
        }
    }

    /**
     * Save event
     */
    public static class SaveEvent extends MetricFormEvent {
        SaveEvent(MetricForm source, Metric metric) {
            super(source, metric);
        }
    }

    /**
     * Delete event
     */
    public static class DeleteEvent extends MetricFormEvent {
        DeleteEvent(MetricForm source, Metric metric) {
            super(source, metric);
        }
    }

    /**
     * Close event
     */
    public static class CloseEvent extends MetricFormEvent {
        CloseEvent(MetricForm source) {
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
