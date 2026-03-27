package service;

import database.JSONDatabaseHelper;
import model.User;
import model.Role;
import model.Permission;
import model.RolePermissionMatrix;

/**
 * Authentication and Authorization Service
 * Handles user authentication and permission checks
 */
public class AuthService {

    private static User currentUser = null;

    /**
     * Authenticate user with username and password
     */
    public static boolean login(String username, String password) {
        String roleStr = JSONDatabaseHelper.authenticate(username, password);
        if (roleStr != null) {
            User user = JSONDatabaseHelper.getUserByUsername(username);
            if (user != null) {
                currentUser = user;
                System.out.println("User logged in: " + username + " with role: " + user.getRole());
                return true;
            }
        }
        return false;
    }

    /**
     * Get the currently logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get the role of the current user
     */
    public static Role getCurrentUserRole() {
        if (currentUser != null) {
            return currentUser.getRole();
        }
        return Role.STUDENT;
    }

    /**
     * Check if current user has a specific permission
     */
    public static boolean hasPermission(Permission permission) {
        if (currentUser == null) {
            return false;
        }
        return RolePermissionMatrix.hasPermission(currentUser.getRole(), permission);
    }

    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(Role role) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasRole(role);
    }

    /**
     * Logout the current user
     */
    public static void logout() {
        currentUser = null;
        System.out.println("User logged out");
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
}
