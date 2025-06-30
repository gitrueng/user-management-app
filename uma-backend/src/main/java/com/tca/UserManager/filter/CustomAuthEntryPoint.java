package com.tca.UserManager.filter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication entry point for handling unauthorized access attempts.
 * This class is used by Spring Security to handle authentication exceptions
 * (such as when a user tries to access a protected resource without being authenticated).
 */
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    // HandlerExceptionResolver is used to delegate exception handling to Spring's global exception handler
    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    /**
     * This method is called whenever an unauthenticated user tries to access a secured endpoint.
     * It delegates the exception handling to the global exception resolver.
     *
     * @param req The HTTP request
     * @param res The HTTP response
     * @param ex The authentication exception that was thrown
     * @throws IOException if an input or output error occurs
     */
    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws
            IOException {
        // Delegate exception handling to the global exception resolver
        this.resolver.resolveException(req, res, null, ex);
    }
}
