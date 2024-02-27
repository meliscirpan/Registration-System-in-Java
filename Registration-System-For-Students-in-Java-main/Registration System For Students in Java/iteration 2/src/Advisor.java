public class Advisor {

    private String name;

    public Advisor(String firstName) {
        this.name = firstName;
    }

    public void approveCourseSection(Student student, CourseSection courseSection) {
        courseSection.getCourse().onRequested(student);
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}

