package edu.riverbend.crs.service;

import edu.riverbend.crs.domain.Course;
import edu.riverbend.crs.domain.Section;
import edu.riverbend.crs.repository.CourseRepository;
import edu.riverbend.crs.repository.SectionRepository;
import edu.riverbend.crs.support.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final CourseRepository courses;
    private final SectionRepository sections;

    public CatalogService(CourseRepository courses, SectionRepository sections) {
        this.courses = courses;
        this.sections = sections;
    }

    public List<Course> courses(String subject) {
        return subject == null ? courses.findAll() : courses.findBySubject(subject);
    }

    public Course course(Long id) {
        return courses.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
    }

    public List<Section> sections(Long termId, Long courseId) {
        return sections.findByFilters(termId, courseId);
    }

    public Section section(Long id) {
        return sections.findById(id).orElseThrow(() -> new NotFoundException("Section not found"));
    }
}
