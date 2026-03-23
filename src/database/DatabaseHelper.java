package database;

import model.Student;
import model.Subject;
import model.User;
import model.Enrollment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/student_management_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found; please add mysql-connector-java to classpath.");
        }
        createTables();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void createTables() {
        String userSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(100) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "role VARCHAR(50) NOT NULL)";

        String studentSQL = "CREATE TABLE IF NOT EXISTS students ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "email VARCHAR(255) NOT NULL, "
                + "phone VARCHAR(255) NOT NULL)";

        String subjectSQL = "CREATE TABLE IF NOT EXISTS subjects ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "code VARCHAR(100) NOT NULL, "
                + "credits INT NOT NULL)";

        String enrollmentSQL = "CREATE TABLE IF NOT EXISTS enrollments ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "student_id INT NOT NULL, "
                + "subject_id INT NOT NULL, "
                + "grade DOUBLE, "
                + "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE, "
                + "FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE, "
                + "UNIQUE(student_id, subject_id))";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(userSQL);
            stmt.execute(studentSQL);
            stmt.execute(subjectSQL);
            stmt.execute(enrollmentSQL);
            seedAdminUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedAdminUser() {
        if (getUserByUsername("admin") == null) {
            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "admin");
                pstmt.setString(2, "admin");
                pstmt.setString(3, "ADMIN");
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String authenticate(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserByUsername(String username) {
        String sql = "SELECT id, username, role FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addStudent(Student student) {
        String sql = "INSERT INTO students(name,email,phone) VALUES(?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id,name,email,phone FROM students";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static Student getStudentById(int id) {
        String sql = "SELECT id,name,email,phone FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setInt(4, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addSubject(Subject subject) {
        String sql = "INSERT INTO subjects(name,code,credits) VALUES(?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, subject.getName());
            pstmt.setString(2, subject.getCode());
            pstmt.setInt(3, subject.getCredits());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT id,name,code,credits FROM subjects";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                subjects.add(new Subject(rs.getInt("id"), rs.getString("name"), rs.getString("code"), rs.getInt("credits")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    public static Subject getSubjectById(int id) {
        String sql = "SELECT id,name,code,credits FROM subjects WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Subject(rs.getInt("id"), rs.getString("name"), rs.getString("code"), rs.getInt("credits"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateSubject(Subject subject) {
        String sql = "UPDATE subjects SET name = ?, code = ?, credits = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, subject.getName());
            pstmt.setString(2, subject.getCode());
            pstmt.setInt(3, subject.getCredits());
            pstmt.setInt(4, subject.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSubject(int id) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Enrollment methods
    public static void enrollStudentInSubject(int studentId, int subjectId, double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        String sql = "INSERT INTO enrollments (student_id, subject_id, grade) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE grade = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setDouble(3, grade);
            pstmt.setDouble(4, grade);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unenrollStudentFromSubject(int studentId, int subjectId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Enrollment> getEnrollmentsForStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.id, e.student_id, e.subject_id, e.grade, s.name, s.code, s.credits "
                + "FROM enrollments e JOIN subjects s ON e.subject_id = s.id WHERE e.student_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("subject_id"),
                            rs.getDouble("grade"),
                            rs.getString("name"),
                            rs.getString("code"),
                            rs.getInt("credits")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public static double calculateGPA(int studentId) {
        List<Enrollment> enrollments = getEnrollmentsForStudent(studentId);
        if (enrollments.isEmpty()) {
            return 0.0;
        }
        double totalPoints = 0.0;
        int totalCredits = 0;
        for (Enrollment e : enrollments) {
            totalPoints += e.getGrade() * e.getCredits();
            totalCredits += e.getCredits();
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    public static Student getTopStudent() {
        List<Student> students = getAllStudents();
        Student top = null;
        double maxGPA = -1;
        for (Student s : students) {
            double gpa = calculateGPA(s.getId());
            if (gpa > maxGPA) {
                maxGPA = gpa;
                top = s;
            }
        }
        return top;
    }
}
