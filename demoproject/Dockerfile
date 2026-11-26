# Build the Carpool application inside Docker, so a local JDK is optional.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
# Keep the wrapper executable even when the checkout came from Windows.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -B -q dependency:go-offline

COPY src src
# Run database integration tests separately with MariaDB running.
RUN ./mvnw -B -q package -DskipTests

# The running container needs the Java runtime, not the compiler or Maven.
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
