# Carpool Java Starting Project

Spring Boot 4.1.2, Java 25 and Maven Wrapper. Dependencies: Spring Web MVC,
Security, OAuth2 Client, Data JPA, MariaDB Driver, Mail and Validation.

## Local Setup

Install JDK 25 and Docker Desktop, and start Docker Desktop. No separate Maven
or MariaDB installation is required. Run all commands from this project folder.

1. Copy the configuration template (only if `.env` does not already exist):

   ```powershell
   Copy-Item .env.example .env
   ```

2. Set the database passwords in `.env`. Keep the simple `KEY=value`
   format without quotes so Docker Compose and Spring can both read it.
   `.env` is ignored by Git. Each developer uses their own file.

3. Start the local database and phpMyAdmin, and wait for the database to become healthy:

   ```powershell
   docker compose up -d --wait
   docker compose ps
   ```

4. Build and test against that database:

   ```powershell
   .\mvnw.cmd clean test
   ```

5. Run Spring Boot:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

Open http://localhost:8085/ to see the backend confirmation message. No website
account or login is required during local development. Stop the application with Ctrl+C.

`APP_PROFILE=dev` enables open development access and disables the generated
login page and HTTP Basic authentication. CSRF protection remains enabled for
state-changing requests. This configuration is excluded when `prod` is active.
Security and OAuth2 Client dependencies remain for the future Microsoft Entra
integration, which must enforce staff-only access before a real pilot. No carpool
pages or feature API endpoints have been implemented yet.

For IntelliJ, use JDK 25 and the project root as the run configuration's working
directory so Spring finds `.env`.

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

There is no `schema.sql` or `data.sql` yet. Hibernate table generation and SQL
initialisation are disabled. The test executes `SELECT 1` against MariaDB to
verify the connection without requiring tables. The database must be running
for tests. Agree the schema and development reset behaviour before enabling scripts.

## View the Database in Your Browser

Docker Compose also starts phpMyAdmin at http://localhost:8086.
Sign in with `MARIADB_USER` (normally `carpool`) and `MARIADB_PASSWORD`
from your `.env` file. Database credentials are still required even though the
Java website has no development login. Select `carpool` in the sidebar to inspect tables or use the
SQL tab. There are no tables until the team adds the schema.

phpMyAdmin connects to `mariadb:3306` on the internal Docker network; IntelliJ
and Spring Boot connect through `localhost:3307`. Both access the same database.
Spring Boot does not need to be running to use phpMyAdmin.

To start or stop just the browser interface:

```powershell
docker compose up -d phpmyadmin
docker compose stop phpmyadmin
```

Change `PHPMYADMIN_PORT` in `.env` if port 8086 is occupied. The interface is
bound to this computer's loopback address for local development.

## Learning the Java Setup

Read these files in this order:

1. `DemoprojectApplication.java`: the main method starts Spring Boot.
   `@SpringBootApplication` lets Spring discover the application's components.
2. `DevelopmentHomeController.java`: `@RestController` sends a method's return
   value to the browser. `@GetMapping` connects a URL to a method. This example
   returns plain text; it does not render an HTML file.
3. `DevelopmentSecurityConfiguration.java`: `@Configuration` and `@Bean` supply
   settings to Spring. `@Profile` limits open access to development. The small
   lambdas configure Spring Security; production sign-in is still unfinished.
4. `DemoprojectApplicationTests.java`: `@Autowired` lets Spring supply an existing
   component instead of creating it with `new`. `JdbcTemplate` runs a small SQL
   query to check the connection. Future feature persistence will use JPA.
5. `DevelopmentAccessTests.java`: MockMvc sends requests through Spring during
   tests. Check the request, response status and content separately. The profile
   test checks exclusion of open development access under `prod`; it does not
   verify complete production security.
6. `BookingExample.java`: an ordinary Java exercise with a constructor, a set,
   validation and exceptions. It is separate from the web application and does
   not implement database transactions or safe concurrent booking.

Java style follows the existing OOP exercises: four spaces, opening braces on a
new line, explicit types, descriptive names and `this.` for instance fields and
methods. Tests use `testMethod_situation_expectedResult` names and separate setup,
action and assertions. Comments explain Spring concepts and reasons for checks.

### Practice Tasks

- Change the development home message, update its test and run the tests.
- Add a development GET endpoint such as `/api/example` returning a fixed message.
  Write a MockMvc test for its response status and content. Use fictional data.
- Run the standalone exercise and explain why duplicate bookings, full trips and
  the driver joining their own trip are rejected:

  ```powershell
  java src/main/java/com/examproject/demoproject/BookingExample.java
  ```

- Explain the difference between stored MariaDB data and the exercise's `HashSet`,
  and why that exercise cannot safely handle concurrent bookings.
- Trace a future HTML/JavaScript request through a controller, service and
  repository. Agree API fields with teammates before implementing the feature.

Run checks with `docker compose up -d --wait`, then `.\mvnw.cmd clean test`.
Database tests need Docker. MockMvc does not need a second running web server.

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
