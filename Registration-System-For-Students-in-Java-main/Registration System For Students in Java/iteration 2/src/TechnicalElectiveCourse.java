import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TechnicalElectiveCourse extends ElectiveCourse{

    private int requiredCredits;
    private ArrayList<Course> preRequisites;
    private Set<Student> nonRegisteredStudents = new HashSet<>();

    public TechnicalElectiveCourse(String courseCode, int quota, int credits, int theoretical, int practical,
                                   ArrayList<Integer> semesters, int requiredCredits, ArrayList<Course> preRequisites) {
        super(courseCode, quota, credits, theoretical, practical, semesters);
        this.requiredCredits = requiredCredits;
        this.preRequisites = preRequisites;
    }

    @Override
    public boolean isEligiblePastCourse(Student student) {
        return student.getTranscript().hasPassedCourses(this.getPreRequisites()) && checkCreditCondition(student)
                && super.isEligiblePastCourse(student);
    }

    @Override
    public void whenRejected(Student student) {
        if (getRegistrationSystem().isThereEmptyTechSection()) {
            student.requestCourseSection(getRandomElective().getCourseSection());
            return;
        }
        student.getExecutionTrace().append("\nAll of the TE course Sections are full");
    }

    @Override
    public Course getRandomElective() {
        ArrayList<TechnicalElectiveCourse> electiveCourses = new ArrayList<>(getRegistrationSystem().
                getTechElectiveCourses());
        electiveCourses.remove(this);
        int index = (int) (Math.random() * electiveCourses.size());
        return electiveCourses.get(index);
    }


    @Override
    public boolean onRequested(Student student) {
        if (!checkCreditCondition(student)){
            student.getExecutionTrace().append("\nThe system didn't allow " + toString() +
                    " because Student completed credits is less than " + requiredCredits +
                    " -> (" + student.getTranscript().getCompletedCredits() + ")");;
            nonRegisteredStudents.add(student);
            return false;
        }
        if (!student.getTranscript().hasPassedCourses(preRequisites)) {
            student.getExecutionTrace().append("\nThe system didn't allow " +  toString() +
                    " because student failed prerequisites -> " );
            for (Course c: preRequisites) {
                if (!student.getTranscript().hasPassedCourse(c)) {
                    student.getExecutionTrace().append(c.toString() + " ");
                }
            }
            return false;
        }

        return super.onRequested(student);
    }


    public boolean checkCreditCondition(Student student) {
         return student.getTranscript().getCompletedCredits() >= requiredCredits;
    }

    public int getRequiredCredits() {
        return requiredCredits;
    }

    public void setRequiredCredits(int requiredCredits) {
        this.requiredCredits = requiredCredits;
    }

    public ArrayList<Course> getPreRequisites() {
        return preRequisites;
    }

    public void setPreRequisites(ArrayList<Course> preRequisites) {
        this.preRequisites = preRequisites;
    }

    public Set<Student> getNonRegisteredStudents() {
        return nonRegisteredStudents;
    }

    public void setNonRegisteredStudents(Set<Student> nonRegisteredStudents) {
        this.nonRegisteredStudents = nonRegisteredStudents;
    }

    public String toString() {
        return super.toString() + "(TE)";
    }
}
