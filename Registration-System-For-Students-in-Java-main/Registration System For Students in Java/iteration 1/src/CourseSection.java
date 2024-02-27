import java.util.ArrayList;

public class CourseSection {

    private Course course;
    private boolean full;
    private int sectionHour;
    ArrayList<Student> students;
    private boolean[][] courseProgram;
    private int quotaStatistics;
    private int prerequisiteStatistics;
    private int collisionStatistics;

    public CourseSection(Course course) {
        this.course = course;
        setSectionHour();
        courseProgram = new boolean[Schedule.HOURS][Schedule.DAYS];
        students = new ArrayList<>();
        setCourseProgram();
    }

    /**Sets the courseProgram by adding all the lecture
     * hours to a random day and hour*/
    public void setCourseProgram() {
        for (int i = 0; i < sectionHour; i++) {
            int randomDay = (int)(Math.random() * Schedule.DAYS);
            int randomHour = (int)(Math.random() * Schedule.HOURS);

            if (!courseProgram[randomHour][randomDay]) {
                courseProgram[randomHour][randomDay] = true;
            }else {
                i--;
            }
        }
    }

    public void addStudent(Student student) {
        if (!isFull()) {
            students.add(student);
            student.addToCurrentCourses(this);
            if (getQuota() == students.size()) { // Set the full true if after the addition, course section is full
                setFull(true);
            }
        }else {
            student.setBuffer("\nThe system didn't allow " + getCourseSectionCode() + " because " +
                    "course section is full. ("  +  students.size() + ")");
            quotaStatistics++;
        }

    }

    public int getQuota() {
        return course.getQuota();
    }

    public boolean isFull() {
        return students.size() == getQuota();
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

    public int getCollisionStatistics() {
        return collisionStatistics;
    }

    public void setCollisionStatistics(int collisionStatistics) {
        this.collisionStatistics = collisionStatistics;
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



    public int getQuotaStatistics() {
        return quotaStatistics;
    }

    public void setQuotaStatistics(int quotaStatistics) {
        this.quotaStatistics = quotaStatistics;
    }

    public int getPrerequisiteStatistics() {
        return prerequisiteStatistics;
    }

    public void setPrerequisiteStatistics(int prerequisiteStatistics) {
        this.prerequisiteStatistics = prerequisiteStatistics;
    }

    public boolean[][] getCourseProgram() {
        return courseProgram;
    }


}
