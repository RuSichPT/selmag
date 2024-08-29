# Selmag
Demo project

## Инфраструктура

### PostgreSQL 
В проекте используется в качестве БД модуля каталога.

Запуск в Docker:

```shell
docker run --name catalogue-db -p 5432:5432 -e POSTGRES_USER=catalogue -e POSTGRES_PASSWORD=catalogue -e POSTGRES_DB=catalogue_db postgres:16
```
или через docker compose (см ниже)

### MongoDB
В проекте используется в качестве БД модуля обратной связи.

Запуск в Docker:

```shell
docker run --name feedback-db -p 27017:27017 mongo:7
```
или через docker compose (см ниже)

### [Keycloak](https://www.keycloak.org/getting-started/getting-started-docker)
В проекте используется как OAuth 2.0/OIDC-сервер для авторизации сервисов и аутентификации пользователей.

Запуск в Docker:  

```shell
    docker run --name selmag-keycloak -p 8082:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -v ${pwd}/config/keycloak/import:/opt/keycloak/data/import quay.io/keycloak/keycloak:25.0.1 start-dev --import-realm 
```
или через docker compose (см ниже)

#### Users
Username: manager  
Password: 123  
Username: customer  
Password: 123

### Victoria Metrics
В проекте используется для сбора метрик сервисов.

Запуск в Docker:  
(используйте абсолютный путь для конфигурации keycloak)

```shell
docker run --name selmag-metrics -p 8428:8428 -v ${pwd}/config/victoria-metrics/promscrape.yaml:/promscrape.yaml victoriametrics/victoria-metrics:v1.93.12 --promscrape.config=/promscrape.yaml
```
или через docker compose (см ниже)

### Grafana
В проекте используется для визуализации метрик, логов и трассировок.

Запуск в Docker:

```shell
docker run --name selmag-grafana -p 3000:3000 -v ${pwd}/data/grafana:/var/lib/grafana grafana/grafana:10.2.4
```
или через docker compose (см ниже)  

#### Адрес настройки

- Victoria Metrics http://host.docker.internal:8428
- Grafana Loki http://host.docker.internal:3100
- Tempo http://host.docker.internal:3200

### Grafana Loki
В проекте используется в качестве централизованного хранилища логов.

Запуск в Docker:

```shell
docker run --name selmag-loki -p 3100:3100 grafana/loki:2.9.4
```
или через docker compose (см ниже)

Для работы с Loki необходимо создать переменную среды 
LOKI=http://localhost:3100   
(admin-server, catalogue-service, customer-app, feedback-service, manager-app)

### Grafana Tempo

В проекте используется в качестве централизованного хранилища трассировок.

Запуск в Docker:

```shell
docker run --name selmag-tracing -p 3200:3200 -p 9095:9095 -p 4317:4317 -p 4318:4318 -p 9411:9411 -p 14268:14268 -v ${pwd}/config/tempo/tempo.yaml:/etc/tempo.yaml grafana/tempo:2.3.1 --config.file=/etc/tempo.yaml
```

### Docker compose

```shell
docker compose up -d
```

## SpringDoc Open API

### Catalogue-service
http://localhost:8081/swagger-ui/index.html

### Feedback-service
http://localhost:8084/webjars/swagger-ui/index.html

## Spring-RestDocs
Файлы (снипеты) генерируются в папке:

    target/generated-snippets

Чтобы увидеть все снипеты воедино, то есть документацию, есть файл:

    catalogue-service/src/asciidoc/documentation.adoc

На основе этого файла можно сгенерировать документацию в разных форматах (например pdf, html), используя плагин **asciidoctor-maven-plugin**.  
Документация генерируется в папке:

    target/generated-docs
