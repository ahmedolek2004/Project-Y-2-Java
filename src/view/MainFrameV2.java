package view;

import service.AuthService;
import service.StudentService;
import service.SubjectService;
import service.AcademicService;
import model.Student;
import model.Subject;
import model.Enrollment;
import model.Permission;
import model.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Main Application Frame with Role-Based Access Control
 */
public class MainFrameV2 extends JFrame {

    private final DefaultTableModel studentModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0);
    private final DefaultTableModel subjectModel = new DefaultTableModel(new String[]{"ID", "Name", "Code", "Credits"}, 0);
    private final DefaultTableModel enrollmentModel = new DefaultTableModel(new String[]{"Subject", "Code", "Credits", "Grade", "Letter"}, 0);

    private JLabel userInfoLabel;
    private JLabel permissionLabel;

    public MainFrameV2() {
        super("Student Management System");

        if (!showLoginDialog()) {
            dispose();
            System.exit(0);
            return;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Create main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Create header with user info
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane based on user role
        JTabbedPane tabs = createRoleBasedTabs();
        mainPanel.add(tabs, BorderLayout.CENTER);

        // Create footer with logout button
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadData();
    }

    /**
     * Create header panel with user information
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        userInfoLabel = new JLabel();
        updateUserInfoLabel();
        panel.add(userInfoLabel, BorderLayout.WEST);

        permissionLabel = new JLabel();
        updatePermissionLabel();
        panel.add(permissionLabel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Create footer panel with logout button
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            AuthService.logout();
            dispose();
            new MainFrameV2();
        });
        panel.add(logoutBtn);
        return panel;
    }

    /**
     * Update user information label
     */
    private void updateUserInfoLabel() {
        if (AuthService.isAuthenticated()) {
            userInfoLabel.setText("User: " + AuthService.getCurrentUser().getFullName() +
                    " | Role: " + AuthService.getCurrentUserRole().getDescription());
        }
    }

    /**
     * Update permission label
     */
    private void updatePermissionLabel() {
        Role role = AuthService.getCurrentUserRole();
        permissionLabel.setText("Role: " + role.name());
    }

    /**
     * Create tabs based on user role
     */
    private JTabbedPane createRoleBasedTabs() {
        JTabbedPane tabs = new JTabbedPane();

        Role role = AuthService.getCurrentUserRole();

        switch (role) {
            case ADMIN:
                tabs.addTab("Students", createStudentPanel());
                tabs.addTab("Subjects", createSubjectPanel());
                tabs.addTab("Student Details", createStudentDetailsPanel());
                tabs.addTab("Admin Panel", createAdminPanel());
                break;

            case STAFF:
                tabs.addTab("Students", createStudentPanel());
                tabs.addTab("Subjects", createStudentSubjectPanel()); // Read-only view
                tabs.addTab("Student Details", createStudentDetailsPanel());
                break;

            case TEACHER:
                tabs.addTab("My Classes", createTeacherPanel());
                tabs.addTab("Attendance", createAttendancePanel());
                break;

            case STUDENT:
                tabs.addTab("My Grades", createStudentViewPanel());
                break;

            default:
                tabs.addTab("Info", createDefaultPanel());
        }

        return tabs;
    }

    /**
     * Create student management panel
     */
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(studentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(1, 3, 5, 5));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        controls.add(new JLabel("Name") {{
            setLabelFor(nameField);
        }});
        controls.add(nameField);
        controls.add(new JLabel("Email") {{
            setLabelFor(emailField);
        }});
        controls.add(emailField);
        controls.add(new JLabel("Phone") {{
            setLabelFor(phoneField);
        }});
        controls.add(phoneField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Add button - only if user has permission
        if (AuthService.hasPermission(Permission.ADD_STUDENT)) {
            JButton addBtn = new JButton("Add Student");
            addBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Student student = new Student(0, name, email, phone);
                if (StudentService.addStudent(student)) {
                    nameField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                    loadStudents();
                    JOptionPane.showMessageDialog(this, "Student added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add student", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonRow.add(addBtn);
        }

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadStudents());
        buttonRow.add(refreshBtn);

        // Delete button - only if user has permission
        if (AuthService.hasPermission(Permission.DELETE_STUDENT)) {
            JButton deleteBtn = new JButton("Delete Selected");
            deleteBtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int id = (int) studentModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (StudentService.deleteStudent(id)) {
                        loadStudents();
                        JOptionPane.showMessageDialog(this, "Student deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete student", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonRow.add(deleteBtn);
        }

        panel.add(controls, BorderLayout.NORTH);
        panel.add(buttonRow, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create subject management panel
     */
    private JPanel createSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(subjectModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(1, 3, 5, 5));
        JTextField nameField = new JTextField();
        JTextField codeField = new JTextField();
        JTextField creditsField = new JTextField();

        controls.add(new JLabel("Name"));
        controls.add(nameField);
        controls.add(new JLabel("Code"));
        controls.add(codeField);
        controls.add(new JLabel("Credits"));
        controls.add(creditsField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (AuthService.hasPermission(Permission.ADD_SUBJECT)) {
            JButton addBtn = new JButton("Add Subject");
            addBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String code = codeField.getText().trim();
                String creditsText = creditsField.getText().trim();

                if (name.isEmpty() || code.isEmpty() || creditsText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int credits;
                try {
                    credits = Integer.parseInt(creditsText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Credits must be a number", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Subject subject = new Subject(0, name, code, credits);
                if (SubjectService.addSubject(subject)) {
                    nameField.setText("");
                    codeField.setText("");
                    creditsField.setText("");
                    loadSubjects();
                    JOptionPane.showMessageDialog(this, "Subject added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add subject", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonRow.add(addBtn);
        }

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadSubjects());
        buttonRow.add(refreshBtn);

        if (AuthService.hasPermission(Permission.DELETE_SUBJECT)) {
            JButton deleteBtn = new JButton("Delete Selected");
            deleteBtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int id = (int) subjectModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (SubjectService.deleteSubject(id)) {
                        loadSubjects();
                        JOptionPane.showMessageDialog(this, "Subject deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete subject", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonRow.add(deleteBtn);
        }

        panel.add(controls, BorderLayout.NORTH);
        panel.add(buttonRow, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create student details panel with enrollment management
     */
    private JPanel createStudentDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Student:"));
        JComboBox<Student> studentCombo = new JComboBox<>();
        topPanel.add(studentCombo);

        JButton loadBtn = new JButton("Load Details");
        topPanel.add(loadBtn);

        JLabel gpaLabel = new JLabel("GPA: --");
        topPanel.add(gpaLabel);

        if (AuthService.hasPermission(Permission.VIEW_ACADEMIC_REPORTS)) {
            JButton topStudentBtn = new JButton("Show Top Student");
            topStudentBtn.addActionListener(e -> {
                Student top = AcademicService.getTopStudent();
                if (top != null) {
                    JOptionPane.showMessageDialog(this, "Top Student: " + top.getName(), "Top Student", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No students found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            topPanel.add(topStudentBtn);
        }

        panel.add(topPanel, BorderLayout.NORTH);

        // Center panel - enrollments table
        JTable table = new JTable(enrollmentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel - enrollment controls
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Create subject combo outside if block so it can be accessed later
        JComboBox<Subject> subjectCombo = new JComboBox<>();

        JPanel enrollPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (AuthService.hasPermission(Permission.ADD_GRADE)) {
            enrollPanel.add(new JLabel("Subject:"));
            enrollPanel.add(subjectCombo);
            enrollPanel.add(new JLabel("Grade:"));
            JTextField gradeField = new JTextField(5);
            enrollPanel.add(gradeField);

            JButton enrollBtn = new JButton("Enroll");
            enrollBtn.addActionListener(e -> {
                Student selectedStudent = (Student) studentCombo.getSelectedItem();
                Subject selectedSubject = (Subject) subjectCombo.getSelectedItem();
                String gradeText = gradeField.getText().trim();

                if (selectedStudent == null || selectedSubject == null || gradeText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select student, subject, and enter grade.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double grade;
                try {
                    grade = Double.parseDouble(gradeText);
                    if (grade < 0 || grade > 100) {
                        JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Grade must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (AcademicService.enrollStudent(selectedStudent.getId(), selectedSubject.getId(), grade)) {
                    loadEnrollments(selectedStudent.getId());
                    double gpa = AcademicService.calculateGPA(selectedStudent.getId());
                    gpaLabel.setText(String.format("GPA: %.2f", gpa));
                    gradeField.setText("");
                    JOptionPane.showMessageDialog(this, "Student enrolled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to enroll student", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            enrollPanel.add(enrollBtn);
        }

        bottomPanel.add(enrollPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshSubjectsBtn = new JButton("Refresh");
        refreshSubjectsBtn.addActionListener(e -> {
            loadStudentsForCombo(studentCombo);
            loadSubjectsForCombo(subjectCombo);
        });
        buttonPanel.add(refreshSubjectsBtn);

        bottomPanel.add(buttonPanel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Initial load
        loadStudentsForCombo(studentCombo);
        loadSubjectsForCombo(subjectCombo);

        loadBtn.addActionListener(e -> {
            Student selected = (Student) studentCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a student.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadEnrollments(selected.getId());
            double gpa = AcademicService.calculateGPA(selected.getId());
            gpaLabel.setText(String.format("GPA: %.2f", gpa));
        });

        return panel;
    }

    /**
     * Create a read-only subject view for Staff
     */
    private JPanel createStudentSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel readOnlySubjectModel = new DefaultTableModel(new String[]{"ID", "Name", "Code", "Credits"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(readOnlySubjectModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            readOnlySubjectModel.setRowCount(0);
            List<Subject> subjects = SubjectService.getAllSubjects();
            for (Subject s : subjects) {
                readOnlySubjectModel.addRow(new Object[]{s.getId(), s.getName(), s.getCode(), s.getCredits()});
            }
        });
        buttonRow.add(refreshBtn);
        panel.add(buttonRow, BorderLayout.SOUTH);

        // Initial load
        refreshBtn.doClick();

        return panel;
    }

    /**
     * Create teacher panel for grade and attendance management
     */
    private JPanel createTeacherPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Teacher Panel - Coming Soon"));
        return panel;
    }

    /**
     * Create attendance panel
     */
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Attendance Panel - Coming Soon"));
        return panel;
    }

    /**
     * Create student view panel (view-only grades)
     */
    private JPanel createStudentViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel studentNameLabel = new JLabel("Logged in as: " + AuthService.getCurrentUser().getFullName());
        panel.add(studentNameLabel, BorderLayout.NORTH);

        DefaultTableModel gradesModel = new DefaultTableModel(new String[]{"Subject", "Code", "Credits", "Grade", "Letter"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(gradesModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel gpaLabel = new JLabel("GPA: N/A");
        infoPanel.add(gpaLabel);
        panel.add(infoPanel, BorderLayout.SOUTH);

        // Load student's own grades
        // Note: This is simplified - in a real system, you'd verify the student can only see their own data
        List<Student> students = StudentService.getAllStudents();
        if (!students.isEmpty()) {
            Student student = students.get(0); // Should be current user's student record
            List<Enrollment> enrollments = AcademicService.getStudentEnrollments(student.getId());
            for (Enrollment e : enrollments) {
                gradesModel.addRow(new Object[]{
                    e.getSubjectName(),
                    e.getSubjectCode(),
                    e.getCredits(),
                    e.getGrade(),
                    e.getLetterGrade()
                });
            }
            double gpa = AcademicService.calculateGPA(student.getId());
            gpaLabel.setText(String.format("GPA: %.2f", gpa));
        }

        return panel;
    }

    /**
     * Create admin panel for system administration
     */
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Admin Panel - Coming Soon"));
        return panel;
    }

    /**
     * Create default panel
     */
    private JPanel createDefaultPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Welcome to Student Management System"));
        return panel;
    }

    /**
     * Load students into table
     */
    private void loadStudents() {
        studentModel.setRowCount(0);
        List<Student> students = StudentService.getAllStudents();
        for (Student s : students) {
            studentModel.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), s.getPhone()});
        }
    }

    /**
     * Load subjects into table
     */
    private void loadSubjects() {
        subjectModel.setRowCount(0);
        List<Subject> subjects = SubjectService.getAllSubjects();
        for (Subject s : subjects) {
            subjectModel.addRow(new Object[]{s.getId(), s.getName(), s.getCode(), s.getCredits()});
        }
    }

    /**
     * Load students for combo box
     */
    private void loadStudentsForCombo(JComboBox<Student> combo) {
        combo.removeAllItems();
        List<Student> students = StudentService.getAllStudents();
        for (Student s : students) {
            combo.addItem(s);
        }
    }

    /**
     * Load subjects for combo box
     */
    private void loadSubjectsForCombo(JComboBox<Subject> combo) {
        combo.removeAllItems();
        List<Subject> subjects = SubjectService.getAllSubjects();
        for (Subject s : subjects) {
            combo.addItem(s);
        }
    }

    /**
     * Load enrollments for a student
     */
    private void loadEnrollments(int studentId) {
        enrollmentModel.setRowCount(0);
        List<Enrollment> enrollments = AcademicService.getStudentEnrollments(studentId);
        for (Enrollment e : enrollments) {
            enrollmentModel.addRow(new Object[]{
                e.getSubjectName(),
                e.getSubjectCode(),
                e.getCredits(),
                e.getGrade(),
                e.getLetterGrade()
            });
        }
    }

    /**
     * Load initial data
     */
    private void loadData() {
        loadStudents();
        loadSubjects();
    }

    /**
     * Show login dialog
     */
    private boolean showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both username and password are required.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return showLoginDialog();
        }

        if (!AuthService.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return showLoginDialog();
        }

        setTitle("Student Management System - " + AuthService.getCurrentUser().getFullName() + " (" + AuthService.getCurrentUserRole().getDescription() + ")");
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrameV2 frame = new MainFrameV2();
            frame.setVisible(true);
        });
    }
}
