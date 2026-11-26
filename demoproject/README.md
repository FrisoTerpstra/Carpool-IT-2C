# Carpool Java Starting Project

Spring Boot 4.1.2, Java 25 and Maven Wrapper. Dependencies: Spring Web MVC,
Security, OAuth2 Client, Data JPA, MariaDB Driver, Mail and Validation.

## Local Setup

Install Docker Desktop and start it. Docker builds and runs Java, MariaDB and
phpMyAdmin. JDK 25 is needed only for IntelliJ/local Maven development and tests;
no separate Maven or MariaDB installation is required. Run commands from this
`demoproject` folder, where `docker-compose.yml` is located.

1. Copy the configuration template (only if `.env` does not already exist):

   ```powershell
   Copy-Item .env.example .env
   ```

2. Set the database passwords in `.env`. Keep the simple `KEY=value`
   format without quotes so Docker Compose and Spring can both read it.
   `.env` is ignored by Git. Each developer uses their own file.

3. Build and start the API, database and phpMyAdmin:

   ```powershell
   docker compose up -d --build
   docker compose ps
   ```

Open http://localhost:8085/ after the API has finished starting. The first build
downloads Java images and Maven dependencies and can take several minutes.
After pulling Java changes, run `docker compose up -d --build` again to rebuild
the image. Java source changes are not automatically reloaded into the container.

```powershell
docker compose logs --tail 50 api
docker compose stop
```

The API has no readiness health check yet. A running container is not proof the
application is ready; wait for `Started DemoprojectApplication` in its logs.

## Run Java from IntelliJ or Maven Instead

Use this workflow for editing and debugging Java with a local JDK 25.
Stop the API container first so it does not occupy the same port:

```powershell
docker compose stop api
docker compose up -d --wait mariadb phpmyadmin
.\mvnw.cmd spring-boot:run
```

Alternatively run `DemoprojectApplication` in IntelliJ, using the project root
as the working directory so it can read `.env`. Do not run the API container
and local Java on the same port. Stop the local application before switching
back to the full Docker setup.

The Dockerfile uses JDK 25 to build the JAR and a Java 25 runtime to execute it.
It does not run database integration tests during the image build. Run them
separately with the database available:

```powershell
docker compose up -d --wait mariadb
.\mvnw.cmd clean test
```

Open http://localhost:8085/ to see the backend confirmation message. No website
account or login is required during local development. Stop the application with Ctrl+C.

`APP_PROFILE=dev` enables open development access and disables the generated
login page and HTTP Basic authentication. CSRF protection remains enabled for
state-changing requests. This configuration is excluded when `prod` is active.
Security and OAuth2 Client dependencies remain for the future Microsoft Entra
integration, which must enforce staff-only access before a real pilot. No carpool
pages or feature API endpoints have been implemented yet.

## Database

MariaDB is available only locally at `localhost:3307`. The application uses the
non-root user from `.env`. Everyone gets an independent local database.
The image version is pinned in `docker-compose.yml` so the team uses the same version.

```powershell
docker compose logs --tail 50 mariadb
docker compose stop
```

The named Docker volume keeps data when the container stops or is recreated.
Changing passwords in `.env` does not change users in an existing database;
update those database users deliberately or recreate a disposable database.

### Add the Team's Schema Later

`database/init/` is mounted read-only at `/docker-entrypoint-initdb.d` in MariaDB.
Only `.gitkeep` is supplied to keep this empty directory in Git. No SQL file,
table definition or seed data has been added. The team can place its approved
`schema.sql` there later; a schema elsewhere in the repository is not loaded
automatically. Use the database name configured by `MARIADB_DATABASE`.

MariaDB runs these SQL files in filename order only when initialising a new,
empty database volume. Restarting existing containers or pulling an updated SQL
file does not apply schema changes to an existing volume. Agree how to apply
those changes separately, for example by importing reviewed SQL through
phpMyAdmin. Check whether a script deletes data before importing it. Do not
delete an existing volume just to apply an update unless its data is disposable.

Hibernate table generation and Spring SQL initialisation remain disabled, so
they do not compete with Docker's initialisation. The test executes `SELECT 1`
against MariaDB without requiring tables. Docker must be running for these tests.

## View the Database in Your Browser

Docker Compose also starts phpMyAdmin at http://localhost:8086.
Sign in with `MARIADB_USER` (normally `carpool`) and `MARIADB_PASSWORD`
from your `.env` file. Database credentials are still required even though the
Java website has no development login. Select `carpool` in the sidebar to inspect tables or use the
SQL tab. There are no tables until the team adds the schema.

phpMyAdmin and the API container connect to `mariadb:3306` inside Docker.
Java running in IntelliJ/Maven and database clients use `localhost:3307`.
The API's connection is supplied by Compose environment variables, overriding
the local connection in `application.properties`. Both workflows use the same database.
Spring Boot does not need to be running to use phpMyAdmin.

The API container listens on 8080 internally. `SERVER_PORT` in `.env` chooses
its published local port, normally 8085. For local Java, that same setting is
read by `application.properties`. `DB_PORT` and `PHPMYADMIN_PORT` choose the
published database and phpMyAdmin ports. Internal Docker ports stay unchanged.

To start or stop just the browser interface:

```powershell
docker compose up -d phpmyadmin
docker compose stop phpmyadmin
```

Change `PHPMYADMIN_PORT` in `.env` if port 8086 is occupied. The interface is
bound to this computer's loopback address for local development.

## Remaining Work

- Add the agreed schema, entities, services and controllers.
- Configure Entra app registration and staff eligibility with ICT. The OAuth2
  dependency alone does not enable Microsoft login. Keep credentials outside Git.
- Configure an approved mail server or local mail catcher before using
  `JavaMailSender`; the dependency alone does not send email.
- Keep CSRF protection when connecting session-authenticated frontend forms.

## Troubleshooting

- **Port in use:** change `DB_PORT` or `SERVER_PORT` in `.env`. Restart the
  affected service. Defaults avoid the other project's ports 3306 and 8080.
- **Database connection refused:** check Docker Desktop and `docker compose ps`.
- **Access denied:** check `.env` credentials and whether the volume was created
  with different passwords.
- **Java release error:** check `java -version` and `.\mvnw.cmd -version`; both
  should use JDK 25.
