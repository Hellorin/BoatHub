package io.hellorin.boathub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class for BoatHub.
 * This class serves as the entry point for the application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.hellorin.boathub.repository")
public class BoatHubApplication {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BoatHubApplication.class, args);
    }
}
