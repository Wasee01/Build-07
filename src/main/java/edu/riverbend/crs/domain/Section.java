package edu.riverbend.crs.domain;

public record Section(Long id, Long courseId, Long termId, String crn, int capacity, int seatsTaken) {
}
