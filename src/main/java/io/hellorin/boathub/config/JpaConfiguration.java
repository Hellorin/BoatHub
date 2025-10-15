package io.hellorin.boathub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Profile("!test")
@Configuration
@EnableJpaRepositories(basePackages = "io.hellorin.boathub.repository")
public class JpaConfiguration {
}
