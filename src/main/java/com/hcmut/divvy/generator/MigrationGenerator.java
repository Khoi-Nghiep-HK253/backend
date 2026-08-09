package com.hcmut.divvy.generator;

import jakarta.persistence.Entity;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Prisma-like Migration Generator for Spring Boot + Flyway.
 *
 * Compares JPA Entities against the running PostgreSQL Database
 * and outputs ONLY the SQL diff (CREATE TABLE, ALTER TABLE, ADD COLUMN, etc.)
 * into a Flyway timestamped migration file (VYYYYMMDDHHMMSS__name.sql).
 *
 * Usage via Gradle:
 * .\gradlew migrateDev -Pname=create_core_tables
 * .\gradlew migrateDev -Pname=add_user_avatar
 */
public class MigrationGenerator {

    private static final String MIGRATION_DIR = "src/main/resources/db/migration";
    private static final String ENTITY_DIR = "src/main/java/com/hcmut/divvy/entity";
    private static final String BASE_PACKAGE = "com.hcmut.divvy.entity.";

    private static final String DB_URL = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/divvy");
    private static final String DB_USER = System.getProperty("db.user", "postgres");
    private static final String DB_PASS = System.getProperty("db.pass", "123456");

    public static void main(String[] args) {
        String migrationName = System.getProperty("migration.name");
        if (migrationName == null || migrationName.isBlank()) {
            migrationName = (args.length > 0 && !args[0].isBlank()) ? args[0] : "schema_update";
        }
        migrationName = migrationName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();

        System.out.println("Scanning JPA Entities...");
        List<Class<?>> entityClasses = scanEntityClasses();
        if (entityClasses.isEmpty()) {
            System.err.println("ERROR: No @Entity classes found in " + ENTITY_DIR);
            System.exit(1);
        }
        System.out.println("Found " + entityClasses.size() + " @Entity classes.");

        // 1. Prepare Flyway migration output path with timestamp
        Path dirPath = Paths.get(MIGRATION_DIR);
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to create migration directory: " + e.getMessage());
            System.exit(1);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = String.format("V%s__%s.sql", timestamp, migrationName);
        File outputFile = dirPath.resolve(fileName).toFile();

        // Temporary script path for Hibernate schema export
        File tempFile = new File(System.getProperty("java.io.tmpdir"), "hibernate_diff_" + timestamp + ".sql");

        // 2. Configure Hibernate settings connecting to PostgreSQL DB
        Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        settings.put("hibernate.connection.url", DB_URL);
        settings.put("hibernate.connection.username", DB_USER);
        settings.put("hibernate.connection.password", DB_PASS);
        settings.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        settings.put("hibernate.implicit_naming_strategy",
                "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl");

        // JPA Schema Generation properties to generate DDL script for diff update
        settings.put("jakarta.persistence.schema-generation.database.action", "none");
        settings.put("jakarta.persistence.schema-generation.scripts.action", "update");
        settings.put("jakarta.persistence.schema-generation.scripts.create-target", tempFile.getAbsolutePath());
        settings.put("hibernate.hbm2ddl.delimiter", ";");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        try {
            MetadataSources sources = new MetadataSources(registry);
            for (Class<?> clazz : entityClasses) {
                sources.addAnnotatedClass(clazz);
            }
            Metadata metadata = sources.buildMetadata();

            // Run Hibernate SchemaManagementToolCoordinator to compute diff against live DB
            SchemaManagementToolCoordinator.process(metadata, registry, settings, null);

        } catch (Exception e) {
            System.err.println("Error generating migration diff: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }

        // 3. Read generated DDL script and write to Flyway migration file if changes
        // exist
        try {
            List<String> rawLines = tempFile.exists() ? Files.readAllLines(tempFile.toPath()) : Collections.emptyList();
            tempFile.delete();

            List<String> sqlStatements = new ArrayList<>();
            for (String line : rawLines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    if (!trimmed.endsWith(";")) {
                        trimmed += ";";
                    }
                    sqlStatements.add(trimmed);
                }
            }

            if (sqlStatements.isEmpty()) {
                System.out.println("===============================================================================");
                System.out.println("No schema changes detected. Database is already up-to-date with JPA Entities.");
                System.out.println("===============================================================================");
                return;
            }

            List<String> outputLines = new ArrayList<>();
            outputLines.add("-- Flyway Migration: " + migrationName);
            outputLines.add("-- Auto-generated diff by MigrationGenerator (Prisma-like)");
            outputLines.add("-- Timestamp: " + timestamp);
            outputLines.add("");
            outputLines.addAll(sqlStatements);

            Files.write(outputFile.toPath(), outputLines);

            System.out.println("===============================================================================");
            System.out.println("PRISMA-LIKE MIGRATION GENERATED SUCCESSFULLY!");
            System.out.println("Migration File: " + outputFile.getAbsolutePath());
            System.out.println("Generated SQL Statements (" + sqlStatements.size() + "):");
            for (String sql : sqlStatements) {
                System.out.println("  -> " + sql);
            }
            System.out.println("===============================================================================");
            System.out.println("Next steps:");
            System.out.println("  1. Review the generated SQL in " + fileName);
            System.out.println("  2. Run '.\\gradlew bootRun' to automatically apply the migration via Flyway!");
            System.out.println("===============================================================================");

        } catch (Exception e) {
            System.err.println("Failed to process migration file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static List<Class<?>> scanEntityClasses() {
        List<Class<?>> entities = new ArrayList<>();
        File folder = new File(ENTITY_DIR);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".java"));
        if (files != null) {
            for (File file : files) {
                String className = BASE_PACKAGE + file.getName().replace(".java", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Entity.class)) {
                        entities.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Could not load class: " + className);
                }
            }
        }
        return entities;
    }
}
