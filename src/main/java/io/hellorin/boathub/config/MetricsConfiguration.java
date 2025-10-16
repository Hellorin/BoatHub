package io.hellorin.boathub.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for metrics and monitoring.
 * Enables automatic timing of REST endpoints and custom metrics collection.
 */
@Configuration
public class MetricsConfiguration {

    /**
     * Enables @Timed annotation support for method-level timing.
     * This allows automatic timing of REST controller methods.
     * 
     * @param registry The meter registry for collecting metrics
     * @return TimedAspect bean for method timing
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
