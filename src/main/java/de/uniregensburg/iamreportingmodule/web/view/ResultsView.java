package de.uniregensburg.iamreportingmodule.web.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.uniregensburg.iamreportingmodule.core.service.MeasurableService;
import de.uniregensburg.iamreportingmodule.data.entity.Measurable;
import de.uniregensburg.iamreportingmodule.data.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.UUID;

/**
 * View for displaying results
 *
 * @author Julian Bauer
 */
@PageTitle("Results | IAM Reporting Modul")
@Route(value = "results", layout = MainLayout.class)
@PermitAll
public class ResultsView extends VerticalLayout implements HasUrlParameter<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Measurable measurable = null;
    private String id;
    private final MeasurableService service;
    private final Grid<Result> grid = new Grid<>(Result.class);
    private final Text warningText = new Text("There is no data to display");
    private final Div warning = new Div();

    /**
     *
     * @param service
     */
    public ResultsView(MeasurableService service) {
        this.service = service;
        addClassName("result-view"); // CSS class name
        setSizeFull(); // whole browser size
    }

    /**
     * Initializes view components
     */
    private void init() {
        if (measurable == null) {
            logger.info("Measurable is null");
            add(new Text("Cannot find measurable with id " + id));
        } else {
            configureGrid();
            configureWarning();

            H2 gridHeading = new H2("Latest ten results of " + measurable.getName());

            Div gridRoot = new Div();
            gridRoot.addClassName("grid-root");
            gridRoot.addClassName(LumoUtility.Position.RELATIVE);
            gridRoot.setSizeFull();

            gridRoot.add(grid, warning);

            add(gridHeading, gridRoot);
            updateResults();
        }
    }

    /**
     * Updates results
     */
    private void updateResults() {
        List<Result> results = service.findFirst10ResultsByMeasurableOrderByPointInTimeDesc(measurable);
        logger.info("Results: " + results.size());
        grid.setItems(results);
        if (results.isEmpty()) {
            warning.removeClassName(LumoUtility.Display.HIDDEN);
        } else {
            warning.addClassName(LumoUtility.Display.HIDDEN);
        }
    }

    /**
     * Configures warning
     */
    private void configureWarning() {
        warning.addClassName("warning");
        warning.addClassName(LumoUtility.JustifyContent.CENTER);
        warning.addClassName(LumoUtility.AlignItems.CENTER);
        warning.addClassName(LumoUtility.FontSize.XLARGE);
        warning.setSizeFull();
        warning.addClassName(LumoUtility.Position.ABSOLUTE);
        warning.getStyle().set("top", "0px");
        warning.getStyle().set("left", "0px");
        warning.addClassName(LumoUtility.Display.FLEX);
        warning.add(warningText);
    }

    /**
     * Configures grid
     */
    private void configureGrid() {
        grid.setPageSize(10); // show only ten elements
        grid.addClassName("result-grid"); // set css class name
        grid.setSizeFull(); // use full size available
        grid.addClassName(LumoUtility.Position.ABSOLUTE);
        grid.getStyle().set("top", "0px");
        grid.getStyle().set("left", "0px");
        grid.removeAllColumns();
        grid.setAllRowsVisible(true);
        grid.addColumn(result -> result.getValue().stripTrailingZeros().toPlainString()).setHeader("Value").setKey("value");
        grid.addColumn(Result::getPointInTime).setHeader("Point in Time").setKey("pointInTime");
        Grid.Column<Result> pointInTime = grid.getColumnByKey("pointInTime");
        GridSortOrder<Result> order = new GridSortOrder<>(pointInTime, SortDirection.DESCENDING);
        grid.sort(List.of(order)); // set sorting
        grid.getColumns().forEach(col -> col.setAutoWidth(true)); // auto resize
    }

    /**
     * Searches for url parameter id and starts view initialization
     *
     * @param event
     * @param id
     */
    @Override
    public void setParameter(BeforeEvent event, String id) {
        this.id = id;
        logger.info("Searching measurable with id " + id);
        try {
            measurable = service.findMeasurableById(UUID.fromString(id));
            init();
        } catch (IllegalArgumentException e) {
            logger.info("Error finding measurable");
            logger.info(e.getMessage());
        }
    }
}
