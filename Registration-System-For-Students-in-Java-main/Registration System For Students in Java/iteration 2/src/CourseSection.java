import java.util.ArrayList;

public class CourseSection {

    private Course course;
    private RegistrationSystem registrationSystem;
    private boolean full;
    private int sectionHour;
    private ArrayList<Student> students;
    private boolean[][] courseProgram;

    public CourseSection(Course course) {
        this.course = course;
        setSectionHour();
        this.registrationSystem = RegistrationSystem.getInstance();
        students = new ArrayList<>();
        setFull();
        courseProgram = new boolean[Schedule.HOURS][Schedule.DAYS];
        setCourseProgram();
        //registrationSystem.getCourseSections().add(this);
    }

    /**Sets the courseProgram by adding all the lecture
     * hours to a random day and hour*/
    public void setCourseProgram() {
        for (int i = 0; i < sectionHour; i++) {
            int randomHour = (int)(Math.random() * Schedule.HOURS);
            int randomDay = (int)(Math.random() * Schedule.DAYS);

            //If course program is empty for that hour and same semester course has no lectures in that hour
            if (!courseProgram[randomHour][randomDay] && !CollidesWithSameSemester(randomHour, randomDay)) {
                courseProgram[randomHour][randomDay] = true;
            }else {
                i--;
            }
        }
    }

    private boolean CollidesWithSameSemester(int randomHour, int randomDay) {
        if (!(course instanceof MandatoryCourse)) {
            return false;
        }

        for (MandatoryCourse c: registrationSystem.getMandatoryCourses()) {
            if (((MandatoryCourse) course).getSemesterNumber() == c.getSemesterNumber() &&
                    c.getCourseSection().getCourseProgram()[randomHour][randomDay]) {
                return true;
            }
        }

        return false;
    }

    public boolean addStudent(Student student) {
        if (!full) {
            students.add(student);
            student.addToCurrentCourses(this);
            setFull();
            return true;
        }else {
            student.getExecutionTrace().append("\nThe system didn't allow " + course.toString() + " because " +
                    "course section is full. ("  +  students.size() + ")");
            course.getNonRegisteredQuota().add(student);
            return false;
        }
    }

    public int getQuota() {
        return course.getQuota();
    }

    public boolean isFull() {
        return full;
    }

    public Course getCourse() {
        return course;
    }

    public String getCourseSectionCode() {
        return getCourse().getCourseCode();
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public void setFull() {
        full = students.size() == getQuota();
    }



    public int getSectionHour() {
        return sectionHour;
    }

    public void setSectionHour() {
        sectionHour = course.getSectionHours();
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    public boolean[][] getCourseProgram() {
        return courseProgram;
    }

}
