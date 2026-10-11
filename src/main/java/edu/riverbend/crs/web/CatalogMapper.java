package edu.riverbend.crs.web;

import edu.riverbend.crs.domain.Course;
import edu.riverbend.crs.domain.Section;
import edu.riverbend.crs.web.dto.CourseDto;
import edu.riverbend.crs.web.dto.SectionDto;

final class CatalogMapper {
    private CatalogMapper() {
    }

    static CourseDto toDto(Course c) {
        return new CourseDto(c.id(), c.subject(), c.number(), c.title(), c.credits());
    }

    static SectionDto toDto(Section s) {
        return new SectionDto(s.id(), s.courseId(), s.termId(), s.crn(), s.capacity(), s.seatsTaken());
    }
}
