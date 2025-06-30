package com.tca.UserManager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Correct import
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource; // Correct import for CorsConfigurationSource

import com.tca.UserManager.filter.CustomAuthEntryPoint;
import com.tca.UserManager.filter.JwtAuthorizationFilter;
import com.tca.UserManager.provider.ResourceProvider;
import com.tca.UserManager.security.CustomUserDetailsService;

/**
 * This class configures the security settings for the application using Spring Security.
 * It defines which endpoints are public, which require authentication, and how authentication is handled.
 */
@Configuration // Marks this class as a configuration class for Spring
@EnableWebSecurity // Enables Spring Security for the application
@EnableMethodSecurity // Allows method-level security annotations (like @PreAuthorize)
public class WebSecurityConfig {

    // Injects the JWT filter that checks for valid JWT tokens in requests
    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    CustomAuthEntryPoint customAuthEntryPoint;

    // Custom service to load user details from the database
    private final CustomUserDetailsService userDetailsService;

    // Configuration for Cross-Origin Resource Sharing (CORS)
    private final CorsConfigurationSource corsConfigurationSource; // Correct type

    @Autowired
    ResourceProvider provider;

    // Constructor injection for required beans
    public WebSecurityConfig(
            JwtAuthorizationFilter jwtAuthorizationFilter, // Corrected parameter name
            CustomUserDetailsService userDetailsService,
            CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter; // Assign the injected filter
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Defines the main security filter chain.
     * - Disables CSRF (not needed for stateless APIs)
     * - Enables CORS with the provided configuration
     * - Sets session management to STATELESS (no server-side sessions)
     * - Specifies which endpoints are public and which require authentication
     * - Adds the JWT filter before the standard username/password filter
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Use lambda for cors config
            .csrf(AbstractHttpConfigurer::disable) // Use lambda for csrf disable
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(this.provider.getJwtExcludedUrls()).permitAll() // Use excluded URLs from config.yml
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception.authenticationEntryPoint(this.customAuthEntryPoint))
            .addFilterBefore(this.jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Defines the password encoder bean.
     * BCrypt is a strong hashing function for storing passwords securely.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean.
     * This is used to authenticate users in the application.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}