INSERT INTO course (subject, number, title, credits) VALUES
    ('CS', '101', 'Introduction to Programming', 3),
    ('CS', '205', 'Database Development', 3),
    ('MATH', '101', 'College Algebra', 3);
INSERT INTO term (name, registration_opens, registration_closes) VALUES
    ('Fall 2026', TIMESTAMP '2026-07-01 00:00:00', TIMESTAMP '2026-09-01 00:00:00');
INSERT INTO section (course_id, term_id, crn, capacity, seats_taken)
SELECT c.id, t.id, '10001', 30, 0 FROM course c CROSS JOIN term t
WHERE c.subject = 'CS' AND c.number = '101' AND t.name = 'Fall 2026';
INSERT INTO section (course_id, term_id, crn, capacity, seats_taken)
SELECT c.id, t.id, '10002', 25, 0 FROM course c CROSS JOIN term t
WHERE c.subject = 'CS' AND c.number = '205' AND t.name = 'Fall 2026';
INSERT INTO student (name, email) VALUES ('Sample Student', 'student@example.edu');
