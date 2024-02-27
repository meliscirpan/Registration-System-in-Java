public class Course {

    private String courseCode;
    private String courseName;
    private String semester;
    private String courseType;
    private int quota;
    private int credits;
    private int[] sectionHours = new int[2]; // {Theoretical, Practical}
    private int year;
    private int requiredCredits;
    private Course preRequisite;


    public Course(String courseCode, String courseName, String semester, String courseType, int quota,
                   int credits, int theoretical, int practical, int year, int requiredCredits, Course preRequisite) {

        this.courseCode = courseCode;
        this.courseName = courseName;
        this.semester = semester;
        this.courseType = courseType;
        this.quota = quota;
        this.credits = credits;
        setSectionHours(theoretical, practical);
        this.year = year;
        this.requiredCredits = requiredCredits;
        this.preRequisite = preRequisite;

    }



    public int getSectionHours() { //Returns the total section hours by summing theoretical and practical hours
        return sectionHours[0] + sectionHours[1];
    }

    public void setSectionHours(int theoretical, int practical) {
        this.sectionHours = new int[]{theoretical, practical};
    }

    public String getCourseCode() {
        return courseCode;
    }


    public String getCourseName() {
        return courseName;
    }



    public String getSemester() {
        return semester;
    }



    public int getQuota() {
        return quota;
    }


    public int getCredits() {
        return credits;
    }



    public Course getPreRequisite() {
        return preRequisite;
    }


    public String getCourseType() {
        return courseType;
    }



    public int getRequiredCredits() {
        return requiredCredits;
    }


}
