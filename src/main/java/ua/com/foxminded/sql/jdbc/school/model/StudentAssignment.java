package ua.com.foxminded.sql.jdbc.school.model;

import java.util.Objects;

public record StudentAssignment(Long studentId, Long courseId) {
    public static final String TABLE_NAME = "students_assignments";

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
