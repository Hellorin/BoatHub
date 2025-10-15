package io.hellorin.boathub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Main Spring Boot application class for BoatHub.
 * This class serves as the entry point for the application.
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
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
