package de.uniregensburg.iamreportingmodule.core.service;

import de.uniregensburg.iamreportingmodule.data.entity.CustomUserDetails;
import de.uniregensburg.iamreportingmodule.data.entity.User;
import de.uniregensburg.iamreportingmodule.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for returning information of user for spring security
 *
 * @author Julian Bauer
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;

    /**
     *
     * @param userRepository
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Overrides method to get user details by username
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Loading user by username " + username);
        // get user
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.info("User null");
            throw new UsernameNotFoundException(username);
        }
        logger.info("Returning user");
        // return user details
        return new CustomUserDetails(user);
    }
}
