
/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/

package com.client;

import com.student.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Login screen for students and administrators to access the Dashboads
public class Login extends JFrame {
    private JTextField txtStudentNumber;  // Field for entering student or admin ID
    private JPasswordField txtPassword;   // Field for entering password (hidden)
    private JButton btnLogin;             // Button to attempt login
    private JButton btnExit;              // Button to close the application
    
    // Constructor sets up the login interface when created
    public Login() {
        initializeUI();  // Build all the visual components
    }
    
    // Creates and arranges all visual elements of the login screen
    private void initializeUI() {
        setTitle("Student Enrollment System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close app when window closes
        setSize(400, 350);  // Set window size
        setLocationRelativeTo(null);  // Center window on screen
        setResizable(false);  // Prevent resizing for consistent look
        
        // Main panel with blue background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header section with system title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(240, 245, 255));
        JLabel titleLabel = new JLabel("Student Enrollment System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Login form panel with white background
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),  // Gray border
            BorderFactory.createEmptyBorder(20, 20, 20, 20)  // Internal padding
        ));
        
        // Layout constraints for precise component positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Make components fill horizontal space
        
        // Student Number label and input field
        gbc.gridx = 0; gbc.gridy = 0;  // Position in grid (column 0, row 0)
        JLabel lblStudentNumber = new JLabel("Student Number:");
        lblStudentNumber.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblStudentNumber, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;  
        txtStudentNumber = new JTextField(15);
        txtStudentNumber.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtStudentNumber, gbc);
        
        // Password label and input field
        gbc.gridx = 0; gbc.gridy = 1;  
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblPassword, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;  
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(txtPassword, gbc);
        
        // Login and Exit buttons
        gbc.gridx = 0; gbc.gridy = 2;  
        gbc.gridwidth = 2;  // Make buttons space both columns
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        // Buttons with specific colors
        btnLogin = createStyledButton("Login", new Color(46, 204, 113));  // Green button
        btnExit = createStyledButton("Exit", new Color(231, 76, 60));     // Red button
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Footer with test account information for convenience
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 245, 255));
        JLabel footerLabel = new JLabel("Student or Admin Login");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);  // this add the main panel to the window
        
        // This add action listeners 
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performLogin();  //Log in when Login button clicked
            }
        });
        
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);  // Closes application when Exit button clicked
            }
        });
    }
    
    //  Styled button with color
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);  // Remove the focus border
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));  // Padding
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Show hand cursor on hover
        return button;
    }
    
    // Validates credentials and attempts to log the user in
    private void performLogin() {
        String studentNumber = txtStudentNumber.getText().trim();  // Get student number
        String password = new String(txtPassword.getPassword());   // Get password
        
        // Check if both fields are filled in
        if (studentNumber.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both student number and password", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;  // Stop here if fields are empty
        }
        
        // Connection to server
        ClientHandler clientHandler = new ClientHandler();
        
        // Checks if connection to server was successful
        if (!clientHandler.isConnected()) {
            JOptionPane.showMessageDialog(this, 
                "Cannot connect to server! Please make sure server is running on port 12345", 
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;  // Stop here if can't connect to server
        }
        
        // Ask server to verify login credentials
        Student student = clientHandler.authenticate(studentNumber, password);
        
        if (student != null) {
            // Login successful and show welcome message
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome " + student.getName(), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open student or admin dashboard 
            if (studentNumber.equals("admin")) {
                new AdminDash(clientHandler).setVisible(true);  // Admin dashboard
            } else {
                new StudentDash(studentNumber, student.getName(), clientHandler).setVisible(true);  // Student dashboard
            }
            dispose();  // Close login window
        } else {
            // Login failed show error message
            JOptionPane.showMessageDialog(this, 
                "Invalid student number or password!", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            clientHandler.close();  // Close the failed connection
        }
    }
    
    // Main method runs application
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);  // Show login window
            }
        });
    }
}