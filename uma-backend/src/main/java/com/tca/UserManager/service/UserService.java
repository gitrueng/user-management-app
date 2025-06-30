package com.tca.UserManager.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpHeaders.AUTHORIZATION; // Static import for HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import for @Transactional
import org.springframework.util.StringUtils; // Import for StringUtils

import com.tca.UserManager.entity.User;
import com.tca.UserManager.exception.domain.EmailExistException;
import com.tca.UserManager.exception.domain.EmailNotVerifiedException;
import com.tca.UserManager.exception.domain.UserNotFoundException;
import com.tca.UserManager.exception.domain.UsernameExistException;
import com.tca.UserManager.provider.ResourceProvider;
import com.tca.UserManager.repository.UserRepository;
import com.tca.UserManager.security.JwtService;

/**
 * Service class for handling user-related business logic.
 * This class acts as a bridge between the controller and the repository.
 */
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRepository userRepository; // Handles database operations for User

    @Autowired
    EmailService emailService; // Handles sending emails (verification, reset, etc.)

    @Autowired
    PasswordEncoder passwordEncoder; // Used to hash and verify passwords

    @Autowired
    AuthenticationManager authenticationManager; // Used for authenticating users

    @Autowired
    JwtService jwtService; // Service for generating JWT tokens

    @Autowired
    ResourceProvider provider; // Provides configuration resources (like JWT expiration)

    /**
     * Utility method to update a value if it is not null or empty.
     * Used for updating user fields only if a new value is provided.
     */
    private void updateValue(Supplier<String> getter, Consumer<String> setter) {
        Optional.ofNullable(getter.get())
            .filter(StringUtils::hasText) // Ensure StringUtils is imported
            .map(String::trim)
            .ifPresent(setter);
    }

    /**
     * Returns a list of all users in the system.
     */
    public List<User> listUsers() {
        return this.userRepository.findAll();
    }

    /**
     * Finds a user by their username.
     */
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    /**
     * Deletes a user by their user ID.
     */
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Creates a new user in the database.
     */
    public void createUser(User user) {
        this.userRepository.save(user);
    }

    /**
     * Handles user signup/registration.
     * - Validates username and email uniqueness
     * - Hashes the password
     * - Sets email as unverified
     * - Sets creation timestamp
     * - Sends verification email
     * - Saves the user to the database
     */
    public User signup(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        user.setEmail(user.getEmail().toLowerCase());
        this.validateUsernameAndEmail(user.getUsername(), user.getEmail());
        user.setEmailVerified(false);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(Timestamp.from(Instant.now()));
        this.emailService.sendVerificationEmail(user);
        this.userRepository.save(user);
        return user;
    }

    /**
     * Checks if the username or email already exists in the database.
     * Throws exceptions if they do.
     */
    private void validateUsernameAndEmail(String username, String email) {
        this.userRepository.findByUsername(username).ifPresent(u -> {
            throw new UsernameExistException(String.format("Username already exists, %s", u.getUsername()));
        });
        this.userRepository.findByEmail(email).ifPresent(u -> {
            throw new EmailExistException(String.format("Email already exists, %s", u.getEmail()));
        });
    }

    /**
     * Marks the currently authenticated user's email as verified.
     */
    public void verifyEmail() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
        user.setEmailVerified(true);
        user.setUpdatedAt(Timestamp.from(Instant.now())); // Add updated timestamp
        this.userRepository.save(user);
    }

    /**
     * Checks if a user's email is verified.
     * Throws an exception if not.
     */
    private static User isEmailVerified(User user) {
        if (user.getEmailVerified().equals(false)) {
            throw new EmailNotVerifiedException(String.format("Email requires verification, %s", user.getEmail()));
        }
        return user;
    }

    /**
     * Authenticates a user using Spring Security.
     */
    private Authentication authenticate(String username, String password) {
        return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    /**
     * Authenticates a user and checks if their email is verified.
     * Returns the user if successful.
     */
    public User authenticate(User user) {
        // Spring Security Authentication
        this.authenticate(user.getUsername(), user.getPassword());
        // Get User from the DB and check email verification
        return this.userRepository.findByUsername(user.getUsername()).map(UserService::isEmailVerified).get();
    }

    /**
     * Generates HTTP headers containing a JWT token for the given username.
     */
    public HttpHeaders generateJwtHeader(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, this.jwtService.generateJwtToken(username, this.provider.getJwtExpiration()));
        return headers;
    }

    /**
     * Sends a password reset email to the user if the email exists.
     */
    public void sendResetPasswordEmail(String email) {
        Optional<User> opt = this.userRepository.findByEmail(email);
        if (opt.isPresent()) {
            this.emailService.sendResetPasswordEmail(opt.get());
        } else {
            logger.debug("Email doesn't exist, {}", email);
        }
    }

    /**
     * Resets the password for the currently authenticated user.
     */
    public void resetPassword(String password) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
        user.setPassword(this.passwordEncoder.encode(password));
        user.setUpdatedAt(Timestamp.from(Instant.now())); // Add updated timestamp
        this.userRepository.save(user);
    }

    /**
     * Returns the currently authenticated user.
     */
    public User getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Get User from the DB
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
    }

    /**
     * Utility method to update the password if a new one is provided.
     */
    private void updatePassword(Supplier<String> getter, Consumer<String> setter) {
        Optional.ofNullable(getter.get())
            .filter(StringUtils::hasText)
            .map(this.passwordEncoder::encode)
            .ifPresent(setter);
    }

    /**
     * Updates the fields of the current user with new values if provided.
     */
    private User updateUser(User user, User currentUser) {
        this.updateValue(user::getFirstName, currentUser::setFirstName);
        this.updateValue(user::getLastName, currentUser::setLastName);
        this.updateValue(user::getPhoneNumber, currentUser::setPhoneNumber);
        this.updateValue(user::getEmail, currentUser::setEmail);
        this.updatePassword(user::getPassword, currentUser::setPassword);
        currentUser.setUpdatedAt(Timestamp.from(Instant.now())); // Update the timestamp
        return this.userRepository.save(currentUser);
    }

    /**
     * Updates the currently authenticated user's information.
     * - Checks if the new email is already used by another user
     * - Updates the user's fields
     */
    @Transactional // Ensure the whole update operation is transactional
    public User updateUser(User user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Find current user to check against new email
        User currentUser = this.userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

        // Validates the new email if provided and if it belongs to another user
        if (user.getEmail() != null && StringUtils.hasText(user.getEmail())) {
            this.userRepository.findByEmail(user.getEmail())
                .filter(u -> !u.getUsername().equals(username)) // Filter if email belongs to someone else
                .ifPresent(u -> {
                    throw new EmailExistException(String.format("Email already exists, %s", u.getEmail()));
                });
        }
        
        // Update user fields and save
        return this.updateUser(user, currentUser);
    }
}
