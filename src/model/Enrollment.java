package model;

public class Enrollment {

    private int id;
    private int studentId;
    private int subjectId;
    private double grade;
    private String subjectName;
    private String subjectCode;
    private int credits;

    public Enrollment(int id, int studentId, int subjectId, double grade, String subjectName, String subjectCode, int credits) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.grade = grade;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.credits = credits;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getLetterGrade() {
        if (grade >= 90) {
            return "A"; 
        }else if (grade >= 80) {
            return "B"; 
        }else if (grade >= 70) {
            return "C"; 
        }else if (grade >= 60) {
            return "D"; 
        }else {
            return "F";
        }
    }

    @Override
    public String toString() {
        return "Enrollment{"
                + "id=" + id
                + ", studentId=" + studentId
                + ", subjectId=" + subjectId
                + ", grade=" + grade
                + ", subjectName='" + subjectName + '\''
                + ", subjectCode='" + subjectCode + '\''
                + ", credits=" + credits
                + '}';
    }
}
