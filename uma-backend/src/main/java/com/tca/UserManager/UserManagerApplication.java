package com.tca.UserManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main class for the UserManager Spring Boot application.
 *
 * - @SpringBootApplication marks this as a Spring Boot application.
 * - @EnableAsync enables asynchronous method execution (for example, sending emails in the background).
 */
@EnableAsync // Enables support for @Async methods in the application
@SpringBootApplication // Marks this as a Spring Boot application and triggers auto-configuration and component scanning
public class UserManagerApplication {
    /**
     * The main method that starts the Spring Boot application.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserManagerApplication.class, args);
    }
}





//package com.tca.UserManager;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class UmaBackendApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(UmaBackendApplication.class, args);
//	}
//
//}
