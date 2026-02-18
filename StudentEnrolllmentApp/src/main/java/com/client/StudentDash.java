
/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/

package com.client;

import com.student.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Student Dashboard - provides interface for students to view and enroll in courses
public class StudentDash extends JFrame {
    private String studentNumber;    // Current student's ID number
    private String studentName;      // Current student's full name
    private ClientHandler clientHandler;  // Handles communication with server
    private JPanel mainContentPanel;       // Panel that switches between views
    private CardLayout cardLayout;         // Manages switching between panels
    private JTable availableCoursesTable;  // Table showing courses available for enrollment
    private JTable myCoursesTable;         // Table showing courses student is already enrolled in
    private DefaultTableModel availableCoursesModel;  // Data model for available courses
    private DefaultTableModel myCoursesModel;         // Data model for enrolled courses
    private JLabel enrolledCoursesCountLabel; // NEW: Big label showing enrolled course count
    private JLabel availableCoursesCountLabel; // NEW: Big label showing available course count
    
    // Colors for consistent theme throughout the student dashboard
    private final Color SIDEBAR_COLOR = new Color(52, 73, 94);     // Dark blue sidebar
    private final Color HEADER_COLOR = new Color(41, 128, 185);    // Blue header
    private final Color CONTENT_COLOR = Color.WHITE;               // White content areas
    private final Color CARD_GREEN = new Color(46, 204, 113);      // Green for enrolled courses
    private final Color CARD_BLUE = new Color(52, 152, 219);       // Blue for available courses
    private final Color CARD_ORANGE = new Color(241, 196, 15);     // Orange for other stats
    
    // Constructor - sets up the student dashboard with student info and server connection
    public StudentDash(String studentNumber, String studentName, ClientHandler clientHandler) {
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.clientHandler = clientHandler;
        initializeUI();  // Build the user interface
        loadData();      // Load initial course data
    }

    // Creates and arranges all visual components of the student dashboard
    private void initializeUI() {
        setTitle("Student Dashboard - Student Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);  // Center window on screen
        setMinimumSize(new Dimension(900, 600));  // Prevent window from getting too small

        // Main container that holds everything using BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(CONTENT_COLOR);

        // Header at the top with welcome message and logout button
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Content area with sidebar and main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CONTENT_COLOR);

        // Navigation sidebar on the left
        JPanel sidebarPanel = createSidebarPanel();
        contentPanel.add(sidebarPanel, BorderLayout.WEST);

        // Main content area that switches between views
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(CONTENT_COLOR);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the different panels students can access
        mainContentPanel.add(createDashboardPanel(), "DASHBOARD");           // Dashboard overview
        mainContentPanel.add(createAvailableCoursesPanel(), "AVAILABLE_COURSES");  // Browse courses
        mainContentPanel.add(createMyCoursesPanel(), "MY_COURSES");               // View enrollments

        contentPanel.add(mainContentPanel, BorderLayout.CENTER);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer);
        
        // Show dashboard panel by default when dashboard opens
        cardLayout.show(mainContentPanel, "DASHBOARD");
    }

    // Creates the top header bar with welcome message and logout button
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(100, 70));

        // Main title label
        JLabel titleLabel = new JLabel("STUDENT DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // Personalized welcome message with student info
        JLabel subtitleLabel = new JLabel("Welcome, " + studentName + " (" + studentNumber + ")");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 200, 200));

        // Container for title and subtitle
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(HEADER_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Logout button that returns to login screen
        JButton btnLogout = createStyledButton("Logout", new Color(231, 76, 60));
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (clientHandler != null) {
                    clientHandler.close();  // Close connection to server
                }
                new Login().setVisible(true);  // Show login screen
                dispose();  // Close this dashboard
            }
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        return headerPanel;
    }

    // Creates the navigation sidebar with menu buttons
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(180, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Sidebar title
        JLabel sidebarTitle = new JLabel("Student Enrollment");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sidebarTitle.setForeground(new Color(200, 200, 200));
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        // Navigation buttons for student features
        JButton btnDashboard = createSidebarButton("📊 Dashboard");
        JButton btnAvailableCourses = createSidebarButton("📚 Available Courses");
        JButton btnMyCourses = createSidebarButton("🎓 My Courses");

        // Switch panels when buttons are clicked
        btnDashboard.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "DASHBOARD");
            loadDashboardData();  // Refresh dashboard data
        });
        btnAvailableCourses.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "AVAILABLE_COURSES");
            loadAvailableCourses();  // Refresh available courses list
        });
        btnMyCourses.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "MY_COURSES");
            loadMyCourses();  // Refresh enrolled courses list
        });

        // Arrange components in sidebar
        sidebarPanel.add(sidebarTitle);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnDashboard);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnAvailableCourses);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebarPanel.add(btnMyCourses);
        sidebarPanel.add(Box.createVerticalGlue());  // Push content to top

        return sidebarPanel;
    }

    // Creates a styled button for the sidebar with hover effects
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(160, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect - darken button when mouse moves over it
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

    // Creates the dashboard panel with overview cards
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("My Student Enrollment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("Welcome back, " + studentName + "! Here's your enrollment summary");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CONTENT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Refresh button
        JButton btnRefresh = createStyledButton("Refresh Data", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> loadDashboardData());

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // Cards panel for statistics - using FlowLayout for better responsiveness
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        cardsPanel.setBackground(CONTENT_COLOR);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Enrolled Courses Card
        JPanel enrolledCard = createStatCard("Courses Enrolled", "0", "🎓", CARD_GREEN);
        enrolledCoursesCountLabel = (JLabel) enrolledCard.getComponent(1);

        // Available Courses Card
        JPanel availableCard = createStatCard("Available Courses", "0", "📚", CARD_BLUE);
        availableCoursesCountLabel = (JLabel) availableCard.getComponent(1);

        // Total Courses Card
        JPanel totalCard = createStatCard("Total Courses", "0", "🏫", CARD_ORANGE);

        cardsPanel.add(enrolledCard);
        cardsPanel.add(availableCard);
        cardsPanel.add(totalCard);

        // Quick actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionsPanel.setBackground(CONTENT_COLOR);

        JButton btnBrowseCourses = createActionCard("Browse Courses", "View and enroll in available courses", new Color(52, 152, 219));
        JButton btnMyEnrollments = createActionCard("My Enrollments", "View your current course enrollments", new Color(46, 204, 113));

        btnBrowseCourses.addActionListener(e -> cardLayout.show(mainContentPanel, "AVAILABLE_COURSES"));
        btnMyEnrollments.addActionListener(e -> cardLayout.show(mainContentPanel, "MY_COURSES"));

        actionsPanel.add(btnBrowseCourses);
        actionsPanel.add(btnMyEnrollments);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Creates a statistic card with large number display - SMALLER SIZE
    private JPanel createStatCard(String title, String count, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)  // Reduced padding
        ));
        card.setPreferredSize(new Dimension(180, 120));  // Smaller size

        // Title and icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Smaller font
        titleLabel.setForeground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));  // Smaller icon
        iconLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(iconLabel, BorderLayout.EAST);

        // Count (big number)
        JLabel countLabel = new JLabel(count, SwingConstants.CENTER);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));  // Smaller font size
        countLabel.setForeground(Color.WHITE);
        countLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));  // Reduced padding

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        return card;
    }

    // Creates an action card for quick navigation - SMALLER SIZE
    private JButton createActionCard(String title, String description, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)  // Reduced padding
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 80));  // Smaller size

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Smaller font
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));  // Smaller font
        descLabel.setForeground(new Color(240, 240, 240));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(color);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        button.add(textPanel, BorderLayout.CENTER);

        // Hover effect
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

    // Creates the panel showing all available courses for enrollment
    private JPanel createAvailableCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel header with title and refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Available Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));

        // Informational label for user guidance
        JLabel infoLabel = new JLabel("Double-click on a course to view details");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CONTENT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(infoLabel, BorderLayout.SOUTH);

        // Refresh button to update course list
        JButton btnRefresh = createStyledButton("Refresh Courses", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> loadAvailableCourses());

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // Table showing courses available for enrollment
        String[] columns = {"Course Code", "Title", "Description", "Action"};
        availableCoursesModel = new DefaultTableModel(columns, 0) {
            // Make only the Action column editable (for enroll buttons)
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        availableCoursesTable = new JTable(availableCoursesModel);
        availableCoursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        availableCoursesTable.setRowHeight(35);
        availableCoursesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Add enroll button to each row in the Action column
        availableCoursesTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        availableCoursesTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Add double-click listener for course details - show popup when course is double-clicked
        availableCoursesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {  // Check for double-click
                    int row = availableCoursesTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        // Get course details from the clicked row
                        String courseCode = (String) availableCoursesTable.getValueAt(row, 0);
                        String title = (String) availableCoursesTable.getValueAt(row, 1);
                        String description = (String) availableCoursesTable.getValueAt(row, 2);
                        
                        // Show course details in a formatted message dialog
                        JOptionPane.showMessageDialog(StudentDash.this,
                            "<html><b>Course Code:</b> " + courseCode + "<br>" +
                            "<b>Title:</b> " + title + "<br>" +
                            "<b>Description:</b> " + (description != null ? description : "No description available") + "</html>",
                            "Course Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(availableCoursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Creates the panel showing courses the student is enrolled in
    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CONTENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("My Enrolled Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel infoLabel = new JLabel("");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CONTENT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(infoLabel, BorderLayout.SOUTH);

        // Refresh button to update enrolled courses list
        JButton btnRefresh = createStyledButton("Refresh My Courses", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> loadMyCourses());

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // Table showing courses the student is enrolled in
        String[] columns = {"Course Code", "Title", "Description"};
        myCoursesModel = new DefaultTableModel(columns, 0);
        myCoursesTable = new JTable(myCoursesModel);
        myCoursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        myCoursesTable.setRowHeight(35);
        myCoursesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Add double-click listener for course details
        myCoursesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = myCoursesTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String courseCode = (String) myCoursesTable.getValueAt(row, 0);
                        String title = (String) myCoursesTable.getValueAt(row, 1);
                        String description = (String) myCoursesTable.getValueAt(row, 2);
                        
                        JOptionPane.showMessageDialog(StudentDash.this,
                            "<html><b>Course Code:</b> " + courseCode + "<br>" +
                            "<b>Title:</b> " + title + "<br>" +
                            "<b>Description:</b> " + (description != null ? description : "No description available") + "</html>",
                            "Course Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(myCoursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Loads initial data when dashboard starts
    private void loadData() {
        loadDashboardData();
        loadAvailableCourses();
        loadMyCourses();
    }

    // Loads dashboard data including counts
    private void loadDashboardData() {
        // Load enrolled courses count
        List<Course> myCourses = clientHandler.getStudentCourses(studentNumber);
        if (myCourses != null) {
            int enrolledCount = myCourses.size();
            enrolledCoursesCountLabel.setText(String.valueOf(enrolledCount));
            System.out.println("🎓 " + studentName + " is enrolled in " + enrolledCount + " courses");
        }

        // Load available courses count
        List<Course> allCourses = clientHandler.getAllCourses();
        if (allCourses != null) {
            int totalCourses = allCourses.size();
            List<Course> myCoursesList = clientHandler.getStudentCourses(studentNumber);
            int enrolledCount = myCoursesList != null ? myCoursesList.size() : 0;
            int availableCount = totalCourses - enrolledCount;
            
            availableCoursesCountLabel.setText(String.valueOf(availableCount));
            
            // Update total courses in the third card
            try {
                JPanel dashboardPanel = (JPanel) mainContentPanel.getComponent(0);
                JPanel cardsPanel = (JPanel) dashboardPanel.getComponent(1);
                JPanel totalCard = (JPanel) cardsPanel.getComponent(2);
                JLabel totalCountLabel = (JLabel) totalCard.getComponent(1);
                totalCountLabel.setText(String.valueOf(totalCourses));
            } catch (Exception e) {
                System.out.println("Note: Could not update total courses count");
            }
            
            System.out.println("📚 Available courses for " + studentNumber + ": " + availableCount);
            System.out.println("🏫 Total courses in system: " + totalCourses);
        }
    }

    // Loads all available courses (that student is not enrolled in) from server
    private void loadAvailableCourses() {
        availableCoursesModel.setRowCount(0);  // Clear existing rows
        
        List<Course> courses = clientHandler.getAllCourses();
        if (courses != null) {
            int availableCount = 0;
            for (Course course : courses) {
                // Only show courses that the student is not already enrolled in
                if (!isCourseEnrolled(course.getCourseCode())) {
                    availableCoursesModel.addRow(new Object[]{
                        course.getCourseCode(), 
                        course.getTitle(), 
                        course.getDescription(),
                        "Enroll"  // Enroll button for available courses
                    });
                    availableCount++;
                }
            }
            System.out.println("📚 Available courses for " + studentNumber + ": " + availableCount);
        }
    }

    // Loads courses that the current student is enrolled in from server
    private void loadMyCourses() {
        myCoursesModel.setRowCount(0);  // Clear existing rows
        
        List<Course> myCourses = clientHandler.getStudentCourses(studentNumber);
        if (myCourses != null) {
            for (Course course : myCourses) {
                myCoursesModel.addRow(new Object[]{
                    course.getCourseCode(), 
                    course.getTitle(), 
                    course.getDescription()
                });
            }
            
            int enrolledCount = myCourses.size();
            System.out.println("🎓 " + studentName + " is enrolled in " + enrolledCount + " courses");
        }
    }

    // Checks if the current student is already enrolled in a specific course
    private boolean isCourseEnrolled(String courseCode) {
        List<Course> myCourses = clientHandler.getStudentCourses(studentNumber);
        if (myCourses != null) {
            for (Course course : myCourses) {
                if (course.getCourseCode().equals(courseCode)) {
                    return true;  // Student is enrolled in this course
                }
            }
        }
        return false;  // Student is not enrolled in this course
    }

    // Refreshes both course tables with current data from server
    public void refreshTables() {
        loadDashboardData();
        loadAvailableCourses();
        loadMyCourses();
    }

    // Creates a consistently styled button with specified color
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

    // Custom button renderer for table cells - makes enroll buttons look nice
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        // This method is called for each cell to determine how it looks
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(new Color(46, 204, 113));  // Green for enroll buttons
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
        }
    }

    // Handles the enroll button in available courses table
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String currentCourseCode;  // Track which course is being enrolled in

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();  // Stop cell editing
                    enrollInCourse(currentCourseCode);  // Perform enrollment
                }
            });
        }

        // This method is called when a cell starts being edited
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentCourseCode = (String) table.getValueAt(row, 0);  // Get course code from first column
            button.setText((value == null) ? "" : value.toString());
            button.setBackground(new Color(39, 174, 96));  // Darker green when clicked
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 11));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return button;
        }

        public Object getCellEditorValue() {
            return "Enroll";  // Return the button text
        }

        // Actually enrolls the student in the selected course
        private void enrollInCourse(String courseCode) {
            boolean success = clientHandler.enrollStudent(studentNumber, courseCode);
            
            if (success) {
                JOptionPane.showMessageDialog(StudentDash.this, 
                    "Successfully enrolled in: " + courseCode, 
                    "Enrollment Successful", JOptionPane.INFORMATION_MESSAGE);
                refreshTables();  // Update all tables to reflect new enrollment
            } else {
                JOptionPane.showMessageDialog(StudentDash.this, 
                    "Failed to enroll in: " + courseCode, 
                    "Enrollment Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}