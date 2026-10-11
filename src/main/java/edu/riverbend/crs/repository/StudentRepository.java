package edu.riverbend.crs.repository;

import edu.riverbend.crs.domain.Student;
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    List<Student> findAll();
    Optional<Student> findById(Long id);
    Student save(Student value);
    boolean deleteById(Long id);

}
