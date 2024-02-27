public class Grade {

    private int intGrade;
    private Course course;

    public Grade(Course course, int intGrade) {
        this.intGrade = intGrade;
        this.course = course;
    }

    /**Returns true if int grade is greater than or equal to 50*/
    public boolean isPassed() {
        return intGrade >= 50;
    }

    /**Calculates letter grade from
     * int grade and returns a string
     * */
    public String getLetterGrade() {
        String letterGrade = "";
        if (intGrade < 0 || intGrade > 100) {
            System.out.println("Grades should be between 0-100!!");
            System.exit(-1);
        }
        else if (intGrade <= 44) {
            letterGrade =  "FF";
        }
        else if (intGrade <= 49) {
            letterGrade =  "FD";
        }
        else if (intGrade <= 54) {
            letterGrade = "DD";
        }
        else if (intGrade <= 64) {
            letterGrade = "DC";
        }
        else if (intGrade <= 74) {
            letterGrade = "CC";
        }
        else if (intGrade <= 79) {
            letterGrade = "CB";
        }
        else if (intGrade <= 84) {
            letterGrade = "BB";
        }
        else if (intGrade <= 89) {
            letterGrade = "BA";
        }
        else {
            letterGrade = "AA";
        }

        return letterGrade;
    }

    public int getIntGrade() {
        return intGrade;
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
