package edu.riverbend.crs.web.dto;

public record SectionDto(Long id, Long courseId, Long termId, String crn, int capacity, int seatsTaken) {
}
