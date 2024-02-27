import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*Note: Before running the program, make sure to delete Students folder to reset all the students*/

public class RegistrationSystem {

    private static RegistrationSystem registrationSystem = null;
    private boolean isRegenerate;
    private Semester semester;
    private int[] totalStudents = new int[4]; //Total students for each year(used in student id class)
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Advisor> advisors = new ArrayList<>();
    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<MandatoryCourse> mandatoryCourses = new ArrayList<>();
    private ArrayList<FinalProjectMandatoryCourse> finalProjectMandatoryCourses = new ArrayList<>();
    private ArrayList<NonTechnicalUniversityElectiveCourse> nontechElectiveCourses = new ArrayList<>();
    private ArrayList<TechnicalElectiveCourse> techElectiveCourses = new ArrayList<>();
    private ArrayList<FacultyTechnicalElectiveCourse> facultyElectiveCourses = new ArrayList<>();
    private double passProbability;
    private int advisorCount;
    private ArrayList<Integer> nonTechElectiveSemesters = new ArrayList<>();
    private ArrayList<Integer> techElectiveSemesters = new ArrayList<>();
    private ArrayList<Integer> facTechElectiveSemesters = new ArrayList<>();
    private String statisticsBuffer = "";

    private RegistrationSystem() { //Prevent instantiation

    }

    public static RegistrationSystem getInstance() {
        if (registrationSystem == null) {
            registrationSystem = new RegistrationSystem();
        }
        return registrationSystem;
    }

    public void startTheSimulation() {
        readInput();
        regenerateCheck();
        requestCourses();
        printRegistrationProcess();
        printStatistics();
        registrationProcessOutput();
        statisticsOutput();
    }

    private void regenerateCheck() {
        if (isRegenerate) {
            readStudents();
        } else {
            initializeAdvisors();
            appointAdvisors();
            addPastCourses();
        }
    }

    private void readStudents() {
        File folder = new File("Students/");
        File[] listOfFiles = folder.listFiles();
        if (!folder.exists()) { //If there is no student to read
            System.out.println("You must have Students folder before regenerating students!!!!!(change it to false first)");
            System.exit(-1);
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject input = (JSONObject) parser.parse(new FileReader("Students/" + file.getName()));

                    String name = (String) input.get("StudentName");
                    String studentId = (String) input.get("StudentId");
                    String advisorName = (String) input.get("AdvisorName");
                    int semesterNum = (int) (long) input.get("SemesterNumber");
                    int completedCredits = (int) (long) input.get("CompletedCredits");

                    Advisor newAdvisor = new Advisor(advisorName);
                    advisors.add(newAdvisor);
                    Student newStudent = new Student(name, studentId, this, 2);
                    newStudent.setAdvisor(newAdvisor);

                    if (completedCredits == 258 || ((getSemester() == Semester.FALL && semesterNum % 2 == 1) || (getSemester() == Semester.SPRING && semesterNum % 2 == 0))) {
                        newStudent.setSemesterNumber(semesterNum);
                    } else {
                        newStudent.setSemesterNumber(++semesterNum);
                    }

                    students.add(newStudent);


                    JSONArray pastCourses = (JSONArray) input.get("Past Courses");
                    ArrayList<Grade> grades = new ArrayList<>();
                    for (Object c : pastCourses) {
                        JSONObject grade = (JSONObject) c;
                        String courseCode = (String) grade.get("Course");
                        Course course = findCourse(courseCode);
                        int intGrade = (int) (long) grade.get("intGrade");
                        grades.add(new Grade(course, intGrade));
                    }
                    newStudent.getTranscript().setGrades(grades);


                    JSONArray currentCourses = (JSONArray) input.get("Current Courses");
                    ArrayList<Course> stuCurrCourses = new ArrayList<>();
                    for (int i = 0; i < currentCourses.size(); i++) {
                        stuCurrCourses.add(findCourse((String) currentCourses.get(i)));
                    }

                    stuCurrCourses.forEach(newStudent.getTranscript()::addPastCourse);//Addpast courses for student
                    addPastSummerMandatories(newStudent);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Output overall statistics as json file using statisticsBuffer
     */
    private void statisticsOutput() {
        org.json.JSONObject statJson = new org.json.JSONObject();
        String[] stats = statisticsBuffer.split("\n");
        statJson.put("Overall Statistics", stats);

        try (FileWriter file = new FileWriter(new File("Statistics.json"))) {
            file.write(statJson.toString(4));
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the registration process json files for each student inside
     * newly created Students folder.
     */
    private void registrationProcessOutput() {
        new File("Students").mkdir();
        for (Student s : students) {
            org.json.JSONObject studentJson = new org.json.JSONObject();
            studentJson.put("StudentName", s.getName());
            studentJson.put("StudentId", s.getStudentId());
            studentJson.put("SemesterNumber", s.getSemesterNumber());
            studentJson.put("CompletedCredits", s.getTranscript().getCompletedCredits());


            ArrayList<Grade> stuGrades = s.getTranscript().getGrades();

            JSONArray pastCourses = new JSONArray();
            for (Grade g : stuGrades) {
                org.json.JSONObject grades = new org.json.JSONObject();
                grades.put("Course", g.getCourse().getCourseCode());
                grades.put("LetterGrade", g.getLetterGrade());
                grades.put("intGrade", g.getIntGrade());
                pastCourses.add(grades);
            }
            studentJson.put("Past Courses", pastCourses);

            JSONArray currentCourses = new JSONArray();
            ArrayList<Course> stuCurrentCourses = s.getTranscript().getCurrentCourses();

            for (Course c : stuCurrentCourses) {
                currentCourses.add(c.getCourseCode());
            }

            studentJson.put("Current Courses", currentCourses);

            JSONArray messages = new JSONArray();
            String[] executionMessages = s.getExecutionTrace().toString().split("\\n");
            for (String st : executionMessages) {
                messages.add(st);
            }
            studentJson.put("Execution Trace", messages);


            studentJson.put("AdvisorName", s.getAdvisor().getName());


            try (FileWriter file = new FileWriter(new File("Students/" + s.getStudentId() + ".json"))) {
                file.write(studentJson.toString(4));
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Prints the registration process for each student to the terminal
     */
    private void printRegistrationProcess() {
        for (Student s : students) {
            System.out.println("==========\nRegistration process for: " + s.getName() + ": " + s.getStudentId() +
                    " \nSemester Number: " + s.getSemesterNumber() + "\nCompleted Credits: " + s.getTranscript().getCompletedCredits());
            System.out.println("Advisor: " + s.getAdvisor().getName() + "\n");
            s.getExecutionTrace().append("\n\nCurrent Courses: \n");
            for (Course c : s.getTranscript().getCurrentCourses()) {
                s.getExecutionTrace().append(c.toString() + ", ");
            }
            System.out.println(s.toString());
            System.out.println(s.getExecutionTrace());
            System.out.println("==============\n\n");
        }
    }

    /**
     * Prints non registered student statistics for mandatory courses
     */
    private void printMandatoryStatistics() {
        for (Course c : courses) {
            if (c.getNonRegisteredCollision().size() > 0) {
                statisticsBuffer += c.getNonRegisteredCollision().size() + " Students couldn't register to " +
                        c.toString() + " Because of a collision problem: (";
                for (Student s : c.getNonRegisteredCollision()) {
                    statisticsBuffer += s.getStudentId() + " ";
                }
                statisticsBuffer += ")\n";
            }

            if (c.getNonRegisteredQuota().size() > 0) {
                statisticsBuffer += c.getNonRegisteredQuota().size() + " Students couldn't register to " +
                        c.toString() + " Because of a quota problem: (";
                for (Student s : c.getNonRegisteredQuota()) {
                    statisticsBuffer += s.getStudentId() + " ";
                }
                statisticsBuffer += ")\n";
            }
        }

        for (MandatoryCourse c: mandatoryCourses) {
            if (c.getNonRegisteredPrereq().size() > 0) {
                statisticsBuffer += c.getNonRegisteredPrereq().size() + " Students couldn't register to " +
                        c.toString() + " Because of a Prerequisite problem: (";
                for (Student s : c.getNonRegisteredPrereq()) {
                    statisticsBuffer += s.getStudentId() + " ";
                }
                statisticsBuffer += ")\n";
            }
        }
}

    private void printFinalProjectStatistics() {
        for (FinalProjectMandatoryCourse c: finalProjectMandatoryCourses) {
            if (c.getNonRegisteredCredit().size() > 0) {
                statisticsBuffer += (c.getNonRegisteredCredit().size() + " Students couldn't register to " +
                        c.toString() + " Because of credit problem: (");
                c.getNonRegisteredCredit().forEach(s -> statisticsBuffer += (s.getStudentId() + " "));
                statisticsBuffer += (")\n");
            }
        }
    }

    private void printElectiveStatistics() {
        Set<Student> teStudents = new HashSet<>();
        for (TechnicalElectiveCourse te: techElectiveCourses) {
            teStudents.addAll(te.getNonRegisteredStudents());
        }
        if (teStudents.size() > 0) {
            statisticsBuffer += (teStudents.size() + " Student couldn't register to a Technical Elective " +
                    "(TE) this semester: (");
            teStudents.forEach(s -> statisticsBuffer += (s.getStudentId() + ", "));
            statisticsBuffer += (")\n");
        }

    }

    private void printStatistics() {
        System.out.println("\n\n=============  Overall Statistics About Courses ===========\n");
        printMandatoryStatistics();
        printFinalProjectStatistics();
        printElectiveStatistics();
        System.out.println(statisticsBuffer);
    }


    private void initializeAdvisors()  {
        for (int i = 0; i < advisorCount; i++) {
            String name = Main.getNamesList().get((int) (Math.random() * Main.getNamesList().size() - 1));
            String surname = Main.getSurnamesList().get((int) (Math.random() * Main.getSurnamesList().size() - 1));
            advisors.add(new Advisor(name + " " + surname));
        }
    }

    private void initStudentsByCount(int currentYear, int numOfStudents) {
        for (int i = 0; i < numOfStudents; i++) {
            String name = Main.getNamesList().get((int) (Math.random() * Main.getNamesList().size() - 1));
            String surname = Main.getSurnamesList().get((int) (Math.random() * Main.getSurnamesList().size() - 1));
            String fullName = name + " " + surname;
            students.add(new Student(fullName, currentYear, ++totalStudents[currentYear - 1], this));
        }
    }

    /**Initializes students for each of the four year by calling
     * initStudentsByCount method*/
    private void initializeStudents(int first, int second, int third, int fourth)  {
        initStudentsByCount(1, first);
        initStudentsByCount(2, second);
        initStudentsByCount(3, third);
        initStudentsByCount(4, fourth);
    }

    /**Sets a random advisor for each student in students list*/
    private void appointAdvisors() {
        for (Student s: students) {
            int index = (int) (Math.random() * advisors.size()); //Random advisor's index to be appointed to the student
            s.setAdvisor(advisors.get(index));
        }
    }


    private void addPastNTEs(Student student) {
        int count = student.getNumOfPastElectives(nonTechElectiveSemesters);
        Collections.shuffle(nontechElectiveCourses); // shuffle nte courses.
        for (int i = 0; i < count; i++) {
            student.getTranscript().addPastCourse(nontechElectiveCourses.get(i));
        }
    }

    private void addPastFTEs(Student student) {
        int count = student.getNumOfPastElectives(facTechElectiveSemesters);
        Collections.shuffle(facultyElectiveCourses); // shuffle FTE courses to get randomized courses.
        for (int i = 0; i < count; i++) {
            student.getTranscript().addPastCourse(facultyElectiveCourses.get(i));
        }
    }

    private void addPastTEs(Student student) {
        int count = student.getNumOfPastElectives(techElectiveSemesters);
        Collections.shuffle(techElectiveCourses); // shuffle FTE courses to get randomized courses.
        for (int i = 0; i < count; i++) {
            student.getTranscript().addPastCourse(techElectiveCourses.get(i));
        }
    }

    /**Adds past mandatory courses for each student*/
    private void addPastMandatories(Student student) {
        for (Course course : mandatoryCourses) { //For each course, add it to past courses list if its semester is less than student's
            if (course.isEligiblePastCourse(student)) { //If course is an eligible past course for student
                student.getTranscript().addPastCourse(course);
            }
        }
    }

    /**Adds every past summer internship courses to student's
     * past course list (Used when regenerating students)*/
    private void addPastSummerMandatories(Student student) {
        for (MandatoryCourse course: mandatoryCourses) {
            if (course.isEligiblePastCourse(student) && course.getSemester() == Semester.SUMMER) {
                student.getTranscript().addPastCourse(course);
            }
        }
    }


    /**Adds past courses for each student*/
    private void addPastCourses() {
        for (Student student : students) {
            addPastMandatories(student);
            addPastNTEs(student);
            addPastFTEs(student);
            addPastTEs(student);
        }
    }

    /**Calls every student's requestCourses method*/
    private void requestCourses() {
        for (Student s: students) {
            s.requestCourses();
        }
    }

    /**Returns the number of technical elective courses
     * that are offered for the student including failed past courses
     * and current semester courses*/
    public int offeredTECount(Student student) {
        int count = 0;
        for (Integer i : techElectiveSemesters) { //Add if student's semester is greater or equal to TE semesters
            if (student.getSemesterNumber() == i) {
                count++;
            }
        }
        //count -= student.getTranscript().getPassedTECount(); //Exclude passed courses
        return count;
    }

    public int offeredFTECount(Student student) {
        int count = 0;
        for (Integer i : facTechElectiveSemesters) { //Add if student's semester is greater or equal to FTE semesters
            if (student.getSemesterNumber() == i) {
                count++;
            }
        }
        //count -= student.getTranscript().getPassedFTECount(); //Exclude passed courses
        return count;
    }

    public int offeredNTECount(Student student) {
        int count = 0;
        for (Integer i : nonTechElectiveSemesters) { //Add if student's semester is greater or equal to NTE semesters
            if (student.getSemesterNumber() == i) {
                count++;
            }
        }
        //count -= student.getTranscript().getPassedNTECount(); //Exclude passed courses
        return count;
    }

    /**Returns a list of mandatory courses which are offered to the
     * student in simulated semester*/
    public ArrayList<CourseSection> getOfferedMandatories(Student student) {
        ArrayList<CourseSection> offeredCourseSections = new ArrayList<>();
        for (MandatoryCourse c: mandatoryCourses) {
            if (c.isOfferableForStudent(student)) {
                offeredCourseSections.add(c.getCourseSection());
            }
        }
        return offeredCourseSections;
    }

    /**Takes a student and returns offered elective course sections
     * for that student randomly, if there are any. */
    public ArrayList<CourseSection> getOfferedElectives(Student student) {
        int nteCount = offeredNTECount(student);
        int teCount = offeredTECount(student);
        int fteCount = offeredFTECount(student);
        ArrayList<CourseSection> offeredCourses = new ArrayList<>();

        Collections.shuffle(nontechElectiveCourses); //Shuffle and get first n elements
        nontechElectiveCourses.subList(0, nteCount).forEach(c -> offeredCourses.add(c.getCourseSection()));

        Collections.shuffle(techElectiveCourses);
        techElectiveCourses.subList(0, teCount).forEach(c -> offeredCourses.add(c.getCourseSection()));

        Collections.shuffle(facultyElectiveCourses);
        facultyElectiveCourses.subList(0, fteCount).forEach(c -> offeredCourses.add(c.getCourseSection()));

        return offeredCourses;
    }

    /**Reads mandatory courses from the input file and
     * adds them to the corresponding list*/
    private void readMandatoryCourses(JSONObject input) {
        JSONArray inputCourses = (JSONArray) input.get("MandatoryCourses");
        for(Object c: inputCourses) { //Read mandatory courses and initialize
            JSONObject course = (JSONObject) c;
            String courseCode = (String) course.get("courseCode");
            float courseSemester = ((Number)course.get("semester")).floatValue();
            int credits = (int)(long)course.get("credits");
            int theoretical = (int)(long)course.get("theoretical");
            int practical = (int)(long) course.get("practical");
            int mandQuota = (int) (long) course.get("quota");
            ArrayList<Course> preRequisiteCourses = new ArrayList<>();
            JSONArray preRequisites = (JSONArray) course.get("preRequisites");
            for (Object p: preRequisites) {
                preRequisiteCourses.add(findCourse((String)p));
            }

            MandatoryCourse newCourse = new MandatoryCourse(courseCode,  courseSemester,  mandQuota, credits, theoretical,
                    practical, preRequisiteCourses);
            courses.add(newCourse);
            mandatoryCourses.add(newCourse);
        }
    }

    /**Reads final project courses from the input file and
     * adds them to the corresponding list*/
    private void readFinalProjectCourses(JSONObject input) {
        int finalProjectReqCredit = (int) (long) input.get("FinalProjectRequiredCredits");
        JSONArray finalProjCourses = (JSONArray) input.get("FinalProjectMandatoryCourses");
        for (Object c: finalProjCourses) {
            JSONObject course = (JSONObject) c;
            String courseCode = (String) course.get("courseCode");
            float courseSemester = ((Number)course.get("semester")).floatValue();
            int credits = (int)(long)course.get("credits");
            int theoretical = (int)(long)course.get("theoretical");
            int practical = (int)(long) course.get("practical");
            int finalQuota = (int) (long) course.get("quota");
            ArrayList<Course> preRequisiteCourses = new ArrayList<>();
            JSONArray preRequisites = (JSONArray) course.get("preRequisites");
            for (Object p: preRequisites) {
                preRequisiteCourses.add(findCourse((String)p));
            }

            FinalProjectMandatoryCourse newCourse = new FinalProjectMandatoryCourse(courseCode,  courseSemester,  finalQuota, credits,
                    theoretical, practical, preRequisiteCourses, finalProjectReqCredit);
            courses.add(newCourse);
            mandatoryCourses.add(newCourse);
            finalProjectMandatoryCourses.add(newCourse);
        }
    }

    /**Reads technical elective courses from the input file and
     * adds them to the corresponding list*/
    private void readTechElectives(JSONObject input) {
        int techReqCredits = (int)(long)input.get("technicalRequiredCredits");
        JSONArray technicalSemesters = (JSONArray) input.get("technicalSemesters");
        for (int i = 0; i< technicalSemesters.size(); i++) {
            techElectiveSemesters.add((int)(long)technicalSemesters.get(i));
        }
        int techCredits = (int) (long) input.get("technicalCredits");
        int techTheoretical = (int) (long) input.get("technicalTheoretical");
        int techPractical = (int) (long) input.get("technicalPractical");

        JSONArray techCourses = (JSONArray) input.get("technicalElectiveCourses");
        for (Object c: techCourses) {
            JSONObject course = (JSONObject) c;
            String courseCode = (String) course.get("courseCode");
            int techQuota = (int) (long) course.get("quota");
            ArrayList<Course> preRequisiteCourses = new ArrayList<>();
            JSONArray preRequisites = (JSONArray) course.get("preRequisites");
            for (Object p: preRequisites) {
                preRequisiteCourses.add(findCourse((String)p));
            }

            TechnicalElectiveCourse newTechElective = new TechnicalElectiveCourse( courseCode, techQuota, techCredits, techTheoretical,
                    techPractical,techElectiveSemesters,techReqCredits, preRequisiteCourses);
            courses.add(newTechElective);
            techElectiveCourses.add(newTechElective);
        }
    }

    /**Reads non-tech elective courses from the input file and
     * adds them to the corresponding list*/
    private void readNonTechs(JSONObject input) {
        JSONArray nontechnicalSemesters = (JSONArray) input.get("nonTechnicalSemesters");
        for (int i = 0; i< nontechnicalSemesters.size(); i++) {
            nonTechElectiveSemesters.add((int)(long)nontechnicalSemesters.get(i));
        }
        int nonTechCredits = (int) (long) input.get("nonTechnicalCredits");
        int nonTechTheoretical = (int) (long) input.get("nonTechnicalTheoretical");
        int nonTechPractical = (int) (long) input.get("nonTechnicalPractical");

        JSONArray nonTechCourses = (JSONArray) input.get("nonTechnicalElectiveCourses");
        for (Object c: nonTechCourses) {
            JSONObject course = (JSONObject) c;
            String courseCode = (String) course.get("courseCode");
            int nonTechQuota = (int) (long) course.get("quota");

            NonTechnicalUniversityElectiveCourse newNonTechElective = new NonTechnicalUniversityElectiveCourse(courseCode, nonTechQuota,
                    nonTechCredits, nonTechTheoretical, nonTechPractical, nonTechElectiveSemesters);
            courses.add(newNonTechElective);
            nontechElectiveCourses.add(newNonTechElective);
        }
    }

    /**Reads faculty elective courses from the input file and
     * adds them to the corresponding list*/
    private void readFacTechs(JSONObject input) {
        JSONArray facTechnicalSemesters = (JSONArray) input.get("facultyTechnicalSemesters");

        for (int i = 0; i< facTechnicalSemesters.size(); i++) {
            facTechElectiveSemesters.add((int)(long)facTechnicalSemesters.get(i));
        }
        int facTechCredits = (int) (long) input.get("facultyTechnicalCredits");
        int facTechTheoretical = (int) (long) input.get("facultyTechnicalTheoretical");
        int facTechPractical = (int) (long) input.get("facultyTechnicalPractical");

        JSONArray facTechCourses = (JSONArray) input.get("facultyTechnicalElectiveCourses");
        for (Object c: facTechCourses) {
            JSONObject course = (JSONObject) c;
            String courseCode = (String) course.get("courseCode");
            int facTechQuota = (int) (long) course.get("quota");

            FacultyTechnicalElectiveCourse newFacTechElective = new FacultyTechnicalElectiveCourse(courseCode, facTechQuota, facTechCredits,
                    facTechTheoretical, facTechPractical, facTechElectiveSemesters);
            courses.add(newFacTechElective);
            facultyElectiveCourses.add(newFacTechElective);
        }
    }

    /**Reads general information about registration system
     * and initializes students */
    private void readGeneralInformation(JSONObject input) {
        double prob =  ((Number)input.get("PassProbability")).doubleValue();
        setPassProbability(prob);
        int advisorCount = (int)(long)input.get("Advisors");
        setAdvisorCount(advisorCount);
        String semester = (String)input.get("CurrentSemester");
        setSemester(semester);
        isRegenerate = (boolean) input.get("RegenerateStudents");
        int first = (int) (long) input.get("1stYearStudents");
        int second = (int) (long) input.get("2ndYearStudents");
        int third = (int) (long) input.get("3rdYearStudents");
        int fourth = (int) (long) input.get("4thYearStudents");

        if (!isRegenerate) { //If we don't regenerate students, initialize them
            initializeStudents(first, second, third, fourth);
        }
    }

    /**Reads the input file and creates courses according to the
     * input file*/
    private void readInput() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject input = (JSONObject) parser.parse(new FileReader("input.json"));

            readGeneralInformation(input);
            readMandatoryCourses(input);
            readFinalProjectCourses(input);
            readNonTechs(input);
            readTechElectives(input);
            readFacTechs(input);
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public boolean isThereEmptyNonTechSection() {
        for (Course c: nontechElectiveCourses) {
            if (!c.getCourseSection().isFull()) {
                return true;
            }
        }
        return false;
    }

    public boolean isThereEmptyTechSection() {
        for (Course c: techElectiveCourses) {
            if (!c.getCourseSection().isFull()) {
                return true;
            }
        }
        return false;
    }

    public boolean isThereEmptyFacTechSection() {
        for (Course c: facultyElectiveCourses) {
            if (!c.getCourseSection().isFull()) {
                return true;
            }
        }
        return false;
    }

    private void setSemester(String semester) {
        switch (semester.toLowerCase()) {
            case "spring":
                this.semester = Semester.SPRING; break;
            case "fall":
                this.semester = Semester.FALL; break;
            default:
                System.out.println("Incorrect Semester for Registration System!!");
                System.exit(-1);
        }
    }


    /**Returns the course in courses list by its course code*/
    private  Course findCourse(String courseCode) {
        for (Course c: courses) {
            if (c.getCourseCode().equals(courseCode)) {
                return c;
            }
        }
        return null;
    }

    public Semester getSemester() {
        return semester;
    }


    public double getPassProbability() {
        return passProbability;
    }

    public void setPassProbability(double passProbability) {
        this.passProbability = passProbability;
    }


    public int getAdvisorCount() {
        return advisorCount;
    }

    public void setAdvisorCount(int advisorCount) {
        this.advisorCount = advisorCount;
    }


    public ArrayList<Course> getCourses() {
        return courses;
    }


    public int[] getTotalStudents() {
        return totalStudents;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Advisor> getAdvisors() {
        return advisors;
    }

    public ArrayList<MandatoryCourse> getMandatoryCourses() {
        return mandatoryCourses;
    }

    public ArrayList<FinalProjectMandatoryCourse> getFinalProjectMandatoryCourses() {
        return finalProjectMandatoryCourses;
    }

    public ArrayList<NonTechnicalUniversityElectiveCourse> getNontechElectiveCourses() {
        return nontechElectiveCourses;
    }

    public ArrayList<TechnicalElectiveCourse> getTechElectiveCourses() {
        return techElectiveCourses;
    }

    public ArrayList<FacultyTechnicalElectiveCourse> getFacultyElectiveCourses() {
        return facultyElectiveCourses;
    }

    public ArrayList<Integer> getNonTechElectiveSemesters() {
        return nonTechElectiveSemesters;
    }

    public ArrayList<Integer> getTechElectiveSemesters() {
        return techElectiveSemesters;
    }

    public ArrayList<Integer> getFacTechElectiveSemesters() {
        return facTechElectiveSemesters;
    }

    public String getStatisticsBuffer() {
        return statisticsBuffer;
    }
}