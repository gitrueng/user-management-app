package com.tca.UserManager.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.tca.UserManager.domain.HttpResponse;
import com.tca.UserManager.exception.domain.EmailExistException;
import com.tca.UserManager.exception.domain.EmailNotFoundException;
import com.tca.UserManager.exception.domain.EmailNotVerifiedException;
import com.tca.UserManager.exception.domain.UserNotFoundException;
import com.tca.UserManager.exception.domain.UsernameExistException;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.LockedException;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import com.auth0.jwt.exceptions.TokenExpiredException;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import java.util.Objects;
import org.springframework.http.HttpMethod;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.web.bind.annotation.RequestMapping; // Use RequestMapping instead of GetMapping for error path

/**
 * Global exception handler for the application.
 * Catches and handles various exceptions, returning consistent HTTP responses.
 */
@RestController
@RestControllerAdvice // Allows this class to handle exceptions across all controllers
public class ExceptionHandling implements ErrorController {
    // Logger for logging errors and information
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Constants for error messages
    private static final String TOKEN_DECODE_ERROR = "Token Decode Error";
    private static final String TOKEN_EXPIRED_ERROR = "Token has Expired";
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username or Password is Incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    private static final String NOT_AUTHENTICATED = "You need to log in to access this URL";
    private static final String NO_MAPPING_EXIST_URL = "There is no mapping for this URL";
    private static final String ERROR_PATH = "/error";

    /**
     * Helper method to create a consistent HTTP response.
     */
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
    }

    /**
     * Handles JWT decode exceptions.
     */
    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<HttpResponse> tokenDecodeException() {
        return this.createHttpResponse(BAD_REQUEST, TOKEN_DECODE_ERROR);
    }

    /**
     * Handles account disabled exceptions.
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return this.createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    /**
     * Handles bad credentials (wrong username or password).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return this.createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    /**
     * Handles access denied exceptions (insufficient permissions).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return this.createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    /**
     * Handles authentication exceptions (not logged in).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpResponse> authenticationException() {
        return this.createHttpResponse(FORBIDDEN, NOT_AUTHENTICATED);
    }

    /**
     * Handles account locked exceptions.
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return this.createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    /**
     * Handles JWT token expired exceptions.
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException ex) {
        return this.createHttpResponse(UNAUTHORIZED, TOKEN_EXPIRED_ERROR);
    }

    /**
     * Handles email already exists exception.
     */
    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException ex) {
        return this.createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles username already exists exception.
     */
    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException ex) {
        return this.createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles email not found exception.
     */
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException ex) {
        return this.createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles email not verified exception.
     */
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<HttpResponse> emailNotVerifiedException(EmailNotVerifiedException ex) {
        return this.createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles user not found exception.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException ex) {
        return this.createHttpResponse(BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles HTTP request method not supported exceptions.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse>
    methodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        HttpMethod supportedMethod = Objects.requireNonNull(ex.getSupportedHttpMethods()).iterator().next();
        return this.createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED,
                supportedMethod));
    }

    /**
     * Handles all other exceptions (internal server errors).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        logger.error(exception.getMessage(), exception);
        return this.createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    /**
     * Handles no result found exceptions (e.g., entity not found in DB).
     */
    @ExceptionHandler(jakarta.persistence.NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(jakarta.persistence.NoResultException exception) {
        logger.error(exception.getMessage());
        return this.createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    /**
     * Handles unmapped URLs (404 errors) for the /error path.
     * Note: ErrorController's getErrorPath is deprecated since Spring Boot 2.3.
     * For Spring Boot 3+, use @RequestMapping with value.
     */
    @RequestMapping(ERROR_PATH) // Changed to @RequestMapping
    public ResponseEntity<HttpResponse> notFound404() throws Exception {
        return this.createHttpResponse(NOT_FOUND, NO_MAPPING_EXIST_URL);
    }
    
    // As of Spring Boot 3+, the getErrorPath method from ErrorController is deprecated
    // and no longer needs to be implemented directly. @RequestMapping("/error") handles it.
    // @Override
    // public String getErrorPath() {
    //    return ERROR_PATH;
    // }
}
