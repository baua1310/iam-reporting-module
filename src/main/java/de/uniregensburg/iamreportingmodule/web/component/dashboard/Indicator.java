package de.uniregensburg.iamreportingmodule.web.component.dashboard;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;

/**
 * Component for displaying an indicator on a card.
 * Information displayed: title, value, unit
 * Card can include href link
 *
 * @author Julian Bauer
 */
@CssImport("./themes/iamreportingmodule/indicator.css")
public class Indicator extends Composite<Div> implements HasStyle, HasSize {

    private final Text title = new Text("Unknown");
    private final Text value = new Text("0");
    private final Text unit = new Text("");
    private final Anchor anchor = new Anchor();

    /**
     *
     * @param title
     * @param value
     * @param unit
     */
    public Indicator(String title, String value, String unit) {
        this.title.setText(title);
        this.value.setText(value);
        this.unit.setText(unit);
        init();
    }

    /**
     *
     * @param title
     * @param value
     */
    public Indicator(String title, String value) {
        this.title.setText(title);
        this.value.setText(value);

        init();
    }

    /**
     *
     * @param title
     */
    public Indicator(String title) {
        this.title.setText(title);

        init();
    }

    /**
     *
     */
    public Indicator() {
        init();
    }

    /**
     * Initializes component
     */
    private void init() {
        Div titleDiv = new Div(title);
        titleDiv.setClassName("indicator-title");

        Div valueDiv = new Div(value);
        valueDiv.setClassName("indicator-value");

        Div unitDiv = new Div(unit);
        unitDiv.setClassName("indicator-unit");

        anchor.addClassName("indicator-link");

        getContent().add(titleDiv, valueDiv, unitDiv, anchor);
        addClassName("indicator");
    }

    /**
     * Returns title
     *
     * @return
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * Sets title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * Returns value
     *
     * @return
     */
    public String getValue() {
        return value.getText();
    }

    /**
     * Sets value
     *
     * @param value
     */
    public void setValue(String value) {
        this.value.setText(value);
    }

    /**
     * Returns unit
     *
     * @return
     */
    public String getUnit() {
        return unit.getText();
    }

    /**
     * Sets unit
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit.setText(unit);
    }

    /**
     * Returns href link
     *
     * @return
     */
    public String getLink() {
        return anchor.getHref();
    }

    /**
     * Sets href link
     *
     * @param href
     */
    public void setLink(String href) {
        anchor.setHref(href);
    }
}
