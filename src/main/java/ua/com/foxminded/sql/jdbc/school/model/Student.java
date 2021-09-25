package ua.com.foxminded.sql.jdbc.school.model;

import java.util.Objects;

public class Student extends LongEntity {
    public static final String STUDENTS_TABLE_NAME = "students";
    public static final String STUDENT_ASSIGNMENT_TABLE_NAME = "students_assignments";
    public static final String STUDENT_ID = "student_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";

    private final String firstName;
    private final String lastName;
    private Long groupId;

    public Student(Long id, String firstName, String lastName, Long groupId) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupId = groupId;
    }

    public Student(String firstName, String lastName, Long groupId) {
        this(null, firstName, lastName, groupId);
    }

    public Student(String firstName, String lastName) {
        this(null, firstName, lastName, null);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        if (!super.equals(o)) return false;
        return firstName.equals(student.firstName) && lastName.equals(student.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName);
    }
}
