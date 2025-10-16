# BoatHub

A full-stack web application for managing boat information with authentication and CRUD operations.

## Technologies Used

### Backend
- **Java 21** - Programming language
- **Spring Boot 3.5.6** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database for development
- **MapStruct 1.5.5** - Object mapping
- **SpringDoc OpenAPI 2.8.13** - API documentation with Swagger UI
- **Micrometer + Prometheus** - Application metrics and monitoring
- **Maven** - Build tool and dependency management
- **JaCoCo** - Code coverage analysis
- **SonarQube** - Code quality analysis

### Frontend
- **Vue.js 3.4.0** - Progressive JavaScript framework
- **TypeScript 5.3.3** - Type-safe JavaScript
- **Vite 5.0.10** - Build tool and development server
- **Vue Router 4.2.5** - Client-side routing
- **Pinia 2.1.7** - State management
- **Node.js 20.17.0** - JavaScript runtime

## Architecture

### Frontend + Backend Integration
BoatHub uses a **monolithic architecture** where Spring Boot serves both the API and the frontend:

1. **Frontend Build Process**: The Vue.js frontend is built using Vite and the resulting static files are automatically copied to Spring Boot's `src/main/resources/static/` directory during the Maven build process.

2. **Static File Serving**: Spring Boot serves the frontend static files through its embedded web server, configured in `WebConfig.java` to serve files from `classpath:/static/`.

3. **Client-Side Routing**: The application uses Vue Router for single-page application (SPA) routing. Spring Boot is configured to forward all non-API routes to `index.html` to support client-side routing.

4. **API Communication**: The frontend communicates with the backend through RESTful APIs. During development, Vite proxies API requests from port 3000 to the Spring Boot server on port 8080.

5. **Security Integration**: Spring Security handles authentication and CSRF protection, with the frontend integrated to work seamlessly with the backend security configuration.

### Key Components
- **Backend**: RESTful API with Spring Boot, serving boat management endpoints
- **Frontend**: Vue.js SPA with TypeScript for type safety
- **Database**: H2 in-memory database with predefined schema and sample data
- **Authentication**: Session-based authentication with Spring Security
- **API Documentation**: Swagger UI available at `/swagger-ui.html`

## How to Launch

### Frontend Only (Development)
To run only the frontend in development mode:

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:3000` with hot-reload enabled.

### Backend + Frontend (Full Application)
To run the complete application:

```bash
# Build and run the Spring Boot application with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

This will:
1. Install Node.js and npm dependencies
2. Build the Vue.js frontend
3. Copy the built frontend files to Spring Boot's static resources
4. Start the Spring Boot application

The application will be available at `http://localhost:8080` with:
- Frontend: `http://localhost:8080/`
- API Documentation: `http://localhost:8080/swagger-ui.html`
- Health Check: `http://localhost:8080/actuator/health`
- Prometheus Metrics: `http://localhost:8080/actuator/prometheus`

## Testing Data (on dev profile)

A test user is created in the dev profile to access the boat listing:
- Username: `owt`
- Password: `owt`

As for the list of boats, a sql script is executed at runtime to load some boats example

## CI/CD

The project includes a very simple GitHub Actions workflow for continuous integration and deployment. The workflow automatically:

- Runs tests and builds the application on every push and pull request
- Ensures code quality standards are maintained

## Code Quality (with SonarQube)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Hellorin_BoatHub&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Hellorin_BoatHub)

## TODO List

Here are the identified areas for future development:

### Database Integration Maintainability
- **Use Flyway or a similar framework**: Ensuring consistent and repeatable database updates across different environments (dev, staging, production) while tracking which changes have been applied.

### Security Enhancements
- **Rate Limiting**: Add rate limiting to API endpoints to prevent abuse and ensure fair usage

### Potential Future Improvements
- **Session persistence**: Use a cache to persist the session to avoid the users to have to log in after an application restart

- **Database Indexing**: Review and add database indexes as the application scales

- **Staging Configuration**: Complete stage environment setup
  - *Reason*: Ensure proper configuration for production deployment with HTTPS, secure cookies, and stage database

- **Production Configuration**: Complete production environment setup
  - *Reason*: Ensure proper configuration for production deployment with HTTPS, secure cookies, and production database

- **Performance Monitoring**: Implement comprehensive monitoring and metrics
  - *Reason*: Track application performance and identify optimization opportunities

- **Stress testing**: Use JMeter to validate Prometheus metrics under load and ensure the application scales with the expected audience size

###  Final Note

I utilized GitHub Copilot and Cursor’s AI-assisted coding features to enhance code quality and efficiency.
