package model;

import java.util.*;

/**
 * Manages role-based access control (RBAC)
 * Defines which permissions each role has
 */
public class RolePermissionMatrix {

    private static final Map<Role, Set<Permission>> rolePermissions = new HashMap<>();

    static {
        initializePermissions();
    }

    private static void initializePermissions() {
        // Admin has all permissions
        Set<Permission> adminPerms = new HashSet<>(Arrays.asList(
            Permission.ADD_USER, Permission.DELETE_USER, Permission.EDIT_USER, Permission.CHANGE_PASSWORD,
            Permission.ADD_STUDENT, Permission.EDIT_STUDENT, Permission.DELETE_STUDENT, Permission.VIEW_STUDENT,
            Permission.ADD_SUBJECT, Permission.EDIT_SUBJECT, Permission.DELETE_SUBJECT, Permission.VIEW_SUBJECT,
            Permission.ENROLL_STUDENT, Permission.UNENROLL_STUDENT, Permission.VIEW_ENROLLMENT,
            Permission.ADD_GRADE, Permission.EDIT_GRADE, Permission.VIEW_GRADES,
            Permission.ADD_ATTENDANCE, Permission.VIEW_ATTENDANCE,
            Permission.VIEW_FINANCIAL_REPORTS, Permission.VIEW_ATTENDANCE_REPORTS, Permission.VIEW_ACADEMIC_REPORTS,
            Permission.MANAGE_SETTINGS, Permission.BACKUP_DATA, Permission.RESTORE_DATA, Permission.VIEW_LOGS
        ));
        rolePermissions.put(Role.ADMIN, adminPerms);

        // Staff/Registrar permissions
        Set<Permission> staffPerms = new HashSet<>(Arrays.asList(
            Permission.ADD_STUDENT, Permission.EDIT_STUDENT, Permission.DELETE_STUDENT, Permission.VIEW_STUDENT,
            Permission.ADD_SUBJECT, Permission.EDIT_SUBJECT, Permission.VIEW_SUBJECT,
            Permission.ENROLL_STUDENT, Permission.UNENROLL_STUDENT, Permission.VIEW_ENROLLMENT,
            Permission.VIEW_GRADES, Permission.VIEW_ATTENDANCE,
            Permission.VIEW_FINANCIAL_REPORTS, Permission.VIEW_ATTENDANCE_REPORTS, Permission.VIEW_ACADEMIC_REPORTS
        ));
        rolePermissions.put(Role.STAFF, staffPerms);

        // Teacher permissions - can only work with their own classes
        Set<Permission> teacherPerms = new HashSet<>(Arrays.asList(
            Permission.VIEW_STUDENT,
            Permission.ADD_GRADE, Permission.EDIT_GRADE, Permission.VIEW_GRADES,
            Permission.ADD_ATTENDANCE, Permission.VIEW_ATTENDANCE,
            Permission.VIEW_ACADEMIC_REPORTS
        ));
        rolePermissions.put(Role.TEACHER, teacherPerms);

        // Student permissions - view only
        Set<Permission> studentPerms = new HashSet<>(Arrays.asList(
            Permission.VIEW_GRADES,
            Permission.VIEW_ATTENDANCE
        ));
        rolePermissions.put(Role.STUDENT, studentPerms);
    }

    /**
     * Check if a role has a specific permission
     */
    public static boolean hasPermission(Role role, Permission permission) {
        Set<Permission> perms = rolePermissions.get(role);
        return perms != null && perms.contains(permission);
    }

    /**
     * Get all permissions for a role
     */
    public static Set<Permission> getPermissions(Role role) {
        Set<Permission> perms = rolePermissions.get(role);
        return perms != null ? new HashSet<>(perms) : new HashSet<>();
    }

    /**
     * Get all roles
     */
    public static Role[] getAllRoles() {
        return Role.values();
    }
}
