import java.util.ArrayList;

public class Schedule {

    public static final int DAYS = 5;
    public static final int HOURS = 8;

    private CourseSection[][] program;
    private Student student;

    public Schedule (Student student) {
        this.student = student;
        program = new CourseSection[HOURS][DAYS];
    }

    /**Takes a courseSection as argument and adds it to
     * the schedule by taking its courseProgram into consideration*/
    public void addToProgram(CourseSection courseSection) {
        boolean[][] courseProgram = courseSection.getCourseProgram();

        for (int i = 0; i < HOURS; i++) {
            for (int j = 0; j < DAYS; j++) {

                if (courseProgram[i][j]) {
                    program[i][j] = courseSection;
                }
            }
        }
    }

    /**Takes a course section as argument and compares it with
     * current schedule, if there is more than 1 hour collision between
     * courseProgram and current schedule returns true, otherwise false */
    public ArrayList<CourseSection> getCollidedHours(CourseSection courseSection) {
        boolean[][] courseProgram = courseSection.getCourseProgram();
        ArrayList<CourseSection> collidedSections = new ArrayList<>();
        for (int i = 0; i < HOURS; i++) {
            for (int j = 0; j < DAYS; j++) {
                // If courseProgram and schedule has lectures in the same hour
                if (courseProgram[i][j] && program[i][j] != null) {
                    collidedSections.add(program[i][j]);
                }
            }
        }
        return collidedSections;
    }

    public boolean isCollision(CourseSection courseSection) {
        return getCollidedHours(courseSection).size() > 1;
    }

    public CourseSection[][] getProgram() {
        return program;
    }

    public void setProgram(CourseSection[][] program) {
        this.program = program;
    }
}
