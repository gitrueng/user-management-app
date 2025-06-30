package com.tca.UserManager.exception.domain;

/**
 * Custom exception thrown when a user's email is not verified.
 * Extends RuntimeException so it can be thrown without being declared.
 */
public class EmailNotVerifiedException extends RuntimeException {
    // Used for serialization (keeps compatibility between different versions)
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new EmailNotVerifiedException with the specified detail message.
     * @param message The detail message explaining the exception
     */
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
