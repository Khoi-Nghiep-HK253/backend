# --- Build Stage ---
FROM eclipse-temurin:26-jdk AS builder
WORKDIR /app

# Copy gradle files for caching dependencies
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/

# Ensure gradlew has execution permission and download dependencies (offline/cache friendly)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy the rest of the source code
COPY src/ src/

# Build the boot jar, skipping tests
RUN ./gradlew bootJar -x test --no-daemon

# --- Run Stage ---
FROM eclipse-temurin:26-jre
WORKDIR /app

# Create a non-root user for security
RUN useradd -ms /bin/sh spring
USER spring

# Copy the generated JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 8080 (the default server.port)
EXPOSE 8080

# Environment variables defaults (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/divvy
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=123456

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
