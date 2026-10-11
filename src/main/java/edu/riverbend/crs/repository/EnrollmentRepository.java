package edu.riverbend.crs.repository;

import edu.riverbend.crs.domain.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    List<Enrollment> findAll();
    Optional<Enrollment> findById(Long id);
    Enrollment save(Enrollment value);
    boolean deleteById(Long id);
    List<Enrollment> findByStudentId(Long studentId);
}
