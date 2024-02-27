public class StudentId {

    private final String depCode = "1501";
    private Student student;
    private String studentId;


    public StudentId(Student student) {
        this.student = student;
        setStudentId();
    }

    public StudentId(String studentId, Student student) {
        this(student);
        this.studentId = studentId;
    }

    public void setStudentId() {
        this.studentId = depCode + getYearString() + getRegistrationString();
    }

    public String getStudentId() {
        return studentId;
    }

    private String getYearString() {
        int idYear = (2021 - student.getCurrentYear()) % 100;
        return String.valueOf(idYear);
    }

    private String getRegistrationString() {
        return String.format("%03d", student.getRegistrationOrder());
    }

    public String toString() {
        return getStudentId();
    }
}

