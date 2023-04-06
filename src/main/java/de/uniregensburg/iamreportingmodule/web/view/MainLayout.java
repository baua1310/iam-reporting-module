package de.uniregensburg.iamreportingmodule.web.view;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.uniregensburg.iamreportingmodule.web.component.appnav.AppNav;
import de.uniregensburg.iamreportingmodule.web.component.appnav.AppNavItem;
import de.uniregensburg.iamreportingmodule.web.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Layout for all views
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/views/MainLayout.java
 *
 * @author Julian Bauer
 */
public class MainLayout extends AppLayout {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final H2 viewTitle = new H2();
    private final SecurityService securityService;
    private final HorizontalLayout navbarButtons = new HorizontalLayout();

    /**
     *
     * @param securityService
     */
    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        setPrimarySection(Section.DRAWER);
        createDrawer();
        createHeader();
    }

    /**
     * Creates Header
     */
    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        HorizontalLayout header = new HorizontalLayout(toggle, viewTitle, navbarButtons);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(viewTitle);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(true, header);
    }

    /**
     * Creates Drawer
     */
    private void createDrawer() {
        H1 appName = new H1("IAM Reporting Module");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    /**
     * Creates global navigation
     *
     * @return
     */
    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        UserDetails userDetails = securityService.getAuthenticatedUser();
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        nav.addItem(new AppNavItem("Dashboard", DashboardView.class, "la la-chart-bar"));
        if (isAdmin) {
            nav.addItem(new AppNavItem("Metrics", MetricsView.class, "la la-percent"));
            nav.addItem(new AppNavItem("Measurements", MeasurementsView.class, "la la-ruler"));
            nav.addItem(new AppNavItem("Datasources", DataSourcesView.class, "la la-database"));
            nav.addItem(new AppNavItem("Information needs", InformationNeedsView.class, "la la-bullseye"));
            nav.addItem(new AppNavItem("Stakeholders", StakeholdersView.class, "la la-user-friends"));
            nav.addItem(new AppNavItem("Audiences", AudiencesView.class, "la la-users"));
        }
        nav.addItem(new AppNavItem("Log out", LogoutView.class, "la la-sign-out-alt"));

        return nav;
    }

    /**
     * Creates footer
     *
     * @return
     */
    private Footer createFooter() {
        return new Footer();
    }

    /**
     * Changes page title after navigation
     */
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    /**
     * Returns current page title
     *
     * @return
     */
    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    /**
     * Returns current active main layout
     *
     * Template: https://vaadin.com/forum/thread/18552728/change-navbar-content-in-applayout-dynamically-when-navigation-occurs
     *
     * @return
     */
    public static MainLayout getInstance() {
        return (MainLayout) UI.getCurrent().getChildren()
                .filter(component -> component.getClass() == MainLayout.class).findFirst().orElse(null);
    }

    /**
     * Returns navbar buttons
     *
     * @return
     */
    public HorizontalLayout getNavbarButtons() {
        return navbarButtons;
    }
}
