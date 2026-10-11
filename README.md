# Schema and JDBC Repository

Standalone Week 7 course-registration catalog project. Spring Boot 4.0.4, Java 25, Maven 3.9+, H2 in PostgreSQL mode, Flyway, JdbcClient, JUnit, and ArchUnit.

## Run on Windows PowerShell

1. Install a JDK 25 and Apache Maven 3.9+, then add their bin directories to PATH.
2. Extract this archive. Open PowerShell inside the `schema-jdbc` folder (where `pom.xml` is).
3. Confirm `java -version` reports 25 and `mvn -version` uses that JDK.
4. Run `mvn clean verify`. This compiles, runs real-schema tests and architecture rules, and packages the executable JAR.
5. Run `mvn spring-boot:run`.
6. In a second PowerShell window, try:

```powershell
Invoke-RestMethod 'http://localhost:8080/api/courses'
Invoke-RestMethod 'http://localhost:8080/api/courses?subject=CS'
Invoke-RestMethod 'http://localhost:8080/api/sections?termId=1&courseId=1'
Invoke-RestMethod 'http://localhost:8080/api/courses/1'
```

Alternatively run `java -jar target/schema-jdbc-1.0.0.jar`. Stop with Ctrl+C. Data resets when the JVM stops.

## Implementation

- V1 creates course, term, and section; V2 creates student and enrollment; V3 seeds reference data. Flyway alone owns schema creation. No schema.sql, Hibernate, or test-only table creation.
- JDBC course and section repositories implement the repository contracts. All SQL is static and parameters are bound, including IDs, updates, filters, and deletes. Null filter values are explicitly typed.
- Student and enrollment repositories are in memory. The SQL student/enrollment tables exist for this week's schema tests and future JDBC conversion. The sample memory student is configured separately. There is deliberately no enrollment HTTP write flow combining memory enrollment and SQL seat counters.
- All four tables' relationships have foreign keys. Enrollment uniqueness, term window, and section seat checks run in the database. FK query paths have indexes; enrollment's composite unique index covers student_id lookups.
- Catalog HTTP endpoints return DTOs through hand-written mapping. Services depend on repository interfaces only.
- GitHub Actions runs `mvn clean verify` with Java 25 on pushes and pull requests. Add the files to your course repository and push; no remote repository was supplied for this deliverable.

## Important acceptance evidence limits

This is a new standalone project, as requested. The actual Week 4 interfaces and Build 6 source were not provided. The included interfaces use database-ready signatures, but exact identity with your original interfaces cannot be verified. Likewise, a historical Build 6 service diff cannot honestly be produced without that source. See `docs/SERVICE_DIFF.md` and the comparison script. This archive does not claim those historical criteria have been proved.

H2 PostgreSQL mode is only a compatibility aid, not a PostgreSQL integration test. Week 10 must add the PostgreSQL driver/Flyway database module and run the same migrations and tests against PostgreSQL. Do not edit applied migrations: add a new numbered migration.
