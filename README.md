# Employee API - README

This repository contains a Spring Boot application that provides a REST API for managing employees of a company.

## Technologies Used

The application is built using the following technologies and frameworks:

- Java 17
- Spring Boot 3.1
- Spring Data JPA
- Kafka
- MongoDb
- Swagger (OpenAPI)
- Spring Security
- Testcontainers
- Maven

## Requirements


For building and running the application you need:

- [Docker](https://www.docker.com/products/docker-desktop)
- [Docker-compose](https://docs.docker.com/compose/install/)

## Running the application locally

```shell
docker-compose up -d
```
```shell
./mvnw spring-boot:run
```

## Swagger API Spec
- [swagger](http://localhost:8080/swagger-ui/index.html)

## Testing on Local
To test the API endpoints locally, you need to use HTTP Basic Authentication with a test user.

The following test user is available for authentication:

- Username: testUser
- Password: test123
