package de.uniregensburg.iamreportingmodule;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application.
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/Application.java
 *
 * @author Julian Bauer
 */
@SpringBootApplication
@Theme(value = "iamreportingmodule")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@NpmPackage(value = "@vaadin-component-factory/vcf-nav", version = "1.0.6")
public class Application implements AppShellConfigurator {

    /**
     * Starts spring application
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
