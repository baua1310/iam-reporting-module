package de.uniregensburg.iamreportingmodule.web.component.notification;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Component for displaying success notifications (extends custom notification)
 *
 * @author Julian Bauer
 */
@Tag("success-notification")
public class SuccessNotification extends CustomNotification {

    /**
     *
     */
    public SuccessNotification() {
        this("", "");
    }

    /**
     *
     * @param message
     */
    public SuccessNotification(String message) {
        this("Success", message);
    }

    /**
     *
     * @param header
     * @param message
     */
    public SuccessNotification(String header, String message) {
        super(header, message);
        init();
    }

    /**
     * Initializes component
     */
    public void init() {
        getNotification().addThemeVariants(NotificationVariant.LUMO_SUCCESS); // color: green
    }
}
