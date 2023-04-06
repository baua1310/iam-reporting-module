package de.uniregensburg.iamreportingmodule.web.component.notification;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Component for displaying error notifications (extends custom notification)
 *
 * @author Julian Bauer
 */
@Tag("error-notification")
public class ErrorNotification extends CustomNotification {

    /**
     *
     */
    public ErrorNotification() {
        this("", "");
    }

    /**
     *
     * @param message
     */
    public ErrorNotification(String message) {
        this("Error", message);
    }

    /**
     *
     * @param header
     * @param message
     */
    public ErrorNotification(String header, String message) {
        super(header, message);
        init();
    }

    /**
     * Initializes component
     */
    public void init() {
        getNotification().addThemeVariants(NotificationVariant.LUMO_ERROR); // color: red
    }
}
