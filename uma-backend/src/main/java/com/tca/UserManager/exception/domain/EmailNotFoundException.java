package com.tca.UserManager.exception.domain;

/**
 * Custom exception thrown when an email is not found in the system.
 * Extends RuntimeException so it can be thrown without being declared.
 */
public class EmailNotFoundException extends RuntimeException {
    // Used for serialization (keeps compatibility between different versions)
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new EmailNotFoundException with the specified detail message.
     * @param message The detail message explaining the exception
     */
    public EmailNotFoundException(String message) {
        super(message);
    }
}
