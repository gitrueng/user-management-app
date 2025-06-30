package com.tca.UserManager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.tca.UserManager.entity.User;
import com.tca.UserManager.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PathVariable;
import java.sql.Timestamp;
import java.time.Instant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode; // For password reset JSON parsing
import org.springframework.web.bind.annotation.PutMapping; // For update method consistency

/**
 * Controller class for handling HTTP requests related to User operations.
 * Maps REST endpoints to service methods for user management.
 */
@CrossOrigin(exposedHeaders = "Authorization") // Allows cross-origin requests and exposes the Authorization header
@RestController // Marks this class as a REST controller
@RequestMapping("/user") // Base URL for all endpoints in this controller
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService; // Injects the UserService to handle business logic

    /**
     * Returns a list of all users.
     */
    @GetMapping("/")
    public List<User> listUsers() {
        logger.debug("The listUsers() method was invoked!");
        return this.userService.listUsers();
    }

    /**
     * Finds a user by their username.
     * @param username The username to search for
     */
    @GetMapping("/{username}")
    public Optional<User> findByUsername(@PathVariable String username) {
        logger.debug("The findByUsername() method was invoked!, username={}", username);
        return this.userService.findByUsername(username);
    }

    /**
     * Deletes a user by their user ID.
     * @param userId The ID of the user to delete
     * @return A message indicating successful deletion
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable("userId") Integer userId) {
        logger.debug("The deleteByUserId() method was invoked!, userId={}", userId);
        userService.deleteUserById(userId);
        return new ResponseEntity<>("User Deleted Successfully", OK); // Return a ResponseEntity for better API practice
    }

    /**
     * Handles user signup/registration.
     * @param user The user object from the request body
     * @return The created user object
     */
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) { // Return ResponseEntity for consistent API
        logger.debug("Signing up, username: {}", user.getUsername());
        User createdUser = this.userService.signup(user);
        return new ResponseEntity<>(createdUser, OK); // Return 200 OK for successful signup
    }

    /**
     * Verifies the email of the currently authenticated user.
     */
    @GetMapping("/verify/email")
    public ResponseEntity<Void> verifyEmail() { // Return ResponseEntity<Void>
        logger.debug("Verifying Email");
        this.userService.verifyEmail();
        return new ResponseEntity<>(OK); // Return 200 OK
    }

    /**
     * Authenticates a user and returns a JWT in the response header if successful.
     * @param user The user credentials from the request body
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        logger.debug("Authenticating, username: {}, password: {}", user.getUsername(), user.getPassword());
        // Authenticate user using Spring Security
        user = this.userService.authenticate(user);
        // Generate JWT and HTTP Header
        HttpHeaders jwtHeader = this.userService.generateJwtHeader(user.getUsername());
        logger.debug("User Authenticated, username: {}", user.getUsername());
        return new ResponseEntity<>(user, jwtHeader, OK);
    }

    /**
     * Sends a password reset email to the user with the given email address.
     * @param emailId The email address to send the reset link to
     */
    @GetMapping("/reset/{emailId}")
    public ResponseEntity<Void> sendResetPasswordEmail(@PathVariable String emailId) { // Return ResponseEntity<Void>
        logger.debug("Sending Reset Password Email, emailId: {}", emailId);
        this.userService.sendResetPasswordEmail(emailId);
        return new ResponseEntity<>(OK); // Return 200 OK
    }

    /**
     * Resets the password for the currently authenticated user.
     * @param json JSON object containing the new password
     */
    @PostMapping("/reset")
    public ResponseEntity<Void> passwordReset(@RequestBody JsonNode json) { // Return ResponseEntity<Void>
        logger.debug("Resetting Password, password: {}", json.get("password").asText());
        this.userService.resetPassword(json.get("password").asText());
        return new ResponseEntity<>(OK); // Return 200 OK
    }

    /**
     * Returns the currently authenticated user's data.
     */
    @GetMapping("/get")
    public ResponseEntity<User> getUser() { // Return ResponseEntity<User>
        logger.debug("Getting User Data");
        User user = this.userService.getUser();
        return new ResponseEntity<>(user, OK); // Return 200 OK with user
    }

    /**
     * Updates the currently authenticated user's information.
     * @param user The updated user data from the request body
     */
    @PutMapping("/update") // Changed to PutMapping for update consistency
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        logger.debug("Updating User Data");
        User updatedUser = this.userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, OK); // Return 200 OK with updated user
    }
}
