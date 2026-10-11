package edu.riverbend.crs.repository.memory;

import edu.riverbend.crs.domain.Enrollment;
import edu.riverbend.crs.repository.EnrollmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEnrollmentRepository implements EnrollmentRepository {
    private final ConcurrentHashMap<Long, Enrollment> values = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public List<Enrollment> findAll() {
        return values.values().stream().sorted(java.util.Comparator.comparing(Enrollment::id)).toList();
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return Optional.ofNullable(values.get(id));
    }

    @Override
    public Enrollment save(Enrollment value) {
        long id = value.id() == null ? sequence.incrementAndGet() : value.id();
        sequence.accumulateAndGet(id, Math::max);
        Enrollment saved = new Enrollment(id, value.studentId(), value.sectionId());
        values.put(id, saved);
        return saved;
    }

    @Override
    public boolean deleteById(Long id) {
        return values.remove(id) != null;
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        return findAll().stream().filter(e -> e.studentId().equals(studentId)).toList();
    }
}
