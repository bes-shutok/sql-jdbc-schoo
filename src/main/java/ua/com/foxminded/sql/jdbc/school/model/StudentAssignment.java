package ua.com.foxminded.sql.jdbc.school.model;

import java.util.Objects;

public class StudentAssignment {
    public static final String TABLE_NAME = "students_assignments";

    private final Long studentId;
    private final Long courseId;

    public StudentAssignment(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentAssignment that)) return false;
        return getStudentId().equals(that.getStudentId()) && getCourseId().equals(that.getCourseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStudentId(), getCourseId());
    }
}
