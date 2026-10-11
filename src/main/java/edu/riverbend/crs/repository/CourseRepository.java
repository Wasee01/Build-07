package edu.riverbend.crs.repository;

import edu.riverbend.crs.domain.Course;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    List<Course> findAll();
    Optional<Course> findById(Long id);
    Course save(Course value);
    boolean deleteById(Long id);
    List<Course> findBySubject(String subject);
}
