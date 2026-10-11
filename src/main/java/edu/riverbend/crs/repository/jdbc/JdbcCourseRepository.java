package edu.riverbend.crs.repository.jdbc;

import edu.riverbend.crs.domain.Course;
import edu.riverbend.crs.repository.CourseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCourseRepository implements CourseRepository {
    private static final RowMapper<Course> MAPPER = (rs, rowNum) ->
            new Course(rs.getLong("id"), rs.getString("subject"), rs.getString("number"), rs.getString("title"), rs.getInt("credits"));
    private final JdbcClient jdbc;

    public JdbcCourseRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Course> findAll() {
        return jdbc.sql("SELECT * FROM course ORDER BY id").query(MAPPER).list();
    }

    @Override
    public Optional<Course> findById(Long id) {
        return jdbc.sql("SELECT * FROM course WHERE id = :id")
                .param("id", id).query(MAPPER).optional();
    }

    @Override
    public Course save(Course value) {
        if (value.id() != null) {
            int changed = jdbc.sql("UPDATE course SET subject = :subject, number = :number, title = :title, credits = :credits WHERE id = :id")
                .param("subject", value.subject())
                .param("number", value.number())
                .param("title", value.title())
                .param("credits", value.credits())
                    .param("id", value.id()).update();
            if (changed != 1) {
                throw new IllegalArgumentException("Cannot update a missing course");
            }
            return value;
        }
        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO course (subject, number, title, credits) VALUES (:subject, :number, :title, :credits)")
                .param("subject", value.subject())
                .param("number", value.number())
                .param("title", value.title())
                .param("credits", value.credits())
                .update(keys, "id");
        long id = java.util.Objects.requireNonNull(keys.getKey()).longValue();
        return new Course(id, value.subject(), value.number(), value.title(), value.credits());
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbc.sql("DELETE FROM course WHERE id = :id").param("id", id).update() == 1;
    }

    @Override
    public List<Course> findBySubject(String subject) {
        return jdbc.sql("SELECT * FROM course WHERE subject = :subject ORDER BY id")
                .param("subject", subject).query(MAPPER).list();
    }
}
