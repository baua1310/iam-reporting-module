package de.uniregensburg.iamreportingmodule.data.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity custom user details implements spring user details
 * Authorities: ROLE_ADMIN
 *
 * @author Julian Bauer
 */
public class CustomUserDetails implements UserDetails {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private User user;

    /**
     *
     * @param user
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns authorities
     * if User isAdmin == true -> ROLE_ADMIN
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        logger.info("Authorities: " + authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")));
        return authorities;
    }

    /**
     * Returns password of user
     *
     * @return
     */
    @Override
    public String getPassword() {
        logger.info("Returning user password");
        return user.getPassword();
    }

    /**
     * Returns username of user
     *
     * @return
     */
    @Override
    public String getUsername() {
        logger.info("Returning username");
        return user.getUsername();
    }

    /**
     * Returns account non expired (always true)
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        logger.info("Returning is account not expired?");
        return true;
    }

    /**
     * Returns account non locked (always true)
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        logger.info("Returning is account not locked?");
        return true;
    }

    /**
     * Returns credentials non expired (always true)
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        logger.info("Returning is account credentials not expired?");
        return true;
    }

    /**
     * Returns account enabled (always true)
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        logger.info("Returning is account enabled?");
        return true;
    }
}
