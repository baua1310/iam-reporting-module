package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uniregensburg.iamreportingmodule.web.security.SecurityService;

import javax.annotation.security.PermitAll;

/**
 * View for logging user out
 *
 * @author Julian Bauer
 */
@Route(value = "logout")
@PageTitle("Logout | IAM Reporting Module")
@PermitAll
public class LogoutView extends Div {

    public LogoutView(SecurityService service) {
        service.logout();
    }
}
