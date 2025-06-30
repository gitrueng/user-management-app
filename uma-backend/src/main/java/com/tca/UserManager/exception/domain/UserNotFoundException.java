package com.tca.UserManager.exception.domain;

/**
 * Custom exception thrown when a user is not found in the system.
 * Extends RuntimeException so it can be thrown without being declared.
 */
public class UserNotFoundException extends RuntimeException {
    // Used for serialization (keeps compatibility between different versions)
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     * @param message The detail message explaining the exception
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}