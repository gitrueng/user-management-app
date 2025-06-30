package com.tca.UserManager.exception.domain;

/**
 * Custom exception thrown when a username already exists in the system.
 * Extends RuntimeException so it can be thrown without being declared.
 */
public class UsernameExistException extends RuntimeException {
    // Used for serialization (keeps compatibility between different versions)
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UsernameExistException with the specified detail message.
     * @param message The detail message explaining the exception
     */
    public UsernameExistException(String message) {
        super(message);
    }
}
