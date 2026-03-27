package service;

import database.JSONDatabaseHelper;
import model.Student;
import model.Subject;
import model.Enrollment;
import model.Permission;
import java.util.List;

/**
 * Academic Service
 * Handles academic operations like enrollment, grades, GPA calculation
 */
public class AcademicService {

    /**
     * Enroll a student in a subject with a grade
     */
    public static boolean enrollStudent(int studentId, int subjectId, double grade) {
        if (!AuthService.hasPermission(Permission.ENROLL_STUDENT)) {
            System.err.println("Permission denied: Cannot enroll student");
            return false;
        }

        try {
            JSONDatabaseHelper.enrollStudentInSubject(studentId, subjectId, grade);
            System.out.println("Student " + studentId + " enrolled in subject " + subjectId);
            return true;
        } catch (Exception e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Unenroll a student from a subject
     */
    public static boolean unenrollStudent(int studentId, int subjectId) {
        if (!AuthService.hasPermission(Permission.UNENROLL_STUDENT)) {
            System.err.println("Permission denied: Cannot unenroll student");
            return false;
        }

        try {
            JSONDatabaseHelper.unenrollStudentFromSubject(studentId, subjectId);
            System.out.println("Student " + studentId + " unenrolled from subject " + subjectId);
            return true;
        } catch (Exception e) {
            System.err.println("Error unenrolling student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get enrollments for a student
     */
    public static List<Enrollment> getStudentEnrollments(int studentId) {
        if (!AuthService.hasPermission(Permission.VIEW_ENROLLMENT)) {
            System.err.println("Permission denied: Cannot view enrollments");
            return java.util.Collections.emptyList();
        }

        return JSONDatabaseHelper.getEnrollmentsForStudent(studentId);
    }

    /**
     * Calculate GPA for a student
     */
    public static double calculateGPA(int studentId) {
        if (!AuthService.hasPermission(Permission.VIEW_GRADES)) {
            System.err.println("Permission denied: Cannot view grades");
            return 0.0;
        }

        return JSONDatabaseHelper.calculateGPA(studentId);
    }

    /**
     * Get the top student
     */
    public static Student getTopStudent() {
        if (!AuthService.hasPermission(Permission.VIEW_ACADEMIC_REPORTS)) {
            System.err.println("Permission denied: Cannot view academic reports");
            return null;
        }

        return JSONDatabaseHelper.getTopStudent();
    }
}
