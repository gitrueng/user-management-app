package com.tca.UserManager.exception.domain;

/**
 * Custom exception thrown when an email already exists in the system.
 * Extends RuntimeException so it can be thrown without being declared.
 */
public class EmailExistException extends RuntimeException {
    // Used for serialization (keeps compatibility between different versions)
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new EmailExistException with the specified detail message.
     * @param message The detail message explaining the exception
     */
    public EmailExistException(String message) {
        super(message);
    }
}
