package io.hellorin.boathub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * Restricts cross-origin requests to only allow requests from the application's own domain.
     * This is a security measure to prevent unauthorized cross-origin access.
     *
     * @param registry The CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        "http://localhost:8080",           // Local development (backend)
                        "http://localhost:3000",           // Local development (frontend)
                        "http://localhost:5173"            // Vite dev server
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight response for 1 hour
    }

    /**
     * Configures resource handlers for static content and Swagger UI.
     * Sets up routing for static resources and disables caching for development.
     *
     * @param registry The resource handler registry to configure
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);

        // Add Swagger UI resources
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .setCachePeriod(0);
    }

    /**
     * Configures view controllers for client-side routing support.
     * Maps specific paths to forward to the main index.html file to enable
     * single-page application routing.
     *
     * @param registry The view controller registry to configure
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all routes to index.html for client-side routing
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/boats").setViewName("forward:/index.html");
    }
}
