# Selmag
Demo project

## PostgreSQL, MongoDB

```shell
     docker compose up -d
```

## [Keycloak](https://www.keycloak.org/getting-started/getting-started-docker)

```shell
    docker run --name selmag-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v ./config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:25.0.1 start-dev --import-realm 
```

Use absolute path for keycloak config

### Users
Username: manager  
Password: 123  
Username: customer  
Password: 123

## Spring-RestDocs
Файлы (снипеты) генерируются в папке:

    target/generated-snippets

Чтобы увидеть все снипеты воедино, то есть документацию, есть файл:

    catalogue-service/src/asciidoc/documentation.adoc

На основе этого файла можно сгенерировать документацию в разных форматах (например pdf, html), используя плагин **asciidoctor-maven-plugin**.  
Документация генерируется в папке:

    target/generated-docs

## SpringDoc Open API

### Catalogue-service
http://localhost:8081/swagger-ui/index.html

### Feedback-service
http://localhost:8084/webjars/swagger-ui/index.html