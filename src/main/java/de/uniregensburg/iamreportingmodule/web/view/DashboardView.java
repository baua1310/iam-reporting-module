package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.uniregensburg.iamreportingmodule.core.service.GroupService;
import de.uniregensburg.iamreportingmodule.core.service.InformationNeedService;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.core.service.UserService;
import de.uniregensburg.iamreportingmodule.data.entity.*;
import de.uniregensburg.iamreportingmodule.web.component.dashboard.Indicator;
import de.uniregensburg.iamreportingmodule.web.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.addons.tatu.GridLayout;

import javax.annotation.security.PermitAll;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * View for displaying report
 *
 * @author Julian Bauer
 */
@PageTitle("Dashboard | IAM Reporting Modul")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class DashboardView extends VerticalLayout implements HasUrlParameter<String> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MeasurableService measurableService;
    private final InformationNeedService informationNeedService;
    private final GridLayout gridLayout = new GridLayout();
    private final Set<Audience> audiences;
    private final ComboBox<InformationNeed> informationNeedList = new ComboBox<>("Information Need");
    private final Text warningText = new Text("No information need selected");
    private final Div warning = new Div();

    /**
     *
     * @param measurableService
     * @param securityService
     * @param groupService
     * @param userService
     * @param informationNeedService
     */
    public DashboardView(MeasurableService measurableService, SecurityService securityService, GroupService groupService, UserService userService, InformationNeedService informationNeedService) {
        this.measurableService = measurableService;
        this.informationNeedService = informationNeedService;

        setSizeFull(); // use full size

        UserDetails userDetails = securityService.getAuthenticatedUser();
        String username = userDetails.getUsername();
        User user = userService.findUserByUsername(username);
        audiences = new HashSet<>(groupService.findAudiencesByMember(user));

        addClassName("dashboard");

        configureWarning();

        Div gridRoot = new Div();
        gridRoot.addClassName("grid-root");
        gridRoot.addClassName(LumoUtility.Position.RELATIVE);
        gridRoot.setSizeFull();

        gridRoot.add(getGridLayout(), warning);

        add(getToolbar(), gridRoot);
    }

    /**
     * Configures grid layout
     *
     * @return
     */
    private Component getGridLayout() {
        logger.info("Initializing grid layout");

        gridLayout.setSizeFull();
        gridLayout.setGap(GridLayout.Gap.LARGE);
        gridLayout.setJustify(GridLayout.Justify.EVENLY);
        gridLayout.setAlign(GridLayout.Align.START);
        gridLayout.setOrientation(GridLayout.Orientation.BY_COLUMNS,3);
        gridLayout.addClassName(LumoUtility.Position.ABSOLUTE);
        gridLayout.getStyle().set("top", "0px");
        gridLayout.getStyle().set("left", "0px");

        return gridLayout;
    }

    /**
     * Configures warning
     *
     */
    private void configureWarning() {
        warning.addClassName("warning");
        warning.addClassName(LumoUtility.JustifyContent.CENTER);
        warning.addClassName(LumoUtility.AlignItems.CENTER);
        warning.addClassName(LumoUtility.FontSize.XXLARGE);
        warning.setSizeFull();
        warning.addClassName(LumoUtility.Position.ABSOLUTE);
        warning.getStyle().set("top", "0px");
        warning.getStyle().set("left", "0px");
        warning.addClassName(LumoUtility.Display.FLEX);
        warning.add(warningText);
    }

    /**
     * Configures and returns toolbar
     *
     * @return
     */
    private Component getToolbar() {
        logger.info("Initializing toolbar");

        informationNeedList.setItems(informationNeedService.findAllInformationNeeds());
        informationNeedList.addValueChangeListener(value -> {
            updateIndicators(value.getValue());
            updateQueryParameters(value.getValue());
        });
        informationNeedList.setPlaceholder("Select information need");

        HorizontalLayout toolbar = new HorizontalLayout(informationNeedList); // horizontal layout
        toolbar.addClassName("toolbar"); // set css class name

        return toolbar;
    }

    /**
     * Updates indicators
     *
     * @param informationNeed
     */
    private void updateIndicators(InformationNeed informationNeed) {
        logger.info("Updating indicators");
        gridLayout.removeAll();
        if (informationNeed == null) {
            logger.info("Information need null");
            warningText.setText("Information need is null");
            warning.removeClassName(LumoUtility.Display.HIDDEN);
            return;
        }
        logger.info("Information need: " + informationNeed.getName());
        if (audiences.isEmpty()) {
            logger.info("User is not part of any audiences");
            warningText.setText("You are not part of any audiences");
            warning.removeClassName(LumoUtility.Display.HIDDEN);
            return;
        }
        logger.info("Audiences: " + audiences.stream().map(Audience::getName).collect(Collectors.joining(", ")));
        List<Metric> metrics = measurableService.findMetricsByInformationNeedAndAudiences(informationNeed, audiences);
        if (metrics.isEmpty()) {
            logger.info("No metrics found");
            warningText.setText("No metrics found");
            warning.removeClassName(LumoUtility.Display.HIDDEN);
            return;
        }
        Set<Metric> metricSet = new HashSet<>(metrics); // remove duplicates
        warning.addClassName(LumoUtility.Display.HIDDEN);
        for (Metric metric : metricSet) {
            Result result = measurableService.findLatestResultByMeasurable(metric);
            Indicator indicator = new Indicator();
            indicator.setTitle(metric.getName());
            if (metric.getUnit() != null) {
                indicator.setUnit(metric.getUnit().toString());
            }
            indicator.setLink("/results/" + metric.getId());
            if (result == null) {
                indicator.setValue("null");
            } else {
                BigDecimal resultValue = result.getValue();;
                if (metric.getUnit() != null) {
                    if (Unit.PERCENT.equals(metric.getUnit())) {
                        resultValue = resultValue.multiply(BigDecimal.valueOf(100));
                    }
                }
                indicator.setValue(resultValue.stripTrailingZeros().toPlainString());
            }
            gridLayout.add(indicator);
        }
    }

    /**
     * Updates query parameter based on currently selected information need
     *
     * @param informationNeed
     */
    private void updateQueryParameters(InformationNeed informationNeed) {
        String deepLinkingUrl = RouteConfiguration.forSessionScope()
                .getUrl(getClass(), informationNeed.getId().toString());
        // Assign the full deep linking URL directly using
        // History object: changes the URL in the browser,
        // but doesn't reload the page.
        if (getUI().isPresent()) {
            getUI().get().getPage().getHistory()
                    .replaceState(null, deepLinkingUrl);
        }
    }

    /**
     * Searches for url parameter id and starts indicator update
     *
     * @param event
     * @param id
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String id) {
        if (id == null) {
            return;
        }
        logger.info("Searching information need with id " + id);
        try {
            InformationNeed informationNeed = informationNeedService.findInformationNeedById(UUID.fromString(id));
            informationNeedList.setValue(informationNeed);
            updateIndicators(informationNeed);
        } catch (IllegalArgumentException e) {
            logger.info("Error finding measurable");
            logger.info(e.getMessage());
        }
    }

}
