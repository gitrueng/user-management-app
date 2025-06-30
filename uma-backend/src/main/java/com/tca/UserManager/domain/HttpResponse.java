package com.tca.UserManager.domain;

import java.util.Date;
import org.springframework.http.HttpStatus;
import java.text.SimpleDateFormat; // Correct import for SimpleDateFormat

/**
 * A simple class to represent HTTP responses in a consistent format.
 * Contains information such as timestamp, status code, status, reason, and message.
 */
public class HttpResponse {

    // The time when the response was created
    private Date timeStamp; // Made private with getters

    // The numeric HTTP status code (e.g., 200, 201, 400, 500)
    private int httpStatusCode; // Made private with getters

    // The HttpStatus enum (e.g., OK, BAD_REQUEST)
    private HttpStatus httpStatus; // Made private with getters

    // A short reason phrase for the response (e.g., "OK", "Bad Request")
    private String reason; // Made private with getters

    // A detailed message about the response
    private String message; // Made private with getters

    /**
     * Constructor to create a new HttpResponse object.
     * Sets the timestamp to the current date and time.
     *
     * @param httpStatusCode The numeric HTTP status code
     * @param httpStatus The HttpStatus enum value
     * @param reason A short reason phrase
     * @param message A detailed message
     */
    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

    // Getter for the timestamp
    public Date getTimeStamp() {
        return timeStamp;
    }

    // Getter for the HTTP status code
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    // Getter for the HttpStatus enum
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    // Getter for the reason phrase
    public String getReason() {
        return reason;
    }

    // Getter for the detailed message
    public String getMessage() {
        return message;
    }

    /**
     * Returns a string representation of the HttpResponse object.
     * Formats the timestamp for readability.
     */
    @Override
    public String toString() {
        String formattedTimeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(this.timeStamp);
        return "HttpResponse [timeStamp=" + formattedTimeStamp + ", httpStatusCode=" + httpStatusCode + ", httpStatus="
                + httpStatus + ", reason=" + reason + ", message=" + message + "]";
    }
}
