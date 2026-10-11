package edu.riverbend.crs.web;

import edu.riverbend.crs.service.CatalogService;
import edu.riverbend.crs.web.dto.CourseDto;
import edu.riverbend.crs.web.dto.SectionDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatalogController {
    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping("/courses")
    public List<CourseDto> courses(@RequestParam(required = false) String subject) {
        return service.courses(subject).stream().map(CatalogMapper::toDto).toList();
    }

    @GetMapping("/courses/{id}")
    public CourseDto course(@PathVariable Long id) {
        return CatalogMapper.toDto(service.course(id));
    }

    @GetMapping("/sections")
    public List<SectionDto> sections(@RequestParam(required = false) Long termId,
                                     @RequestParam(required = false) Long courseId) {
        return service.sections(termId, courseId).stream().map(CatalogMapper::toDto).toList();
    }

    @GetMapping("/sections/{id}")
    public SectionDto section(@PathVariable Long id) {
        return CatalogMapper.toDto(service.section(id));
    }
}
