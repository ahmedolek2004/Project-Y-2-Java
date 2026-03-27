package service;

import database.JSONDatabaseHelper;
import model.Student;
import model.Permission;
import java.util.List;

/**
 * Student Management Service
 * Handles student CRUD operations with permission checks
 */
public class StudentService {

    /**
     * Add a new student
     */
    public static boolean addStudent(Student student) {
        if (!AuthService.hasPermission(Permission.ADD_STUDENT)) {
            System.err.println("Permission denied: Cannot add student");
            return false;
        }

        try {
            JSONDatabaseHelper.addStudent(student);
            System.out.println("Student added: " + student.getName());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing student
     */
    public static boolean updateStudent(Student student) {
        if (!AuthService.hasPermission(Permission.EDIT_STUDENT)) {
            System.err.println("Permission denied: Cannot edit student");
            return false;
        }

        try {
            JSONDatabaseHelper.updateStudent(student);
            System.out.println("Student updated: " + student.getName());
            return true;
        } catch (Exception e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a student
     */
    public static boolean deleteStudent(int studentId) {
        if (!AuthService.hasPermission(Permission.DELETE_STUDENT)) {
            System.err.println("Permission denied: Cannot delete student");
            return false;
        }

        try {
            JSONDatabaseHelper.deleteStudent(studentId);
            System.out.println("Student deleted: ID " + studentId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all students
     */
    public static List<Student> getAllStudents() {
        if (!AuthService.hasPermission(Permission.VIEW_STUDENT)) {
            System.err.println("Permission denied: Cannot view students");
            return java.util.Collections.emptyList();
        }

        return JSONDatabaseHelper.getAllStudents();
    }

    /**
     * Get a specific student by ID
     */
    public static Student getStudentById(int studentId) {
        if (!AuthService.hasPermission(Permission.VIEW_STUDENT)) {
            System.err.println("Permission denied: Cannot view student");
            return null;
        }

        return JSONDatabaseHelper.getStudentById(studentId);
    }
}
