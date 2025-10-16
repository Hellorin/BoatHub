package io.hellorin.boathub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "io.hellorin.boathub.repository")
public class JpaConfiguration {
}
