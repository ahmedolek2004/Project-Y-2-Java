package model;

public class User {

    private int id;
    private String username;
    private String password;
    private Role role;
    private String fullName;
    private String email;
    private boolean active;

    public User(int id, String username, String password, String roleName, String fullName, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = Role.fromString(roleName);
        this.fullName = fullName;
        this.email = email;
        this.active = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Check if user has a specific permission
     */
    public boolean hasPermission(Permission permission) {
        return RolePermissionMatrix.hasPermission(this.role, permission);
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(Role roleToCheck) {
        return this.role == roleToCheck;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
