import java.util.ArrayList;

public class Transcript {

    private Student student;
    private ArrayList<Course> currentCourses = new ArrayList<>();
    private ArrayList<Grade> grades = new ArrayList<>();


    public Transcript(Student student) {
        this.student = student;
    }

    /**Adds past course as either failed or passed accoding
     * to the pass probability of the registration system*/
    public void addPastCourse(Course course) {
        if (Math.random() < student.getRegistrationSystem().getPassProbability()) {
            addPassedCourse(course);
        }
        else {
            addFailedCourse(course);
        }
    }

    /**Returns completed credits by iterating over every passed course*/
    public int getCompletedCredits() {
        int credits = 0;
        for (Course c: getPassedCourses()) {
            credits += c.getCredits();
        }
        return credits;
    }

    /**Returns passed courses of the student*/
    public ArrayList<Course> getPassedCourses() {
        ArrayList<Course> passedCourses = new ArrayList<>();
        for (Grade g : grades) {
            if (g.isPassed()) {
                passedCourses.add(g.getCourse());
            }
        }
        return passedCourses;
    }

    /**Takes a course as argument and checks if student
     * has passed that course by iterating over student's
     * grades*/
    public boolean hasPassedCourse(Course course) {
        if (course == null) { // If course is null, return true (Used for courses that have no prerequisite)
            return true;
        }
        return getPassedCourses().contains(course);
    }

    public boolean hasPassedCourses(ArrayList<Course> courses) {
        for (Course course: courses) {
            if (!hasPassedCourse(course)) {
                return false;
            }
        }
        return true;
    }

    /**Adds a passed course with a random grade that is between 50-100*/
    public void addPassedCourse(Course course) {
        int grade = (int) (Math.random() * 51) + 50; // random grade that is greater than 50
        grades.add(new Grade(course, grade));
    }

    /**Adds a failed course with random grade between 0-49*/
    public void addFailedCourse(Course course) {
        int grade = (int) (Math.random() * 50); //random grade between 0-49
        grades.add(new Grade(course, grade));
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<Grade> grades) {
        this.grades = grades;
    }

    public ArrayList<Course> getCurrentCourses() {
        return currentCourses;
    }

    public void setCurrentCourses(ArrayList<Course> currentCourses) {
        this.currentCourses = currentCourses;
    }

    public String toString() {
        String pastCourses = "";

        for (Grade g: grades) {
            pastCourses +=  g.getCourse().toString() + ": " + g.getLetterGrade() + "\n";
        }
        return pastCourses;
    }

}
