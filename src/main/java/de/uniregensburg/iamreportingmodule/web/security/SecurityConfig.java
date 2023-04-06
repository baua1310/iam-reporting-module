package de.uniregensburg.iamreportingmodule.web.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import de.uniregensburg.iamreportingmodule.web.view.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration of login security: pages that require no authentication, login page, password encoder
 *
 * Template: https://github.com/vaadin/flow-crm-tutorial/blob/v23/src/main/java/com/example/application/security/SecurityConfig.java
 *
 * @author Julian Bauer
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    /**
     * Configures pages that require no authentication and login page
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Authorize access to /images/ and /icons/ without authentication
        http.authorizeRequests().antMatchers("/images/**", "/icons/**").permitAll();
        // Set default security policy that permits Vaadin internal requests and
        // denies all other
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    /**
     * Returns password encoder: bcrypt
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}