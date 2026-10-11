package edu.riverbend.crs.config;

import edu.riverbend.crs.domain.Student;
import edu.riverbend.crs.repository.StudentRepository;
import edu.riverbend.crs.repository.EnrollmentRepository;
import edu.riverbend.crs.repository.memory.InMemoryStudentRepository;
import edu.riverbend.crs.repository.memory.InMemoryEnrollmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {
    @Bean
    StudentRepository studentRepository() {
        StudentRepository repository = new InMemoryStudentRepository();
        repository.save(new Student(null, "Sample Student", "student@example.edu"));
        return repository;
    }

    @Bean
    EnrollmentRepository enrollmentRepository() {
        return new InMemoryEnrollmentRepository();
    }
}
