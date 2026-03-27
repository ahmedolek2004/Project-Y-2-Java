package model;

/**
 * Enum for system permissions
 */
public enum Permission {
    // User Management
    ADD_USER("Add User"),
    DELETE_USER("Delete User"),
    EDIT_USER("Edit User"),
    CHANGE_PASSWORD("Change Password"),
    
    // Student Management
    ADD_STUDENT("Add Student"),
    EDIT_STUDENT("Edit Student"),
    DELETE_STUDENT("Delete Student"),
    VIEW_STUDENT("View Student"),
    
    // Subject Management
    ADD_SUBJECT("Add Subject"),
    EDIT_SUBJECT("Edit Subject"),
    DELETE_SUBJECT("Delete Subject"),
    VIEW_SUBJECT("View Subject"),
    
    // Enrollment Management
    ENROLL_STUDENT("Enroll Student"),
    UNENROLL_STUDENT("Unenroll Student"),
    VIEW_ENROLLMENT("View Enrollment"),
    
    // Grades and Attendance
    ADD_GRADE("Add Grade"),
    EDIT_GRADE("Edit Grade"),
    VIEW_GRADES("View Grades"),
    ADD_ATTENDANCE("Add Attendance"),
    VIEW_ATTENDANCE("View Attendance"),
    
    // Reports
    VIEW_FINANCIAL_REPORTS("View Financial Reports"),
    VIEW_ATTENDANCE_REPORTS("View Attendance Reports"),
    VIEW_ACADEMIC_REPORTS("View Academic Reports"),
    
    // Settings and Backup
    MANAGE_SETTINGS("Manage Settings"),
    BACKUP_DATA("Backup Data"),
    RESTORE_DATA("Restore Data"),
    VIEW_LOGS("View System Logs");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
