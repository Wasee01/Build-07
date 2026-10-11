package edu.riverbend.crs.repository.jdbc;

import edu.riverbend.crs.domain.Section;
import edu.riverbend.crs.repository.SectionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcSectionRepository implements SectionRepository {
    private static final RowMapper<Section> MAPPER = (rs, rowNum) ->
            new Section(rs.getLong("id"), rs.getLong("course_id"), rs.getLong("term_id"), rs.getString("crn"), rs.getInt("capacity"), rs.getInt("seats_taken"));
    private final JdbcClient jdbc;

    public JdbcSectionRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Section> findAll() {
        return jdbc.sql("SELECT * FROM section ORDER BY id").query(MAPPER).list();
    }

    @Override
    public Optional<Section> findById(Long id) {
        return jdbc.sql("SELECT * FROM section WHERE id = :id")
                .param("id", id).query(MAPPER).optional();
    }

    @Override
    public Section save(Section value) {
        if (value.id() != null) {
            int changed = jdbc.sql("UPDATE section SET course_id = :course_id, term_id = :term_id, crn = :crn, capacity = :capacity, seats_taken = :seats_taken WHERE id = :id")
                .param("course_id", value.courseId())
                .param("term_id", value.termId())
                .param("crn", value.crn())
                .param("capacity", value.capacity())
                .param("seats_taken", value.seatsTaken())
                    .param("id", value.id()).update();
            if (changed != 1) {
                throw new IllegalArgumentException("Cannot update a missing section");
            }
            return value;
        }
        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO section (course_id, term_id, crn, capacity, seats_taken) VALUES (:course_id, :term_id, :crn, :capacity, :seats_taken)")
                .param("course_id", value.courseId())
                .param("term_id", value.termId())
                .param("crn", value.crn())
                .param("capacity", value.capacity())
                .param("seats_taken", value.seatsTaken())
                .update(keys, "id");
        long id = java.util.Objects.requireNonNull(keys.getKey()).longValue();
        return new Section(id, value.courseId(), value.termId(), value.crn(), value.capacity(), value.seatsTaken());
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbc.sql("DELETE FROM section WHERE id = :id").param("id", id).update() == 1;
    }

    @Override
    public List<Section> findByFilters(Long termId, Long courseId) {
        return jdbc.sql("""
                SELECT * FROM section
                WHERE (:termId IS NULL OR term_id = :termId)
                  AND (:courseId IS NULL OR course_id = :courseId)
                ORDER BY id
                """)
                .param("termId", termId, java.sql.Types.BIGINT)
                .param("courseId", courseId, java.sql.Types.BIGINT)
                .query(MAPPER).list();
    }
}
