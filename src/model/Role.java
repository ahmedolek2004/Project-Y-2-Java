package model;

/**
 * Enum for user roles in the system
 */
public enum Role {
    ADMIN("System Administrator"),
    STAFF("Registrar / Staff"),
    TEACHER("Teacher / Instructor"),
    STUDENT("Student");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STUDENT; // Default role
        }
    }
}
