package de.uniregensburg.iamreportingmodule.core.service;

import de.uniregensburg.iamreportingmodule.data.entity.User;
import de.uniregensburg.iamreportingmodule.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for persisting users
 *
 * @author Julian Bauer
 */
@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    /**
     *
     * @param userRepository
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns all users
     * @return
     */
    public List<User> findAllUsers() {
        logger.info("Returning all users");
        // get and return all users
        return userRepository.findAll();
    }

    /**
     * Returns user by username
     *
     * @param username
     * @return
     */
    public User findUserByUsername(String username) {
        logger.info("Searching user with username " + username);
        // search user by username
        User user = userRepository.findByUsername(username);

        // check user
        if (user == null) {
            logger.info("User not found");
            return null;
        }

        logger.info("User found");
        // return user
        return user;
    }
}
