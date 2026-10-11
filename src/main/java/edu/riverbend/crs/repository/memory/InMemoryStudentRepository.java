package edu.riverbend.crs.repository.memory;

import edu.riverbend.crs.domain.Student;
import edu.riverbend.crs.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryStudentRepository implements StudentRepository {
    private final ConcurrentHashMap<Long, Student> values = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public List<Student> findAll() {
        return values.values().stream().sorted(java.util.Comparator.comparing(Student::id)).toList();
    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(values.get(id));
    }

    @Override
    public Student save(Student value) {
        long id = value.id() == null ? sequence.incrementAndGet() : value.id();
        sequence.accumulateAndGet(id, Math::max);
        Student saved = new Student(id, value.name(), value.email());
        values.put(id, saved);
        return saved;
    }

    @Override
    public boolean deleteById(Long id) {
        return values.remove(id) != null;
    }


}
