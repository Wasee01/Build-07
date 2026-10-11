package edu.riverbend.crs;

import edu.riverbend.crs.domain.Course;
import edu.riverbend.crs.domain.Section;
import edu.riverbend.crs.repository.CourseRepository;
import edu.riverbend.crs.repository.SectionRepository;
import edu.riverbend.crs.repository.StudentRepository;
import edu.riverbend.crs.repository.EnrollmentRepository;
import edu.riverbend.crs.repository.jdbc.JdbcCourseRepository;
import edu.riverbend.crs.repository.jdbc.JdbcSectionRepository;
import edu.riverbend.crs.repository.memory.InMemoryStudentRepository;
import edu.riverbend.crs.repository.memory.InMemoryEnrollmentRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:repository-tests;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
@Transactional
class RepositoryIntegrationTest {
    @Autowired CourseRepository courses;
    @Autowired SectionRepository sections;
    @Autowired StudentRepository students;
    @Autowired EnrollmentRepository enrollments;
    @Autowired JdbcClient jdbc;
    @Autowired Flyway flyway;

    @Test
    void flywayBuildsAndSeedsAnEmptyDatabase() {
        assertThat(flyway.info().applied()).hasSize(3);
        assertThat(flyway.info().pending()).isEmpty();
        assertThat(courses.findAll()).hasSize(3);
        assertThat(sections.findAll()).hasSize(2);
        assertThat(courses).isInstanceOf(JdbcCourseRepository.class);
        assertThat(sections).isInstanceOf(JdbcSectionRepository.class);
        assertThat(students).isInstanceOf(InMemoryStudentRepository.class);
        assertThat(enrollments).isInstanceOf(InMemoryEnrollmentRepository.class);
    }

    @Test
    void courseCrudAndFiltersUseTheRealSchema() {
        Course saved = courses.save(new Course(null, "TEST", "900", "Testing", 3));
        assertThat(saved.id()).isPositive();
        assertThat(courses.findById(saved.id())).contains(saved);
        assertThat(courses.findBySubject("TEST")).containsExactly(saved);
        Course updated = new Course(saved.id(), "TEST", "900", "Updated", 4);
        courses.save(updated);
        assertThat(courses.findById(saved.id())).contains(updated);
        assertThat(courses.deleteById(saved.id())).isTrue();
        assertThat(courses.findById(saved.id())).isEmpty();
        assertThat(courses.deleteById(saved.id())).isFalse();
    }

    @Test
    void sectionCrudAndEveryFilterCombination() {
        Section seed = sections.findAll().getFirst();
        Section saved = sections.save(new Section(null, seed.courseId(), seed.termId(), "99999", 10, 0));
        assertThat(sections.findById(saved.id())).contains(saved);
        assertThat(sections.findByFilters(null, null)).contains(saved);
        assertThat(sections.findByFilters(seed.termId(), null)).contains(saved);
        assertThat(sections.findByFilters(null, seed.courseId())).contains(saved);
        assertThat(sections.findByFilters(seed.termId(), seed.courseId())).contains(saved);
        assertThat(sections.findByFilters(-1L, seed.courseId())).isEmpty();
        Section updated = new Section(saved.id(), saved.courseId(), saved.termId(), saved.crn(), 10, 1);
        sections.save(updated);
        assertThat(sections.findById(saved.id())).contains(updated);
        assertThat(sections.deleteById(saved.id())).isTrue();
        assertThat(sections.findById(saved.id())).isEmpty();
    }

    @Test
    void payloadsRemainLiteralValues() {
        assertThat(courses.findBySubject("CS' OR '1'='1")).isEmpty();
        assertThat(courses.findBySubject("CS'; DROP TABLE course; --")).isEmpty();
        Course saved = courses.save(new Course(null, "TEST", "901", "O'Brien'; DROP TABLE course; --", 3));
        assertThat(courses.findById(saved.id())).contains(saved);
        assertThat(courses.findById(-1L)).isEmpty();
        assertThat(courses.findBySubject("CS")).hasSize(2);
    }

    @Test
    void duplicateEnrollmentFailsOnDirectInsert() {
        long student = jdbc.sql("SELECT id FROM student ORDER BY id").query(Long.class).list().getFirst();
        long section = sections.findAll().getFirst().id();
        jdbc.sql("INSERT INTO enrollment (student_id, section_id) VALUES (:student, :section)")
                .param("student", student).param("section", section).update();
        assertThatThrownBy(() -> jdbc.sql("INSERT INTO enrollment (student_id, section_id) VALUES (:student, :section)")
                .param("student", student).param("section", section).update())
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("UQ_ENROLLMENT_STUDENT_SECTION");
    }

    @Test
    void seatsCannotExceedCapacityOrBecomeNegative() {
        Section section = sections.findAll().getFirst();
        assertThatThrownBy(() -> jdbc.sql("UPDATE section SET seats_taken = :seats WHERE id = :id")
                .param("seats", section.capacity() + 1).param("id", section.id()).update())
                .isInstanceOf(DataIntegrityViolationException.class).hasMessageContaining("CK_SECTION_SEATS");
        assertThatThrownBy(() -> jdbc.sql("UPDATE section SET seats_taken = :seats WHERE id = :id")
                .param("seats", -1).param("id", section.id()).update())
                .isInstanceOf(DataIntegrityViolationException.class);
        jdbc.sql("UPDATE section SET seats_taken = capacity WHERE id = :id").param("id", section.id()).update();
        assertThat(sections.findById(section.id()).orElseThrow().seatsTaken()).isEqualTo(section.capacity());
    }

    @Test
    void termWindowRejectsEqualAndReversedDates() {
        for (String closes : new String[] {"2026-01-01T00:00:00", "2025-12-31T00:00:00"}) {
            assertThatThrownBy(() -> jdbc.sql("INSERT INTO term (name, registration_opens, registration_closes) VALUES (:name, :opens, :closes)")
                    .param("name", "Invalid")
                    .param("opens", java.time.LocalDateTime.parse("2026-01-01T00:00:00"))
                    .param("closes", java.time.LocalDateTime.parse(closes)).update())
                    .isInstanceOf(DataIntegrityViolationException.class).hasMessageContaining("CK_TERM_REGISTRATION_WINDOW");
        }
    }

    @Test
    void allFourForeignKeysAreEnforced() {
        Section section = sections.findAll().getFirst();
        long student = jdbc.sql("SELECT id FROM student ORDER BY id").query(Long.class).list().getFirst();
        assertThatThrownBy(() -> jdbc.sql("UPDATE section SET course_id = :missing WHERE id = :id")
                .param("missing", -1).param("id", section.id()).update()).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.sql("UPDATE section SET term_id = :missing WHERE id = :id")
                .param("missing", -1).param("id", section.id()).update()).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.sql("INSERT INTO enrollment (student_id, section_id) VALUES (:student, :section)")
                .param("student", -1).param("section", section.id()).update()).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.sql("INSERT INTO enrollment (student_id, section_id) VALUES (:student, :section)")
                .param("student", student).param("section", -1).update()).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void requiredColumnsAreNotNullable() {
        int nullable = jdbc.sql("""
                SELECT COUNT(*) FROM information_schema.columns
                WHERE table_schema = 'PUBLIC'
                  AND table_name IN ('COURSE', 'TERM', 'SECTION', 'STUDENT', 'ENROLLMENT')
                  AND is_nullable = 'YES'
                """).query(Integer.class).single();
        assertThat(nullable).isZero();
        assertThatThrownBy(() -> jdbc.sql("INSERT INTO course (subject, number, title, credits) VALUES (:subject, :number, :title, :credits)")
                .param("subject", "TEST").param("number", "902")
                .param("title", null, java.sql.Types.VARCHAR).param("credits", 3).update())
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
