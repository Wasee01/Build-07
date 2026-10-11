package edu.riverbend.crs.repository;

import edu.riverbend.crs.domain.Section;
import java.util.List;
import java.util.Optional;

public interface SectionRepository {
    List<Section> findAll();
    Optional<Section> findById(Long id);
    Section save(Section value);
    boolean deleteById(Long id);
    List<Section> findByFilters(Long termId, Long courseId);
}
