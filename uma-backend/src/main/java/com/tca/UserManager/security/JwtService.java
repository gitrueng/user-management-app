package com.tca.UserManager.security;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.tca.UserManager.provider.ResourceProvider;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import java.util.Date;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Service class for handling JWT (JSON Web Token) operations.
 * Responsible for generating, verifying, and extracting information from JWT tokens.
 */
@Component
public class JwtService {
    @Autowired
    ResourceProvider provider;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Generates a JWT token for the given username with the specified expiration time.
     * Sets issuer, audience, issued date, subject, and expiration in the token.
     *
     * @param username The username to include as the subject in the token
     * @param expiration The expiration time in milliseconds
     * @return A signed JWT token as a String
     */
    public String generateJwtToken(String username, long expiration) {
        return JWT.create()
                .withIssuer(this.provider.getJwtIssuer())
                .withAudience(this.provider.getJwtAudience())
                .withIssuedAt(new Date())
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(HMAC512(this.provider.getJwtSecret()));
    }

    /**
     * Verifies the given JWT token using the configured secret and issuer.
     * Throws an exception if the token is invalid or expired.
     *
     * @param token The JWT token to verify
     * @return The decoded JWT object if verification is successful
     */
    public DecodedJWT verifyJwtToken(String token) {
        return JWT.require(HMAC512(this.provider.getJwtSecret()))
                .withIssuer(this.provider.getJwtIssuer())
                .build().verify(token);
    }

    /**
     * Extracts the subject (usually the username) from the given JWT token.
     *
     * @param token The JWT token
     * @return The subject (username) from the token
     */
    public String getSubject(String token) {
        return JWT.require(HMAC512(this.provider.getJwtSecret()))
                .withIssuer(this.provider.getJwtIssuer())
                .build().verify(token).getSubject();
    }
}
