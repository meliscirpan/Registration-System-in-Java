import java.util.ArrayList;
import java.util.Collections;

public class FacultyTechnicalElectiveCourse extends ElectiveCourse{

    public FacultyTechnicalElectiveCourse(String courseCode, int quota, int credits, int theoretical,
                                          int practical, ArrayList<Integer> semesters) {
        super (courseCode, quota, credits, theoretical, practical, semesters);
    }


    @Override
    public void whenRejected(Student student) {
        if (getRegistrationSystem().isThereEmptyFacTechSection()) {
            student.getAdvisor().approveCourseSection(student, getRandomElective().getCourseSection());
            return;
        }
        student.getExecutionTrace().append("\nAll of the FTE course Sections are full");
    }


    @Override
    public Course getRandomElective() {
        ArrayList<FacultyTechnicalElectiveCourse> electiveCourses = new ArrayList<>(getRegistrationSystem().
                getFacultyElectiveCourses());
        electiveCourses.remove(this);
        int index = (int) (Math.random() * electiveCourses.size());
        return electiveCourses.get(index);
    }

    public String toString() {
        return super.toString() + "(FTE)";
    }
}
