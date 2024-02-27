public class Transcript {

    private Student student;

    public Transcript(Student student) {
        this.student = student;
    }

    public int getCompletedCredits() {
        int credits = 0;
        for (Grade g: student.getGrades()) {
            credits += g.getCourse().getCredits();
        }
        return credits;
    }

}
