package com.tca.UserManager.provider;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import com.tca.UserManager.provider.factory.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Component class for providing configuration properties to the application.
 * Loads values from a YAML configuration file and exposes them via getter methods.
 */
@Component
@PropertySource(value = "classpath:config.yml", factory = YamlPropertySourceFactory.class)
public class ResourceProvider {
    // JWT secret key used for signing tokens
    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    // JWT expiration time in milliseconds
    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;

    // JWT issuer (who issued the token)
    @Value("${app.security.jwt.issuer}")
    private String jwtIssuer;

    // JWT audience (who the token is intended for)
    @Value("${app.security.jwt.audience}")
    private String jwtAudience;

    // Prefix for JWT in the Authorization header (e.g., "Bearer ")
    @Value("${app.security.jwt.prefix}")
    private String jwtPrefix;

    // URLs that should be excluded from JWT authentication
    @Value("${app.security.jwt.excluded.urls}")
    private String[] jwtExcludedUrls;

    // Client application base URL (used in email links)
    @Value("${client.url}")
    private String clientUrl;

    // Parameter name for email verification links
    @Value("${client.email.verify.param}")
    private String clientVerifyParam;

    // Expiration time for email verification tokens
    @Value("${client.email.verify.expiration}")
    private long clientVerifyExpiration;

    // Parameter name for password reset links
    @Value("${client.email.reset.param}")
    private String clientResetParam;

    // Expiration time for password reset tokens
    @Value("${client.email.reset.expiration}")
    private long clientResetExpiration;

    // Email subject for verification
    @Value("${app.mail.verification-subject}")
    private String mailVerificationSubject;

    // Email subject for password reset
    @Value("${app.mail.reset-password-subject}")
    private String mailResetPasswordSubject;

    // CORS allowed origins
    @Value("${app.cors.allowed-origins}")
    private String[] corsAllowedOrigins;

    // CORS allowed methods
    @Value("${app.cors.allowed-methods}")
    private String[] corsAllowedMethods;

    // CORS allowed headers
    @Value("${app.cors.allowed-headers}")
    private String corsAllowedHeaders;

    // CORS allow credentials
    @Value("${app.cors.allow-credentials}")
    private boolean corsAllowCredentials;

    // CORS max age
    @Value("${app.cors.max-age}")
    private long corsMaxAge;

    // Getters for all properties
    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    public String getJwtIssuer() {
        return jwtIssuer;
    }

    public String getJwtAudience() {
        return jwtAudience;
    }

    public String getJwtPrefix() {
        return jwtPrefix;
    }

    public String[] getJwtExcludedUrls() {
        return jwtExcludedUrls;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public String getClientVerifyParam() {
        return clientVerifyParam;
    }

    public long getClientVerifyExpiration() {
        return clientVerifyExpiration;
    }

    public String getClientResetParam() {
        return clientResetParam;
    }

    public long getClientResetExpiration() {
        return clientResetExpiration;
    }

    public String getMailVerificationSubject() {
        return mailVerificationSubject;
    }

    public String getMailResetPasswordSubject() {
        return mailResetPasswordSubject;
    }

    public String[] getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public String[] getCorsAllowedMethods() {
        return corsAllowedMethods;
    }

    public String getCorsAllowedHeaders() {
        return corsAllowedHeaders;
    }

    public boolean isCorsAllowCredentials() {
        return corsAllowCredentials;
    }

    public long getCorsMaxAge() {
        return corsMaxAge;
    }
}
