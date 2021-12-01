drop table if exists groups, students, courses, students_assignments cascade;
CREATE TABLE groups
(
    group_id   bigserial primary key,
    group_name varchar(50) unique not null
);

CREATE TABLE students
(
    student_id bigserial primary key,
    group_id   bigint      null,
    first_name varchar(50) not null,
    last_name  varchar(50) not null,
    foreign key (group_id) references groups (group_id)
);

CREATE TABLE courses
(
    course_id          bigserial primary key,
    course_name        varchar(50) unique not null,
    course_description text unique        not null
);

CREATE TABLE students_assignments
(
    student_id bigint not null,
    course_id  bigint not null,
    primary key (student_id, course_id),
    foreign key (student_id) references students (student_id),
    foreign key (course_id) references courses (course_id)
);