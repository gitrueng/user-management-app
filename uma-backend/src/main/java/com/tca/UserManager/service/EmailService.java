package com.tca.UserManager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.tca.UserManager.entity.User;
import com.tca.UserManager.provider.ResourceProvider;
import com.tca.UserManager.security.JwtService;
import jakarta.mail.internet.MimeMessage;

/**
 * Service class for handling email-related operations.
 * Responsible for sending verification and password reset emails using HTML templates.
 */
@Service
public class EmailService {

    // Logger for logging information and errors
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    // The email address from which emails will be sent (configured in application properties)
    @Value("${spring.mail.username}")
    private String emailFrom;

    // Service for generating JWT tokens (used in email links)
    @Autowired
    JwtService jwtService;

    // Provides configuration values such as client URLs and expiration times
    @Autowired
    ResourceProvider provider;

    // Used to process HTML email templates
    @Autowired
    TemplateEngine templateEngine;

    // Used to send emails
    @Autowired
    JavaMailSender javaMailSender;

    /**
     * Sends a verification email to the user asynchronously.
     * The email contains a verification link with a JWT token.
     * Uses the "verify_email" HTML template.
     */
    @Async
    public void sendVerificationEmail(User user) {
        this.sendEmail(
                user,
                this.provider.getClientVerifyParam(), // Parameter name for verification
                "verify_email", // Template name
                this.provider.getMailVerificationSubject(), // Email subject from config
                this.provider.getClientVerifyExpiration() // Token expiration time
        );
    }

    /**
     * Sends a password reset email to the user asynchronously.
     * The email contains a reset link with a JWT token.
     * Uses the "reset_password" HTML template.
     */
    @Async
    public void sendResetPasswordEmail(User user) {
        this.sendEmail(
                user,
                this.provider.getClientResetParam(), // Parameter name for reset
                "reset_password", // Template name
                this.provider.getMailResetPasswordSubject(), // Email subject from config
                this.provider.getClientResetExpiration() // Token expiration time
        );
    }

    /**
     * Helper method to build and send an HTML email to the user.
     * Fills the template with user and token data, sets email details, and sends the email.
     *
     * @param user The user to whom the email is sent
     * @param clientParam The parameter name for the client (e.g., "verify" or "reset")
     * @param templateName The name of the Thymeleaf template to use
     * @param emailSubject The subject of the email
     * @param expiration The expiration time for the JWT token
     */
    private void sendEmail(User user, String clientParam, String templateName, String emailSubject, long expiration) {
        try {
            // Prepare the context for the HTML template
            Context context = new Context();
            context.setVariable("user", user); // Add user object to template
            context.setVariable("client", this.provider.getClientUrl()); // Add client URL
            context.setVariable("param", clientParam); // Add parameter name
            // Generate a JWT token for the user with the given expiration
            context.setVariable("token", this.jwtService.generateJwtToken(user.getUsername(), expiration));

            // Process the HTML template with the context data
            String process = this.templateEngine.process(templateName, context);

            // Create a MIME email message (for HTML content)
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            // Set the sender, subject, HTML body, and recipient
            helper.setFrom(this.emailFrom, "TCA User Manager"); // Using "TCA User Manager" as sender name
            helper.setSubject(emailSubject);
            helper.setText(process, true); // true = HTML content
            helper.setTo(user.getEmail());

            // Send the email
            this.javaMailSender.send(mimeMessage);

            // Log success
            this.logger.debug("Email Sent, {} ", user.getEmail());
        } catch (Exception ex) {
            // Log any errors that occur during email sending
            this.logger.error("Error while Sending Email, Username: " + user.getUsername(), ex);
        }
    }
}
