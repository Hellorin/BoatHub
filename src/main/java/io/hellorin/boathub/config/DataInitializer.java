package io.hellorin.boathub.config;

import io.hellorin.boathub.domain.UserEntity;
import io.hellorin.boathub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer that runs at application startup.
 * Creates default users for testing and development purposes.
 */
@Component
@Profile("dev") // We don't want to run this in stage or production
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Initializes default users at application startup.
     * Creates admin and user accounts if they don't already exist.
     *
     * @param args command line arguments
     * @throws Exception if initialization fails
     */
    @Override
    public void run(String... args) throws Exception {
        createUserIfNotExists("admin", "admin", "Administrator account");
        createUserIfNotExists("user", "user", "Regular user account");
    }

    /**
     * Creates a user if it doesn't already exist.
     *
     * @param username the username
     * @param password the plain text password (will be encoded)
     * @param description description for logging
     */
    private void createUserIfNotExists(String username, String password, String description) {
        if (!userRepository.existsByUsername(username)) {
            String encodedPassword = passwordEncoder.encode(password);
            UserEntity user = new UserEntity(username, encodedPassword);
            userRepository.save(user);
            System.out.println("Created user: " + username + " (" + description + ")");
        } else {
            System.out.println("User already exists: " + username);
        }
    }
}
