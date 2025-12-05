# Construction Control App Backend

Spring Boot сервис для управления жизненным циклом строительства: выбор типового проекта, заявка на объект, согласование документов, контроль стройки (отчёты, камеры, чат) и финальное подписание.

## Быстрый старт
- Требования: Java 17, Maven, PostgreSQL.
- Сборка без тестов: `mvn -DskipTests package`
- Запуск: `mvn spring-boot:run` (конфигурация БД берётся из `application.yml`).

## Swagger / OpenAPI
Подключён springdoc-openapi. После запуска:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI спецификация: `http://localhost:8080/v3/api-docs`

## Docker / Docker Compose
В репозитории есть `Dockerfile` и `docker-compose.yml`.

Запуск всех сервисов (PostgreSQL + приложение + pgAdmin):
- `docker-compose up --build`

Что поднимается:
- `db` — PostgreSQL 15 (`postgres/postgres`, БД `construction_control`), порт `5432`.
- `app` — Spring Boot сервис на `:8080`, читает БД из env (`DB_HOST=db` и т.д.).
- `pgadmin` — pgAdmin 4 на `:5050` (логин `admin@example.com` / пароль `admin`).

Данные БД сохраняются в volume `pg_data`.

### Подключение к БД через pgAdmin
1) Открыть `http://localhost:5050`, логин: `admin@example.com`, пароль: `admin`.
2) Add New Server:
   - General / Name: `construction_db` (любое)
   - Connection:
     - Host: `db`
     - Port: `5432`
     - Maintenance DB: `construction_control`
     - Username: `postgres`
     - Password: `postgres` (Save Password: on)
3) В дереве: Servers → construction_db → Databases → construction_control → Schemas → public → Tables — там таблицы и данные (`data.sql` загружается при первом старте, если база пустая).
