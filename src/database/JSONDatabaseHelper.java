package database;

import model.Student;
import model.Subject;
import model.User;
import model.Enrollment;
import model.Role;
import model.Permission;
import model.RolePermissionMatrix;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * JSONDatabaseHelper - Manages data using JSON files instead of a database
 * Provides the same interface as DatabaseHelper for easy migration
 */
public class JSONDatabaseHelper {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.json";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.json";
    private static final String SUBJECTS_FILE = DATA_DIR + "/subjects.json";
    private static final String ENROLLMENTS_FILE = DATA_DIR + "/enrollments.json";

    private static int nextStudentId = 1;
    private static int nextSubjectId = 1;
    private static int nextEnrollmentId = 1;
    private static int nextUserId = 1;

    // In-memory caches
    private static List<User> users = new ArrayList<>();
    private static List<Student> students = new ArrayList<>();
    private static List<Subject> subjects = new ArrayList<>();
    private static List<Enrollment> enrollments = new ArrayList<>();

    static {
        initializeDataDirectory();
        loadAllData();
        seedAdminUser();
    }

    private static void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAllData() {
        users = loadUsers();
        students = loadStudents();
        subjects = loadSubjects();
        enrollments = loadEnrollments();

        // Update nextIds based on loaded data
        users.forEach(u -> nextUserId = Math.max(nextUserId, u.getId() + 1));
        students.forEach(s -> nextStudentId = Math.max(nextStudentId, s.getId() + 1));
        subjects.forEach(s -> nextSubjectId = Math.max(nextSubjectId, s.getId() + 1));
        enrollments.forEach(e -> nextEnrollmentId = Math.max(nextEnrollmentId, e.getId() + 1));
    }

    public static String authenticate(String username, String password) {
        System.out.println("[JSON Auth] Checking user: " + username);
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.isActive()) {
                System.out.println("[JSON Auth] User authenticated with role: " + user.getRole());
                return user.getRole().toString();
            }
        }
        System.out.println("[JSON Auth] Authentication failed");
        return null;
    }

    public static User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // ==================== STUDENT OPERATIONS ====================
    public static void addStudent(Student student) {
        if (student.getId() == 0) {
            student.setId(nextStudentId++);
        }
        students.add(student);
        saveStudents();
    }

    public static List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public static Student getStudentById(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public static void updateStudent(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == student.getId()) {
                students.set(i, student);
                saveStudents();
                return;
            }
        }
    }

    public static void deleteStudent(int id) {
        students.removeIf(s -> s.getId() == id);
        enrollments.removeIf(e -> e.getStudentId() == id);
        saveStudents();
        saveEnrollments();
    }

    // ==================== SUBJECT OPERATIONS ====================
    public static void addSubject(Subject subject) {
        if (subject.getId() == 0) {
            subject.setId(nextSubjectId++);
        }
        subjects.add(subject);
        saveSubjects();
    }

    public static List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }

    public static Subject getSubjectById(int id) {
        for (Subject s : subjects) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public static void updateSubject(Subject subject) {
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getId() == subject.getId()) {
                subjects.set(i, subject);
                saveSubjects();
                return;
            }
        }
    }

    public static void deleteSubject(int id) {
        subjects.removeIf(s -> s.getId() == id);
        enrollments.removeIf(e -> e.getSubjectId() == id);
        saveSubjects();
        saveEnrollments();
    }

    // ==================== ENROLLMENT OPERATIONS ====================
    public static void enrollStudentInSubject(int studentId, int subjectId, double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }

        Subject subject = getSubjectById(subjectId);
        if (subject == null) {
            throw new IllegalArgumentException("Subject not found");
        }

        // Check if enrollment already exists
        for (Enrollment e : enrollments) {
            if (e.getStudentId() == studentId && e.getSubjectId() == subjectId) {
                e.setGrade(grade);
                saveEnrollments();
                return;
            }
        }

        // Create new enrollment
        Enrollment enrollment = new Enrollment(
                nextEnrollmentId++,
                studentId,
                subjectId,
                grade,
                subject.getName(),
                subject.getCode(),
                subject.getCredits()
        );
        enrollments.add(enrollment);
        saveEnrollments();
    }

    public static void unenrollStudentFromSubject(int studentId, int subjectId) {
        enrollments.removeIf(e -> e.getStudentId() == studentId && e.getSubjectId() == subjectId);
        saveEnrollments();
    }

    public static List<Enrollment> getEnrollmentsForStudent(int studentId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e.getStudentId() == studentId) {
                result.add(e);
            }
        }
        return result;
    }

    // ==================== GPA CALCULATIONS ====================
    public static double calculateGPA(int studentId) {
        List<Enrollment> studentEnrollments = getEnrollmentsForStudent(studentId);
        if (studentEnrollments.isEmpty()) {
            return 0.0;
        }
        double totalPoints = 0.0;
        int totalCredits = 0;
        for (Enrollment e : studentEnrollments) {
            totalPoints += e.getGrade() * e.getCredits();
            totalCredits += e.getCredits();
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    public static Student getTopStudent() {
        List<Student> allStudents = getAllStudents();
        Student top = null;
        double maxGPA = -1;
        for (Student s : allStudents) {
            double gpa = calculateGPA(s.getId());
            if (gpa > maxGPA) {
                maxGPA = gpa;
                top = s;
            }
        }
        return top;
    }

    // ==================== FILE I/O OPERATIONS ====================
    private static void seedAdminUser() {
        if (getUserByUsername("admin") == null) {
            User adminUser = new User(nextUserId++, "admin", "admin", "ADMIN", "System Administrator", "admin@system.com");
            users.add(adminUser);
            saveUsers();
        }
    }

    private static List<User> loadUsers() {
        return loadFromJSON(USERS_FILE, new ArrayList<>(), "users", User.class);
    }

    private static List<Student> loadStudents() {
        return loadFromJSON(STUDENTS_FILE, new ArrayList<>(), "students", Student.class);
    }

    private static List<Subject> loadSubjects() {
        return loadFromJSON(SUBJECTS_FILE, new ArrayList<>(), "subjects", Subject.class);
    }

    private static List<Enrollment> loadEnrollments() {
        return loadFromJSON(ENROLLMENTS_FILE, new ArrayList<>(), "enrollments", Enrollment.class);
    }

    private static <T> List<T> loadFromJSON(String filePath, List<T> defaultValue, String arrayName, Class<T> clazz) {
        try {
            if (Files.exists(Paths.get(filePath))) {
                String content = new String(Files.readAllBytes(Paths.get(filePath)));
                if (!content.isEmpty()) {
                    return parseJSONArray(content, arrayName, clazz);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading from " + filePath + ": " + e.getMessage());
        }
        return defaultValue;
    }

    private static void saveUsers() {
        saveToJSON(USERS_FILE, users, "users");
    }

    private static void saveStudents() {
        saveToJSON(STUDENTS_FILE, students, "students");
    }

    private static void saveSubjects() {
        saveToJSON(SUBJECTS_FILE, subjects, "subjects");
    }

    private static void saveEnrollments() {
        saveToJSON(ENROLLMENTS_FILE, enrollments, "enrollments");
    }

    private static <T> void saveToJSON(String filePath, List<T> data, String arrayName) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"").append(arrayName).append("\": [\n");
            for (int i = 0; i < data.size(); i++) {
                json.append("    ").append(objectToJSON(data.get(i)));
                if (i < data.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("  ]\n");
            json.append("}\n");

            Files.write(Paths.get(filePath), json.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error saving to " + filePath + ": " + e.getMessage());
        }
    }

    private static String objectToJSON(Object obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            return String.format("{\"id\": %d, \"username\": \"%s\", \"password\": \"%s\", \"role\": \"%s\"}", 
                u.getId(), escapeJSON(u.getUsername()), escapeJSON(u.getPassword()), u.getRole());
        } else if (obj instanceof Student) {
            Student s = (Student) obj;
            return String.format("{\"id\": %d, \"name\": \"%s\", \"email\": \"%s\", \"phone\": \"%s\"}", 
                s.getId(), escapeJSON(s.getName()), escapeJSON(s.getEmail()), escapeJSON(s.getPhone()));
        } else if (obj instanceof Subject) {
            Subject s = (Subject) obj;
            return String.format("{\"id\": %d, \"name\": \"%s\", \"code\": \"%s\", \"credits\": %d}", 
                s.getId(), escapeJSON(s.getName()), escapeJSON(s.getCode()), s.getCredits());
        } else if (obj instanceof Enrollment) {
            Enrollment e = (Enrollment) obj;
            return String.format("{\"id\": %d, \"studentId\": %d, \"subjectId\": %d, \"grade\": %f, \"subjectName\": \"%s\", \"subjectCode\": \"%s\", \"credits\": %d}", 
                e.getId(), e.getStudentId(), e.getSubjectId(), e.getGrade(), 
                escapeJSON(e.getSubjectName()), escapeJSON(e.getSubjectCode()), e.getCredits());
        }
        return "{}";
    }

    private static String escapeJSON(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static <T> List<T> parseJSONArray(String json, String arrayName, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        try {
            // Find the array section
            int startIdx = json.indexOf("\"" + arrayName + "\"");
            if (startIdx == -1) return result;

            startIdx = json.indexOf("[", startIdx);
            int endIdx = json.indexOf("]", startIdx);
            if (startIdx == -1 || endIdx == -1) return result;

            String arrayContent = json.substring(startIdx + 1, endIdx);

            // Split by }, { to separate objects
            String[] objects = arrayContent.split("(?<=\\}),(?=\\{)");

            for (String objStr : objects) {
                objStr = objStr.trim();
                if (!objStr.isEmpty()) {
                    T obj = parseJSONObject(objStr, clazz);
                    if (obj != null) {
                        result.add(obj);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
        return result;
    }

    private static <T> T parseJSONObject(String json, Class<T> clazz) {
        try {
            if (clazz == User.class) {
                int id = extractInt(json, "id");
                String username = extractString(json, "username");
                String password = extractString(json, "password");
                String role = extractString(json, "role");
                return clazz.getDeclaredConstructor(int.class, String.class, String.class, String.class)
                        .newInstance(id, username, password, role);
            } else if (clazz == Student.class) {
                int id = extractInt(json, "id");
                String name = extractString(json, "name");
                String email = extractString(json, "email");
                String phone = extractString(json, "phone");
                return clazz.getDeclaredConstructor(int.class, String.class, String.class, String.class)
                        .newInstance(id, name, email, phone);
            } else if (clazz == Subject.class) {
                int id = extractInt(json, "id");
                String name = extractString(json, "name");
                String code = extractString(json, "code");
                int credits = extractInt(json, "credits");
                return clazz.getDeclaredConstructor(int.class, String.class, String.class, int.class)
                        .newInstance(id, name, code, credits);
            } else if (clazz == Enrollment.class) {
                int id = extractInt(json, "id");
                int studentId = extractInt(json, "studentId");
                int subjectId = extractInt(json, "subjectId");
                double grade = extractDouble(json, "grade");
                String subjectName = extractString(json, "subjectName");
                String subjectCode = extractString(json, "subjectCode");
                int credits = extractInt(json, "credits");
                return clazz.getDeclaredConstructor(int.class, int.class, int.class, double.class, 
                        String.class, String.class, int.class)
                        .newInstance(id, studentId, subjectId, grade, subjectName, subjectCode, credits);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON object: " + e.getMessage());
        }
        return null;
    }

    private static int extractInt(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private static double extractDouble(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([0-9.]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0.0;
    }

    private static String extractString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            String value = m.group(1);
            // Unescape JSON strings
            return value.replace("\\\"", "\"")
                    .replace("\\\\", "\\")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r");
        }
        return "";
    }
}
