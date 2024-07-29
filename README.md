# selmag
Demo project

## PostgreSQL, MongoDB
     docker compose up -d

## [Keycloak](https://www.keycloak.org/getting-started/getting-started-docker)

    docker run --name selmag-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v ./config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:23.0.4 start-dev --import-realm 

Use absolute path for keycloak config

### Users
Username: manager  
Password: 123  
Username: customer  
Password: 123