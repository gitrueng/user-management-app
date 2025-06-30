package com.tca.UserManager.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tca.UserManager.entity.User;

/**
 * Repository interface for User entity.
 * Extends JpaRepository to provide CRUD operations and custom queries for User.
 */
@Repository // Marks this interface as a Spring Data repository bean
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Finds a user by their username.
     * @param username The username to search for
     * @return An Optional containing the User if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     * @param email The email to search for
     * @return An Optional containing the User if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Deletes a user by their user ID.
     * @param userId The ID of the user to delete
     */
    void deleteById(Integer userId);
}
