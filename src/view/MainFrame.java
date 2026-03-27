package view;

import database.JSONDatabaseHelper;
import model.Student;
import model.Subject;
import model.Enrollment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private final DefaultTableModel studentModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0);
    private final DefaultTableModel subjectModel = new DefaultTableModel(new String[]{"ID", "Name", "Code", "Credits"}, 0);
    private final DefaultTableModel enrollmentModel = new DefaultTableModel(new String[]{"Subject", "Code", "Credits", "Grade", "Letter"}, 0);

    private String currentUserRole;

    public MainFrame() {
        super("Student Management System");

        if (!showLoginDialog()) {
            dispose();
            System.exit(0);
            return;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", createStudentPanel());
        tabs.addTab("Subjects", createSubjectPanel());
        tabs.addTab("Student Details", createStudentDetailsPanel());

        add(tabs);
        loadData();
    }

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

        String role = JSONDatabaseHelper.authenticate(username, password);
        if (role == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return showLoginDialog();
        }

        currentUserRole = role;
        setTitle("Student Management System - User: " + username + " (" + role + ")");
        return true;
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable table = new JTable(studentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(1, 5, 5, 5));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        controls.add(new JLabel("Name"));
        controls.add(nameField);
        controls.add(new JLabel("Email"));
        controls.add(emailField);
        controls.add(new JLabel("Phone"));
        controls.add(phoneField);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> {
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                JOptionPane.showMessageDialog(this, "You do not have permission to add students.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JSONDatabaseHelper.addStudent(new Student(0, name, email, phone));
            clearStudentFields(nameField, emailField, phoneField);
            loadStudents();
        });

        refreshBtn.addActionListener(e -> loadStudents());

        buttonRow.add(addBtn);
        buttonRow.add(refreshBtn);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                JOptionPane.showMessageDialog(this, "You do not have permission to delete students.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) studentModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JSONDatabaseHelper.deleteStudent(id);
                loadStudents();
            }
        });
        buttonRow.add(deleteBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(buttonRow, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable table = new JTable(subjectModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(1, 5, 5, 5));
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
        JButton addBtn = new JButton("Add");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> {
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                JOptionPane.showMessageDialog(this, "You do not have permission to add subjects.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }

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
                JOptionPane.showMessageDialog(this, "Credits must be an integer", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JSONDatabaseHelper.addSubject(new Subject(0, name, code, credits));
            clearSubjectFields(nameField, codeField, creditsField);
            loadSubjects();
        });

        refreshBtn.addActionListener(e -> loadSubjects());

        buttonRow.add(addBtn);
        buttonRow.add(refreshBtn);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                JOptionPane.showMessageDialog(this, "You do not have permission to delete subjects.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) subjectModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this subject?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JSONDatabaseHelper.deleteSubject(id);
                loadSubjects();
            }
        });
        buttonRow.add(deleteBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(buttonRow, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Top: Student selector and GPA
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Student:"));
        JComboBox<Student> studentCombo = new JComboBox<>();
        topPanel.add(studentCombo);
        JButton loadBtn = new JButton("Load Details");
        topPanel.add(loadBtn);
        JLabel gpaLabel = new JLabel("GPA: --");
        topPanel.add(gpaLabel);
        JButton topStudentBtn = new JButton("Show Top Student");
        topPanel.add(topStudentBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // Center: Enrollments table
        JTable table = new JTable(enrollmentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: Enroll/Unenroll controls
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel enrollPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enrollPanel.add(new JLabel("Subject:"));
        JComboBox<Subject> subjectCombo = new JComboBox<>();
        enrollPanel.add(subjectCombo);
        enrollPanel.add(new JLabel("Grade:"));
        JTextField gradeField = new JTextField(5);
        enrollPanel.add(gradeField);
        JButton enrollBtn = new JButton("Enroll");
        enrollPanel.add(enrollBtn);
        JButton unenrollBtn = new JButton("Unenroll Selected");
        enrollPanel.add(unenrollBtn);

        bottomPanel.add(enrollPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshSubjectsBtn = new JButton("Refresh Subjects");
        buttonPanel.add(refreshSubjectsBtn);

        bottomPanel.add(buttonPanel);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Load students and subjects
        loadStudentsForCombo(studentCombo);
        loadSubjectsForCombo(subjectCombo);

        loadBtn.addActionListener(e -> {
            Student selected = (Student) studentCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a student.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadEnrollments(selected.getId());
            double gpa = JSONDatabaseHelper.calculateGPA(selected.getId());
            gpaLabel.setText(String.format("GPA: %.2f", gpa));
        });

        enrollBtn.addActionListener(e -> {
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                JOptionPane.showMessageDialog(this, "You do not have permission to enroll students.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
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
            JSONDatabaseHelper.enrollStudentInSubject(selectedStudent.getId(), selectedSubject.getId(), grade);
            loadEnrollments(selectedStudent.getId());
            double gpa = JSONDatabaseHelper.calculateGPA(selectedStudent.getId());
            gpaLabel.setText(String.format("GPA: %.2f", gpa));
            gradeField.setText("");
        });

        unenrollBtn.addActionListener(e -> {
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                JOptionPane.showMessageDialog(this, "You do not have permission to unenroll students.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an enrollment to remove.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Student selectedStudent = (Student) studentCombo.getSelectedItem();
            if (selectedStudent == null) {
                return;
            }
            String subjectCode = (String) enrollmentModel.getValueAt(selectedRow, 1);
            Subject subject = findSubjectByCode(subjectCode);
            if (subject != null) {
                JSONDatabaseHelper.unenrollStudentFromSubject(selectedStudent.getId(), subject.getId());
                loadEnrollments(selectedStudent.getId());
                double gpa = JSONDatabaseHelper.calculateGPA(selectedStudent.getId());
                gpaLabel.setText(String.format("GPA: %.2f", gpa));
            }
        });

        topStudentBtn.addActionListener(e -> {
            Student top = JSONDatabaseHelper.getTopStudent();
            if (top != null) {
                JOptionPane.showMessageDialog(this, "Top Student: " + top.getName() + " (ID: " + top.getId() + ")", "Top Student", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No students found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        refreshSubjectsBtn.addActionListener(e -> loadSubjectsForCombo(subjectCombo));

        return panel;
    }

    private void clearStudentFields(JTextField nameField, JTextField emailField, JTextField phoneField) {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    private void clearSubjectFields(JTextField nameField, JTextField codeField, JTextField creditsField) {
        nameField.setText("");
        codeField.setText("");
        creditsField.setText("");
    }

    private void loadData() {
        loadStudents();
        loadSubjects();
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        List<Student> students = JSONDatabaseHelper.getAllStudents();
        for (Student s : students) {
            studentModel.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), s.getPhone()});
        }
    }

    private void loadSubjects() {
        subjectModel.setRowCount(0);
        List<Subject> subjects = JSONDatabaseHelper.getAllSubjects();
        for (Subject s : subjects) {
            subjectModel.addRow(new Object[]{s.getId(), s.getName(), s.getCode(), s.getCredits()});
        }
    }

    private void loadStudentsForCombo(JComboBox<Student> combo) {
        combo.removeAllItems();
        List<Student> students = JSONDatabaseHelper.getAllStudents();
        for (Student s : students) {
            combo.addItem(s);
        }
    }

    private void loadSubjectsForCombo(JComboBox<Subject> combo) {
        combo.removeAllItems();
        List<Subject> subjects = JSONDatabaseHelper.getAllSubjects();
        for (Subject s : subjects) {
            combo.addItem(s);
        }
    }

    private void loadEnrollments(int studentId) {
        enrollmentModel.setRowCount(0);
        List<Enrollment> enrollments = JSONDatabaseHelper.getEnrollmentsForStudent(studentId);
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

    private Subject findSubjectByCode(String code) {
        List<Subject> subjects = JSONDatabaseHelper.getAllSubjects();
        for (Subject s : subjects) {
            if (s.getCode().equals(code)) {
                return s;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
