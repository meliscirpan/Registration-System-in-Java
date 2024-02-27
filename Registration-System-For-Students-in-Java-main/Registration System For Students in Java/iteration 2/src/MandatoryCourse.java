import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MandatoryCourse extends Course {

    private float semesterNumber;
    private Semester semester;
    private Set<Student> nonRegisteredPrereq = new HashSet<>();//Students that couldn't register because of prerequisite condition
    private ArrayList<Course> preRequisites; //Every mandatory course has a prerequisite course

    public MandatoryCourse(String courseCode, float semester, int quota,
                  int credits, int theoretical, int practical, ArrayList<Course> preRequisites) {

        super(courseCode, quota, credits, theoretical, practical);
        setSemesterNumber(semester);
        this.preRequisites = preRequisites;
        setSemester();
        setCourseSection(new CourseSection(this));
    }

    @Override
    public boolean isEligiblePastCourse(Student student) {
        return student.getTranscript().hasPassedCourses(this.getPreRequisites()) &&
                student.getSemesterNumber() > this.getSemesterNumber() && super.isEligiblePastCourse(student);
    }


    public boolean isOfferableForStudent(Student student) {
        return !student.getTranscript().hasPassedCourse(this) && (student.getSemesterNumber() == getSemesterNumber()
                || (student.getSemesterNumber() > getSemesterNumber() && getRegistrationSystem().getSemester()
                == getSemester()));
    }


    @Override
    public boolean onRequested(Student student) {
        if (!student.getTranscript().hasPassedCourses(preRequisites)) {
            student.getExecutionTrace().append("\nThe system didn't allow " +  toString() +
                    " because student failed prerequisites -> " );
            for (Course c: preRequisites) {
                if (!student.getTranscript().hasPassedCourse(c)) {
                    student.getExecutionTrace().append(c.toString() + " ");
                }
            }
            nonRegisteredPrereq.add(student);
            return false;
        }
        if (!super.onRequested(student)) { //If there is collision
            return false;
        }

        if (!getCourseSection().addStudent(student)) {//If there is quota problem
            return false;
        }
        return true;

    }



    public void setSemesterNumber(float semesterNumber) {
        if (semesterNumber < 0 || semesterNumber > 8) {
            System.out.println("Incorrect semester for the mandatory course!!");
            System.exit(-1);
        }
        this.semesterNumber = semesterNumber;
    }

    public float getSemesterNumber() {
        return semesterNumber;
    }

    public Semester getSemester() {
        return semester;
    }

    public Semester setSemester() {
        int multipliedSemester = (int) (this.semesterNumber * 10); //We multiply the semesters by 10 and cast to int to avoid float arithmetic errors

        if (multipliedSemester % 10 == 5) { //If semester multiplied by 10 ends with 5, its semester is summer.
            semester = Semester.SUMMER;
        }
        else if ((multipliedSemester / 10) % 2 == 1) {
            semester = Semester.FALL;
        }
        else  {
            semester = Semester.SPRING;
        }

        return semester;
    }

    public ArrayList<Course> getPreRequisites() {
        return preRequisites;
    }

    public void setPreRequisites(ArrayList<Course> preRequisites) {
        this.preRequisites = preRequisites;
    }

    public Set<Student> getNonRegisteredPrereq() {
        return nonRegisteredPrereq;
    }

    public void setNonRegisteredPrereq(Set<Student> nonRegisteredPrereq) {
        this.nonRegisteredPrereq = nonRegisteredPrereq;
    }
}
