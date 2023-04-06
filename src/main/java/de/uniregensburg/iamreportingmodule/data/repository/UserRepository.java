package de.uniregensburg.iamreportingmodule.data.repository;

import de.uniregensburg.iamreportingmodule.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for users
 * extends JpaRepository
 *
 * @author Julian Bauer
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Returns user by username
     *
     * @param username
     * @return
     */
    User findByUsername(String username);
}
