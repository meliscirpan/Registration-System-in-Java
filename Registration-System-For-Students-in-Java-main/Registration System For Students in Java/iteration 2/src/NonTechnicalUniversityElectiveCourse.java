import java.util.ArrayList;
import java.util.Collections;

public class NonTechnicalUniversityElectiveCourse extends ElectiveCourse{




    public NonTechnicalUniversityElectiveCourse(String courseCode, int quota, int credits, int theoretical,
                                                int practical, ArrayList<Integer> semesters) {
        super (courseCode, quota, credits, theoretical, practical, semesters);
    }


    @Override
    public void whenRejected(Student student) {
        if (getRegistrationSystem().isThereEmptyNonTechSection()) {
            student.getAdvisor().approveCourseSection(student,getRandomElective().getCourseSection());
            return;
        }
        student.getExecutionTrace().append("\nAll of the NTE course Sections are full");
    }

    @Override
    public Course getRandomElective() {
        ArrayList<NonTechnicalUniversityElectiveCourse> electiveCourses = new ArrayList<>(getRegistrationSystem().
                getNontechElectiveCourses());
        electiveCourses.remove(this);
        int index = (int) (Math.random() * electiveCourses.size());
        return electiveCourses.get(index);
    }


    public String toString() {
        return super.toString() + "(NTE/UE)";
    }
}
