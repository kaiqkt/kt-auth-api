# kt-auth-api

## Description

This is a simple API that allows users to register and login. It uses JWT for authentication and authorization.
Has a management system for users roles and his respective sessions.


## Requirements

    - Requires JDK 17 installed
    - Requires Docker/Colima installed

## Running the application

    - Clone the repository
    - Run the command `./gradlew build` to build the application
    - Run the command `docker build -t kt-auth-api .` to build the docker image
    - Run the command `docker compose up` to start the application and his respective database and mail server
    - The application will be available at `http://localhost:8080` and the mail server at `http://localhost:8025`
    - The swagger with the documentation is available at `http://localhost:8080/swagger-ui.html`
