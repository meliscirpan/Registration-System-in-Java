import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FinalProjectMandatoryCourse extends MandatoryCourse {

    private int requiredCredits;
    private Set<Student> nonRegisteredCredit = new HashSet<>();

    public FinalProjectMandatoryCourse(String courseCode, float semester, int quota, int credits,
                                       int theoretical, int practical, ArrayList<Course> preRequisites, int requiredCredits) {
        super (courseCode, semester, quota, credits, theoretical, practical, preRequisites);
        this.requiredCredits = requiredCredits;
    }

    @Override
    public boolean isEligiblePastCourse(Student student) {
        //Returns true if student completed the prerequisite course and has enough credits to take this course
        return super.isEligiblePastCourse(student) && checkReqCredits(student);

    }


    @Override
    public boolean onRequested(Student student) {
        if (!checkReqCredits(student)){
            student.getExecutionTrace().append("\nThe system didn't allow " + toString() +
                     " because Student completed credits is less than " + requiredCredits +  "-> (" +
                     student.getTranscript().getCompletedCredits() + ")");
            getNonRegisteredCredit().add(student);
            return false;
        }else {
            return super.onRequested(student);
        }   
    }

    public int getRequiredCredits() {
        return requiredCredits;
    }

    private boolean checkReqCredits(Student student) {
        return student.getTranscript().getCompletedCredits() >= requiredCredits;
    }

    public void setRequiredCredits(int requiredCredits) {
        this.requiredCredits = requiredCredits;
    }

    public Set<Student> getNonRegisteredCredit() {
        return nonRegisteredCredit;
    }

    public void setNonRegisteredCredit(Set<Student> nonRegisteredCredit) {
        this.nonRegisteredCredit = nonRegisteredCredit;
    }

    public String toString() {
        return super.toString() + "(Final Project)";
    }
}
