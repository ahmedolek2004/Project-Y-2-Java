package service;

import database.JSONDatabaseHelper;
import model.Subject;
import model.Permission;
import java.util.List;

/**
 * Subject Management Service
 * Handles subject CRUD operations with permission checks
 */
public class SubjectService {

    /**
     * Add a new subject
     */
    public static boolean addSubject(Subject subject) {
        if (!AuthService.hasPermission(Permission.ADD_SUBJECT)) {
            System.err.println("Permission denied: Cannot add subject");
            return false;
        }

        try {
            JSONDatabaseHelper.addSubject(subject);
            System.out.println("Subject added: " + subject.getName());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding subject: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing subject
     */
    public static boolean updateSubject(Subject subject) {
        if (!AuthService.hasPermission(Permission.EDIT_SUBJECT)) {
            System.err.println("Permission denied: Cannot edit subject");
            return false;
        }

        try {
            JSONDatabaseHelper.updateSubject(subject);
            System.out.println("Subject updated: " + subject.getName());
            return true;
        } catch (Exception e) {
            System.err.println("Error updating subject: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a subject
     */
    public static boolean deleteSubject(int subjectId) {
        if (!AuthService.hasPermission(Permission.DELETE_SUBJECT)) {
            System.err.println("Permission denied: Cannot delete subject");
            return false;
        }

        try {
            JSONDatabaseHelper.deleteSubject(subjectId);
            System.out.println("Subject deleted: ID " + subjectId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting subject: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all subjects
     */
    public static List<Subject> getAllSubjects() {
        if (!AuthService.hasPermission(Permission.VIEW_SUBJECT)) {
            System.err.println("Permission denied: Cannot view subjects");
            return java.util.Collections.emptyList();
        }

        return JSONDatabaseHelper.getAllSubjects();
    }

    /**
     * Get a specific subject by ID
     */
    public static Subject getSubjectById(int subjectId) {
        if (!AuthService.hasPermission(Permission.VIEW_SUBJECT)) {
            System.err.println("Permission denied: Cannot view subject");
            return null;
        }

        return JSONDatabaseHelper.getSubjectById(subjectId);
    }
}
