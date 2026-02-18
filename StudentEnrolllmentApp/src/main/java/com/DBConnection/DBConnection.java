/*
 * Database Connection Manager - handles all connections to the Java DB database
 * This class provides a centralized way to connect to the database throughout the application
 */
   /* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/

package com.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database connection details - points to Java DB in NetBeans Services
    private static final String URL = "jdbc:derby://localhost:1527/StudentEnrollmentDB";
    private static final String USER = "StudentEnrollment";  // Database username
    private static final String PASSWORD = "1234";           // Database password
    
    // Static block that runs when the class is first loaded
    static {
        // Load Derby client driver to enable database connections
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            System.out.println("✅ Derby Client Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Failed to load Derby Client Driver: " + e.getMessage());
        }
    }

    // Provides a database connection to be used throughout the application
    public static Connection getConnection() throws SQLException {
        try {
            // Create and return a connection to the database
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to Java DB successfully");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to Java DB: " + e.getMessage());
            throw e;  // Re-throw the exception so calling code can handle it
        }
    }
}