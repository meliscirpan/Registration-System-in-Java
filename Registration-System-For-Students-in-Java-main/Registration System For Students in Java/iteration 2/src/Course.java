import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class Course {

    private String courseCode;
    private int quota;
    private int credits;
    private int theoretical;
    private int practical;
    private CourseSection courseSection;
    private RegistrationSystem registrationSystem;
    private Set<Student> nonRegisteredCollision = new HashSet<>();
    private Set<Student> nonRegisteredQuota = new HashSet<>();


    protected Course(String courseCode, int quota,
                   int credits, int theoretical, int practical) {

        this.courseCode = courseCode;
        this.quota = quota;
        this.credits = credits;
        this.theoretical = theoretical;
        this.practical = practical;
        registrationSystem = RegistrationSystem.getInstance(); // Singleton Controller class
    }

    /**Returns true if student hasn't passed this course
     * (common for all of the course types)*/
    public boolean isEligiblePastCourse(Student student) {
        return !student.getTranscript().hasPassedCourse(this);
    }


    /**Checks for collision first when requested for all of the courses*/
    public boolean onRequested(Student student) {
        ArrayList<CourseSection> collidedSections = student.getSchedule().getCollidedHours(courseSection);
        if (student.getSchedule().isCollision(courseSection)) {
            student.getExecutionTrace().append("\nAdvisor didn't approve " + toString() +
                    " because of more than one hour collision with -> ");

            collidedSections.forEach(c -> student.getExecutionTrace().append(c.getCourse().toString() + " "));
            student.getExecutionTrace().append(" in schedule");
            nonRegisteredCollision.add(student);
            return false; //return false if there is a problem
        }
        return true;
    }


    public int getSectionHours() { //Returns the total section hours by summing theoretical and practical hours
        return theoretical + practical;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getQuota() {
        return quota;
    }

    public int getCredits() {
        return credits;
    }

    public int getTheoretical() {
        return theoretical;
    }

    public int getPractical() {
        return practical;
    }

    public RegistrationSystem getRegistrationSystem() {
        return registrationSystem;
    }

    public CourseSection getCourseSection() {
        return courseSection;
    }

    public Set<Student> getNonRegisteredCollision() {
        return nonRegisteredCollision;
    }

    public void setNonRegisteredCollision(Set<Student> nonRegisteredCollision) {
        this.nonRegisteredCollision = nonRegisteredCollision;
    }

    public void setCourseSection(CourseSection courseSection) {
        this.courseSection = courseSection;
    }

    public Set<Student> getNonRegisteredQuota() {
        return nonRegisteredQuota;
    }

    public void setNonRegisteredQuota(Set<Student> nonRegisteredQuota) {
        this.nonRegisteredQuota = nonRegisteredQuota;
    }

    public String toString() {
        return courseCode;
    }
}
