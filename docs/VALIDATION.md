# Validation evidence

Verified with Amazon Corretto Java 25.0.4 and Maven 3.9.11.

- `mvn clean verify`: BUILD SUCCESS, 14 tests, 0 failures, 0 errors, 0 skipped.
- 9 repository integration tests use a fresh H2 database built by all three real Flyway migrations.
- 5 ArchUnit dependency rules passed.
- Tests cover course/section CRUD and filters, injection payload binding, all foreign keys, required columns, duplicate enrollment, seat bounds and term windows.
- Packaged executable JAR started successfully. HTTP smoke checks passed for course listing, filtered sections and 404 ProblemDetail.
- Reviewed every JdbcClient SQL call: SQL is static, and variable values use named parameter binding.
- No remote repository was supplied, so GitHub pipeline execution/pushing was not performed. Workflow is included.
- Historical Week 4/Build 6 equivalence is unverified; see SERVICE_DIFF.md.
