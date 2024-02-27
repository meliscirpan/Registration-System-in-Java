
import java.util.ArrayList;

public class Student {

    private String name;
    private StudentId studentId;
    private int registrationOrder;
    private int currentYear;
    private int semesterNumber;
    private Advisor advisor;
    private Schedule schedule;
    private Transcript transcript;
    private RegistrationSystem registrationSystem;
    private StringBuilder executionTrace = new StringBuilder();

    /**Constructor for regenerating student from the input file*/
    public Student(String name, String studentId, RegistrationSystem registrationSystem, int semesterNumber) {
        this.name = name;
        this.studentId = new StudentId(studentId, this);
        this.registrationSystem = registrationSystem;
        transcript = new Transcript(this);
        schedule = new Schedule(this);
        this.semesterNumber = semesterNumber;
    }

    /**Constructor for creating students from scratch*/
    public Student(String name, int currentYear, int registrationOrder, RegistrationSystem registrationSystem) {
        this.name = name;
        this.currentYear = currentYear;
        this.registrationOrder = registrationOrder;
        studentId = new StudentId(this);
        this.registrationSystem = registrationSystem;
        setSemesterNumber();
        transcript = new Transcript(this);
        schedule = new Schedule(this);
    }

    /**Gets the number of past elective courses of student by
     * looking at student's semester number and parameter's semester
     * numbers*/
    public int getNumOfPastElectives(ArrayList<Integer> semesterNums) {
        int count = 0;
        for (Integer i: semesterNums) {
            if (i < getSemesterNumber()) {
                count++;
            }
        }
        return count;
    }


    public void addToCurrentCourses(CourseSection courseSection) {
        schedule.addToProgram(courseSection);
        transcript.getCurrentCourses().add(courseSection.getCourse());
    }


    public void requestCourseSection(CourseSection courseSection) {
         advisor.approveCourseSection(this, courseSection);
    }

    public void requestCourses() {
        executionTrace.append("Current Semester Courses: \n");
        requestMandatoryCourses();
        requestElectiveCourses();
    }


    private void requestMandatoryCourses() {
        ArrayList<CourseSection> offeredCourseSections = registrationSystem.getOfferedMandatories(this);

        for (CourseSection c : offeredCourseSections) {
            executionTrace.append(c.getCourse().toString()).append(", ");
        }
        executionTrace.append("\n");
        executionTrace.append("(").append(registrationSystem.offeredNTECount(this)).append(" NTE), ");
        executionTrace.append("(").append(registrationSystem.offeredTECount(this)).append(" TE), ");
        executionTrace.append("(").append(registrationSystem.offeredFTECount(this)).append(" FTE), ");
        executionTrace.append("\n");
        for (CourseSection c: offeredCourseSections) {
            requestCourseSection(c);
        }
    }

    /**Calls the getOfferedElectives method and after getting offered electives randomly,
     **/
    private void requestElectiveCourses() {

        ArrayList<CourseSection> offeredCourses = registrationSystem.getOfferedElectives(this);

        for (CourseSection c: offeredCourses) {
            requestCourseSection(c);
        }
    }

    public StringBuilder getExecutionTrace() {
        return executionTrace;
    }

    public void setExecutionTrace(StringBuilder executionTrace) {
        this.executionTrace = executionTrace;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public void setSemesterNumber() {
        switch (registrationSystem.getSemester()) {
            case FALL:
                this.semesterNumber = currentYear * 2 - 1; break;
            case SPRING:
                this.semesterNumber = currentYear * 2; break;
            default:
                System.out.println("Incorrect Semester for registration System!!");
                System.exit(-1);
        }
    }


    public String getName() {
        return name;
    }

    public int getRegistrationOrder() {
        return registrationOrder;
    }

    public String getStudentId() {
        return studentId.getStudentId();
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public Advisor getAdvisor() {
        return advisor;
    }

    public void setAdvisor(Advisor advisor) {
        this.advisor = advisor;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public RegistrationSystem getRegistrationSystem() {
        return registrationSystem;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public String toString() {
        String studentStr = "Past Courses: \n" + transcript.toString() + "\n";

        return studentStr;
    }

}
