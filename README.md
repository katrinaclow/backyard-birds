# Backyard Birds 🦜

## Overview
Backyard Birds is a web application for tracking and managing personal bird observations with eBird integration and mapping capabilities. The application allows users to record bird sightings, track weather conditions, and visualize observation data through an interactive interface.

## Features
- Record and manage bird observations
- Integration with eBird taxonomy and sighting data
- Location-based mapping of observations
- Weather condition tracking
- Statistical analysis and data visualization
- Photo upload capability (planned)
- Mobile-friendly interface

## Technology Stack
- Backend: Spring Boot 3.4.1 (Java 21)
- Frontend: React (planned)
- Database: PostgreSQL
- Build Tool: Maven
- Testing: JUnit 5 with AssertJ
- API Integration: eBird API
- Mapping: Leaflet.js/Google Maps (planned)

## Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Node.js and npm (for frontend, once implemented)

## Setup and Installation

### Database Setup
1. Install PostgreSQL if not already installed
2. Create a new database:
```sql
CREATE DATABASE backyardbirds;
-- For testing environment
CREATE DATABASE backyardbirds_test;
```

### Backend Setup
1. Clone the repository:
```bash
git clone [repository-url]
cd backyard-birds
```

2. Configure database connection in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/backyardbirds
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## Project Structure
```
backyard-birds/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/keclow/backyardbirds/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       ├── controller/
│   │   │       └── BackyardBirdsApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/keclow/backyardbirds/
│               └── model/
└── pom.xml
```

## Testing
Run the test suite with:
```bash
mvn test
```

For test coverage report:
```bash
mvn verify
```

## API Documentation
Once implemented, API documentation will be available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Development Guidelines
- Follow SOLID principles
- Write comprehensive unit tests
- Document all major components
- Use feature branches for development
- Maintain clear commit messages
- Follow Java code style guidelines

## License
[License Type] - See LICENSE.md for details

## Author
Katrina Clow

## Versioning
This project uses [SemVer](http://semver.org/) for versioning.

## Acknowledgments
- eBird API for taxonomy data
- Spring Boot team for the framework
- Contributors and maintainers

## Support
For support, please open an issue in the GitHub repository or contact the maintainers.