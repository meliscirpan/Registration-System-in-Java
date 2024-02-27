public class Advisor {

    private String firstName;
    private String lastName;


    public Advisor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void approveCourse(Student student, CourseSection courseSection) {
        if (!checkCredits(student, courseSection)) {
            student.setBuffer("\nCredit condition isn't satisfied!!");
        }else if (!checkPrerequisite(student, courseSection)) {
            student.setBuffer("\nThe system didn't allow " + courseSection.getCourseSectionCode() +
                    " because student failed prerequisite -> " + courseSection.getCourse().getPreRequisite().getCourseCode());
            courseSection.setPrerequisiteStatistics(courseSection.getPrerequisiteStatistics() + 1);
        }else if (checkCollision(student, courseSection)) {
            courseSection.setCollisionStatistics(courseSection.getCollisionStatistics() + 1);
        }
        else {
            courseSection.addStudent(student);
        }
    }

    /**Checks if the prerequisite condition is satisfied by the student.
     * Returns true if prerequisite condition is met*/
    private boolean checkPrerequisite(Student student, CourseSection courseSection) {
        if (courseSection.getCourse().getPreRequisite() == null) {
            return true;
        }

        return (student.hasPassedCourse(courseSection.getCourse().getPreRequisite()));

    }

    /**Checks if the credit condition of the course is satisfied by the student.
     * returns true if condition is satisfied*/
    private boolean checkCredits(Student student, CourseSection courseSection) {
        if (student.getTranscript().getCompletedCredits() >= courseSection.getCourse().getRequiredCredits()) {
            return true;
        }
        return false;
    }

    /**Checks if the student schedule has a collision with the requested course
     * Section by invoking the collision check method inside student's schedule.
     * Returns true if there is more than one hour collision*/
    private boolean checkCollision(Student student, CourseSection courseSection) {
        return student.getSchedule().isCollision(courseSection);
    }

    public String getFirstName () {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

