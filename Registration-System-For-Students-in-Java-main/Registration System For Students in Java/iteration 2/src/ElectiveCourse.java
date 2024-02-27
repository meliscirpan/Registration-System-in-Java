import java.util.ArrayList;
import java.util.Collections;

public abstract class ElectiveCourse extends Course {


    private ArrayList<Integer> semesters; //Elective courses have more than one semester

    public ElectiveCourse(String courseCode, int quota, int credits, int theoretical,
                          int practical, ArrayList<Integer> semesters) {

        super(courseCode, quota, credits, theoretical, practical);
        this.semesters = semesters;
        setCourseSection(new CourseSection(this));
    }


    @Override
    public boolean onRequested(Student student) {
       if (!super.onRequested(student)) { //If there is a collision
            whenRejected(student);
            return false;
        }
        if (!getCourseSection().addStudent(student)) { //If Quota is full for elective
            whenRejected(student);
            return false;
        }

        return true;
    }

    public abstract void whenRejected(Student student);
    public abstract Course getRandomElective();

    public ArrayList<Integer> getSemesters() {
        return semesters;
    }

    public void setSemesters(ArrayList<Integer> semesters) {
        this.semesters = semesters;
    }
}
