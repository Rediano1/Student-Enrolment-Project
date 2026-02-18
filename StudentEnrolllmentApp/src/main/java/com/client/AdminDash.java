/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/

package com.client;

import com.student.Student;
import com.student.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// The main screen for administrators to manage the entire system
public class AdminDash extends JFrame {
    // Handles talking to the server
    private ClientHandler clientHandler;
    // The main area that switches between different views
    private JPanel mainContentPanel;
    // Manages switching between dashboard, students, courses panels
    private CardLayout cardLayout;
    // Tables to display data
    private JTable studentsTable;
    private JTable coursesTable;
    // Data models for the tables
    private DefaultTableModel studentsModel;
    private DefaultTableModel coursesModel;
    // Allows searching through student list
    private TableRowSorter<DefaultTableModel> studentTableSorter;
    // Field to type search terms
    private JTextField studentSearchField;
    // Labels showing counts on dashboard
    private JLabel studentCountLabel;
    private JLabel courseCountLabel;
    private JLabel enrollmentCountLabel;
    
    // Color scheme for the interface
    private final Color SIDEBAR_COLOR = new Color(52, 73, 94);
    private final Color HEADER_COLOR = new Color(41, 128, 185);
    private final Color CONTENT_COLOR = Color.WHITE;
    private final Color CARD_BLUE = new Color(52, 152, 219);
    private final Color CARD_GREEN = new Color(46, 204, 113);
    private final Color CARD_PURPLE = new Color(155, 89, 182);
    
    // Sets up the admin dashboard with server connection
    public AdminDash(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        initializeUI();  // Build the interface
        loadData();      // Load initial data
    }

    // Creates all the visual components of the admin dashboard
    private void initializeUI() {
        setTitle("Admin Dashboard - Student Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));

        // Main container holding everything
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(CONTENT_COLOR);

        // Top header with title and logout
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Main content area with sidebar and switching panels
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CONTENT_COLOR);

        // Left navigation menu
        JPanel sidebarPanel = createSidebarPanel();
        contentPanel.add(sidebarPanel, BorderLayout.WEST);

        // The main area that changes between dashboard, students, courses
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(CONTENT_COLOR);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add all the different management screens
        mainContentPanel.add(createDashboardPanel(), "DASHBOARD");
        mainContentPanel.add(createStudentsPanel(), "STUDENTS");
        mainContentPanel.add(createCoursesPanel(), "COURSES");
        mainContentPanel.add(createEnrollmentsPanel(), "ENROLLMENTS");

        contentPanel.add(mainContentPanel, BorderLayout.CENTER);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
        
        // Start with dashboard visible
        cardLayout.show(mainContentPanel, "DASHBOARD");
    }

    // Creates the top bar with title and logout button
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(100, 70));

        // Main title
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Student Enrollment System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 200, 200));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(HEADER_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Logout button - returns to login screen
        JButton btnLogout = createStyledButton("Logout", new Color(231, 76, 60));
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (clientHandler != null) {
                    clientHandler.close();
                }
                new Login().setVisible(true);
                dispose();
            }
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        return headerPanel;
    }

    // Creates the left navigation menu
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Sidebar title
        JLabel sidebarTitle = new JLabel("Student Enrollment");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sidebarTitle.setForeground(new Color(200, 200, 200));
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        // Navigation buttons
        JButton btnDashboard = createSidebarButton("📊 Dashboard");
        JButton btnStudents = createSidebarButton("👥 Manage Students");
        JButton btnCourses = createSidebarButton("📚 Manage Courses");
        JButton btnEnrollments = createSidebarButton("🎓 View Enrollments");

        // Switch panels when buttons clicked
        btnDashboard.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "DASHBOARD");
            loadDashboardData();
        });
        btnStudents.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "STUDENTS");
            loadStudents();
        });
        btnCourses.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "COURSES");
            loadCourses();
        });
        btnEnrollments.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "ENROLLMENTS");
            refreshEnrollmentsPanel();
        });

        // Arrange everything in sidebar
        sidebarPanel.add(sidebarTitle);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnStudents);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnCourses);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnEnrollments);
        sidebarPanel.add(Box.createVerticalGlue());

        return sidebarPanel;
    }

    // Creates a styled button for the sidebar
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Darken button when mouse hovers over it
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SIDEBAR_COLOR);
            }
        });

        return button;
    }

    // Creates the main dashboard with overview cards
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("System Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Quick overview of the enrollment system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CONTENT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Refresh button to update counts
        JButton btnRefresh = createStyledButton("Refresh Data", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> loadDashboardData());

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // Cards showing statistics
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        cardsPanel.setBackground(CONTENT_COLOR);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Create the three statistic cards
        JPanel studentCard = createStatCard("Total Students", "0", "👥", CARD_BLUE);
        studentCountLabel = (JLabel) studentCard.getComponent(1);

        JPanel courseCard = createStatCard("Total Courses", "0", "📚", CARD_GREEN);
        courseCountLabel = (JLabel) courseCard.getComponent(1);

        JPanel enrollmentCard = createStatCard("Total Enrollments", "0", "🎓", CARD_PURPLE);
        enrollmentCountLabel = (JLabel) enrollmentCard.getComponent(1);

        cardsPanel.add(studentCard);
        cardsPanel.add(courseCard);
        cardsPanel.add(enrollmentCard);

        // Quick action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionsPanel.setBackground(CONTENT_COLOR);

        JButton btnManageStudents = createActionCard("Manage Students", "Add, edit, or remove students", new Color(41, 128, 185));
        JButton btnManageCourses = createActionCard("Manage Courses", "Add, edit, or remove courses", new Color(46, 204, 113));
        JButton btnViewEnrollments = createActionCard("View Enrollments", "View all student enrollments", new Color(155, 89, 182));

        // Navigate to different sections
        btnManageStudents.addActionListener(e -> cardLayout.show(mainContentPanel, "STUDENTS"));
        btnManageCourses.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSES"));
        btnViewEnrollments.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "ENROLLMENTS");
            refreshEnrollmentsPanel();
        });

        actionsPanel.add(btnManageStudents);
        actionsPanel.add(btnManageCourses);
        actionsPanel.add(btnViewEnrollments);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Creates a statistic card with big numbers
    private JPanel createStatCard(String title, String count, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(180, 120));

        // Title and icon at top
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(iconLabel, BorderLayout.EAST);

        // Big number in middle
        JLabel countLabel = new JLabel(count, SwingConstants.CENTER);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        countLabel.setForeground(Color.WHITE);
        countLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        return card;
    }

    // Creates a clickable action card
    private JButton createActionCard(String title, String description, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 80));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(new Color(240, 240, 240));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(color);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        button.add(textPanel, BorderLayout.CENTER);

        // Darken on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // Creates the student management panel
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(CONTENT_COLOR);

        JButton btnAddStudent = createStyledButton("Add Student", new Color(46, 204, 113));
        JButton btnRefresh = createStyledButton("Refresh", new Color(52, 152, 219));

        btnAddStudent.addActionListener(e -> showAddStudentDialog());
        btnRefresh.addActionListener(e -> {
            studentSearchField.setText("");
            loadStudents();
        });

        actionPanel.add(btnAddStudent);
        actionPanel.add(btnRefresh);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        // Search panel for finding students
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(CONTENT_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel searchLabel = new JLabel("Search Students:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        studentSearchField = new JTextField(20);
        studentSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentSearchField.setToolTipText("Search by student number or name");
        
        JButton btnSearch = createStyledButton("Search", new Color(155, 89, 182));
        JButton btnClearSearch = createStyledButton("Clear", new Color(149, 165, 166));
        
        btnSearch.addActionListener(e -> searchStudents());
        btnClearSearch.addActionListener(e -> {
            studentSearchField.setText("");
            searchStudents();
        });
        
        // Search when Enter key pressed
        studentSearchField.addActionListener(e -> searchStudents());
        
        searchPanel.add(searchLabel);
        searchPanel.add(studentSearchField);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);

        // Table to display all students
        String[] columns = {"Student Number", "Name", "Actions"};
        studentsModel = new DefaultTableModel(columns, 0) {
            // Only the Actions column can be clicked (for delete buttons)
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        studentsTable = new JTable(studentsModel);
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentsTable.setRowHeight(35);
        studentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set up search functionality
        studentTableSorter = new TableRowSorter<>(studentsModel);
        studentsTable.setRowSorter(studentTableSorter);
        
        // Add delete buttons to each row
        studentsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        studentsTable.getColumn("Actions").setCellEditor(new StudentButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(CONTENT_COLOR);
        contentContainer.add(searchPanel, BorderLayout.NORTH);
        contentContainer.add(scrollPane, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentContainer, BorderLayout.CENTER);

        return panel;
    }

    // Creates the course management panel
    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(CONTENT_COLOR);

        JButton btnAddCourse = createStyledButton("Add Course", new Color(46, 204, 113));
        JButton btnRefresh = createStyledButton("Refresh", new Color(52, 152, 219));

        btnAddCourse.addActionListener(e -> showAddCourseDialog());
        btnRefresh.addActionListener(e -> loadCourses());

        actionPanel.add(btnAddCourse);
        actionPanel.add(btnRefresh);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        // Course table
        String[] columns = {"Course Code", "Title", "Description", "Actions"};
        coursesModel = new DefaultTableModel(columns, 0) {
            // Only Actions column is clickable
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        coursesTable = new JTable(coursesModel);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        coursesTable.setRowHeight(35);
        coursesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Add delete buttons
        coursesTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        coursesTable.getColumn("Actions").setCellEditor(new CourseButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Creates the enrollment viewing panel
    private JPanel createEnrollmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Enrollment Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(CONTENT_COLOR);

        JButton btnRefresh = createStyledButton("Refresh All", new Color(52, 152, 219));
        JButton btnViewCourseStudents = createStyledButton("Course Students", new Color(155, 89, 182));
        JButton btnViewStudentCourses = createStyledButton("Student Courses", new Color(241, 196, 15));

        btnRefresh.addActionListener(e -> refreshEnrollmentsPanel());
        btnViewCourseStudents.addActionListener(e -> showCourseStudentsDialog());
        btnViewStudentCourses.addActionListener(e -> showStudentCoursesDialog());

        actionPanel.add(btnRefresh);
        actionPanel.add(btnViewCourseStudents);
        actionPanel.add(btnViewStudentCourses);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        // Enrollment table showing student-course relationships
        String[] columns = {"Student Number", "Student Name", "Course Code", "Course Title"};
        DefaultTableModel enrollmentsModel = new DefaultTableModel(columns, 0);
        JTable enrollmentsTable = new JTable(enrollmentsModel);
        enrollmentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        enrollmentsTable.setRowHeight(35);
        enrollmentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(enrollmentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Searches through the student list based on text input
    private void searchStudents() {
        String searchText = studentSearchField.getText().trim();
        
        if (searchText.isEmpty()) {
            // Show all students if search is empty
            studentTableSorter.setRowFilter(null);
        } else {
            // Filter to show only matching students
            RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 0, 1);
            studentTableSorter.setRowFilter(rf);
            
            // Show message if no results found
            if (studentTableSorter.getViewRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No students found matching: " + searchText, 
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Shows a popup to add a new student
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add New Student", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form with input fields
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtStudentNumber = new JTextField();
        JTextField txtName = new JTextField();
        JPasswordField txtPassword = new JPasswordField();

        panel.add(new JLabel("Student Number:"));
        panel.add(txtStudentNumber);
        panel.add(new JLabel("Name:"));
        panel.add(txtName);
        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);

        // Save and cancel buttons
        JButton btnSave = createStyledButton("Save", new Color(46, 204, 113));
        JButton btnCancel = createStyledButton("Cancel", new Color(231, 76, 60));

        // Save the new student
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String studentNumber = txtStudentNumber.getText().trim();
                String name = txtName.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();

                // Check all fields are filled
                if (studentNumber.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create student and send to server
                Student student = new Student(studentNumber, name, password);
                boolean success = clientHandler.addStudent(student);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    studentSearchField.setText("");
                    loadStudents();
                    loadDashboardData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Close without saving
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        panel.add(btnSave);
        panel.add(btnCancel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Shows a popup to add a new course
    private void showAddCourseDialog() {
        JDialog dialog = new JDialog(this, "Add New Course", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtCourseCode = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtDescription = new JTextField();

        panel.add(new JLabel("Course Code:"));
        panel.add(txtCourseCode);
        panel.add(new JLabel("Title:"));
        panel.add(txtTitle);
        panel.add(new JLabel("Description:"));
        panel.add(txtDescription);

        JButton btnSave = createStyledButton("Save", new Color(46, 204, 113));
        JButton btnCancel = createStyledButton("Cancel", new Color(231, 76, 60));

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String courseCode = txtCourseCode.getText().trim();
                String title = txtTitle.getText().trim();
                String description = txtDescription.getText().trim();

                // Check required fields
                if (courseCode.isEmpty() || title.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill required fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create course and send to server
                Course course = new Course(courseCode, title, description);
                boolean success = clientHandler.addCourse(course);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                    loadDashboardData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add course.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        panel.add(btnSave);
        panel.add(btnCancel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Loads all initial data when dashboard starts
    private void loadData() {
        loadDashboardData();
        loadStudents();
        loadCourses();
    }

    // Loads the counts for the dashboard cards
    private void loadDashboardData() {
        // Get student count
        List<Student> students = clientHandler.getAllStudents();
        if (students != null) {
            studentCountLabel.setText(String.valueOf(students.size()));
            System.out.println("📊 Total students in system: " + students.size());
        }

        // Get course count
        List<Course> courses = clientHandler.getAllCourses();
        if (courses != null) {
            courseCountLabel.setText(String.valueOf(courses.size()));
            System.out.println("📚 Total courses in system: " + courses.size());
        }

        // Get enrollment count
        List<String[]> enrollments = clientHandler.getAllEnrollments();
        if (enrollments != null) {
            enrollmentCountLabel.setText(String.valueOf(enrollments.size()));
            System.out.println("🎓 Total enrollments in system: " + enrollments.size());
        }
    }

    // Loads all students into the table
    private void loadStudents() {
        studentsModel.setRowCount(0);
        
        List<Student> students = clientHandler.getAllStudents();
        
        if (students != null) {
            for (Student student : students) {
                // Don't allow deleting admin user
                String action = student.getStudentNumber().equals("admin") ? "Cannot Delete" : "Delete";
                studentsModel.addRow(new Object[]{
                    student.getStudentNumber(), 
                    student.getName(),
                    action
                });
            }
        }
        
        // Clear any search filters
        studentTableSorter.setRowFilter(null);
    }

    // Loads all courses into the table
    private void loadCourses() {
        coursesModel.setRowCount(0);
        
        List<Course> courses = clientHandler.getAllCourses();
        
        if (courses != null) {
            for (Course course : courses) {
                coursesModel.addRow(new Object[]{
                    course.getCourseCode(), 
                    course.getTitle(), 
                    course.getDescription(),
                    "Delete"
                });
            }
        }
    }

    // Refreshes the enrollments panel with current data
    private void refreshEnrollmentsPanel() {
        try {
            JPanel enrollmentsPanel = (JPanel) mainContentPanel.getComponent(3);
            if (enrollmentsPanel != null) {
                Component[] components = enrollmentsPanel.getComponents();
                if (components.length > 1 && components[1] instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) components[1];
                    JTable table = (JTable) scrollPane.getViewport().getView();
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.setRowCount(0);
                    
                    List<String[]> enrollments = clientHandler.getAllEnrollments();
                    if (enrollments != null) {
                        for (String[] enrollment : enrollments) {
                            model.addRow(enrollment);
                        }
                        System.out.println("🎓 Loaded " + enrollments.size() + " enrollments");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Note: Could not refresh enrollments panel");
        }
    }

    // Shows which students are enrolled in a specific course
    private void showCourseStudentsDialog() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code:");
        if (courseCode != null && !courseCode.trim().isEmpty()) {
            List<Student> students = clientHandler.getCourseStudents(courseCode);
            if (students != null && !students.isEmpty()) {
                // Build formatted list of students
                StringBuilder sb = new StringBuilder();
                sb.append("Students enrolled in ").append(courseCode).append(":\n\n");
                for (Student student : students) {
                    sb.append("• ").append(student.getStudentNumber())
                      .append(" - ").append(student.getName()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), 
                    "Course Students", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No students enrolled in this course or course not found", 
                    "No Enrollments", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Shows which courses a specific student is enrolled in
    private void showStudentCoursesDialog() {
        String studentNumber = JOptionPane.showInputDialog(this, "Enter Student Number:");
        if (studentNumber != null && !studentNumber.trim().isEmpty()) {
            List<Course> courses = clientHandler.getStudentCourses(studentNumber);
            if (courses != null && !courses.isEmpty()) {
                // Build formatted list of courses
                StringBuilder sb = new StringBuilder();
                sb.append("Courses enrolled by ").append(studentNumber).append(":\n\n");
                for (Course course : courses) {
                    sb.append("• ").append(course.getCourseCode())
                      .append(" - ").append(course.getTitle()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), 
                    "Student Courses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No courses found for this student or student not found", 
                    "No Courses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Refreshes all tables with current data
    public void refreshTables() {
        loadStudents();
        loadCourses();
        loadDashboardData();
    }

    // Creates a consistently styled button
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Makes buttons look nice in table cells
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        // Determines how each button cell looks
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            
            // Color buttons based on their purpose
            if ("Delete".equals(value)) {
                setBackground(new Color(231, 76, 60));
            } else if ("Cannot Delete".equals(value)) {
                setBackground(new Color(149, 165, 166));
            } else {
                setBackground(new Color(46, 204, 113));
            }
            
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
        }
    }

    // Handles the delete buttons in student table
    private class StudentButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String currentStudentNumber;

        public StudentButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    deleteStudent(currentStudentNumber);
                }
            });
        }

        // Called when a cell starts being edited
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentStudentNumber = (String) table.getValueAt(row, 0);
            button.setText((value == null) ? "" : value.toString());
            
            // Color button based on state
            if ("Delete".equals(value)) {
                button.setBackground(new Color(192, 57, 43));
            } else {
                button.setBackground(new Color(149, 165, 166));
            }
            
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return button;
        }

        public Object getCellEditorValue() {
            return "Delete";
        }

        // Actually deletes a student after confirmation
        private void deleteStudent(String studentNumber) {
            // Prevent deletion of admin
            if ("admin".equals(studentNumber)) {
                JOptionPane.showMessageDialog(AdminDash.this, 
                    "Cannot delete the admin user!", 
                    "Delete Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ask for confirmation
            int confirm = JOptionPane.showConfirmDialog(AdminDash.this, 
                "Delete student: " + studentNumber + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = clientHandler.deleteStudent(studentNumber);
                
                if (success) {
                    JOptionPane.showMessageDialog(AdminDash.this, 
                        "Student deleted successfully!", 
                        "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                    refreshTables();
                } else {
                    JOptionPane.showMessageDialog(AdminDash.this, 
                        "Failed to delete student.", 
                        "Delete Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Handles the delete buttons in course table
    private class CourseButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String currentCourseCode;

        public CourseButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    deleteCourse(currentCourseCode);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentCourseCode = (String) table.getValueAt(row, 0);
            button.setText((value == null) ? "" : value.toString());
            button.setBackground(new Color(192, 57, 43));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return button;
        }

        public Object getCellEditorValue() {
            return "Delete";
        }

        // deletes a course after confirmation
        private void deleteCourse(String courseCode) {
            // Ask for confirmation
            int confirm = JOptionPane.showConfirmDialog(AdminDash.this, 
                "Delete course: " + courseCode + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = clientHandler.deleteCourse(courseCode);
                
                if (success) {
                    JOptionPane.showMessageDialog(AdminDash.this, 
                        "Course deleted successfully!", 
                        "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                    refreshTables();
                } else {
                    JOptionPane.showMessageDialog(AdminDash.this, 
                        "Failed to delete course.", 
                        "Delete Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}