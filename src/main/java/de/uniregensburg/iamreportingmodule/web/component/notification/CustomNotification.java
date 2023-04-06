package de.uniregensburg.iamreportingmodule.web.component.notification;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Component for displaying customized notifications
 * Composed of a header, a message and a close button
 * The notification is shown in the bottom right corner for five seconds.
 *
 * @author Julian Bauer
 */
@Tag("notification")
public class CustomNotification extends Component {

    private final Text headerText;
    private final Text messageText;
    private final Notification notification = new Notification();

    /**
     *
     */
    public CustomNotification() {
        this("", "");
    }

    /**
     *
     * @param message
     */
    public CustomNotification(String message) {
        this("", message);
    }

    /**
     *
     * @param header
     * @param message
     */
    public CustomNotification(String header, String message) {
        headerText =  new Text(header);
        messageText = new Text(message);
        init();
    }

    /**
     * Initializes component
     */
    private void init() {
        Div headerDiv = new Div(headerText); // add header
        headerDiv.getStyle().set("font-weight", "600"); // bold
        Div messageDiv = new Div(messageText); // add message
        messageDiv.getStyle().set("font-size", "var(--lumo-font-size-s)"); // text size
        Button closeButton = new Button(new Icon("lumo", "cross")); // add button
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE); // style: tertiary inline
        closeButton.getElement().setAttribute("aria-label", "Close"); // label
        closeButton.addClickListener(event -> notification.close()); // listener -> close
        HorizontalLayout layout = new HorizontalLayout(new Div(headerDiv, messageDiv), closeButton); // layout
        layout.setAlignItems(FlexComponent.Alignment.CENTER); // center items
        notification.setPosition(Notification.Position.BOTTOM_END); // show in button right corner
        notification.setDuration(5000); // show for five seconds
        notification.add(layout); // add layout to notification
    }

    /**
     * Shows notification
     */
    public void open() {
        notification.open();
    }

    /**
     * Closes notification
     */
    public void close() {
        notification.close();
    }

    /**
     * Returns message
     *
     * @return
     */
    public String getMessage() {
        return messageText.getText();
    }

    /**
     * Sets message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageText.setText(message);
    }

    /**
     * Returns header
     *
     * @return
     */
    public String getHeader() {
        return headerText.getText();
    }

    /**
     * Sets header
     *
     * @param header
     */
    public void setHeader(String header) {
        headerText.setText(header);
    }

    /**
     * Returns notification component
     *
     * @return
     */
    public Notification getNotification() {
        return notification;
    }
}
