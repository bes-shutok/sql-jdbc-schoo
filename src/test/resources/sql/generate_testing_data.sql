INSERT INTO groups (group_name) VALUES('Some group');
INSERT INTO courses (course_name, course_description) VALUES('Java', 'Java course');
INSERT INTO students (first_name, last_name) VALUES('John', 'Doe');
INSERT INTO students_assignments (student_id, course_id)
VALUES ((SELECT student_id FROM students WHERE first_name = 'John' AND last_name = 'Doe'),
        (SELECT course_id FROM courses WHERE course_name = 'Java'));