/*
 * Database Tables Initialization - creates and sets up all required database tables
 * This class ensures the database structure exists before the application starts
 */

/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/

package com.DBConnection;

import com.DBConnection.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class DBTables {
    // Creates all necessary database tables and inserts initial data
    public static void initializeDatabase() {
        // Try-with-resources automatically closes database connections
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create students table to store student information
            String createStudentsTable = "CREATE TABLE students (" +
                    "student_number VARCHAR(20) PRIMARY KEY, " +  // Unique student ID
                    "name VARCHAR(100) NOT NULL, " +              // Student's full name
                    "password VARCHAR(100) NOT NULL)";            // Login password
            stmt.executeUpdate(createStudentsTable);
            
            // Create courses table to store course information
            String createCoursesTable = "CREATE TABLE courses (" +
                    "course_code VARCHAR(20) PRIMARY KEY, " +     // Unique course code
                    "title VARCHAR(100) NOT NULL, " +             // Course title
                    "description VARCHAR(500))";                  // Course description (optional)
            stmt.executeUpdate(createCoursesTable);
            
            // Create enrollments table to track which students are in which courses
            String createEnrollmentsTable = "CREATE TABLE enrollments (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +  // Auto-incrementing ID
                    "student_number VARCHAR(20), " +              // Foreign key to students table
                    "course_code VARCHAR(20), " +                 // Foreign key to courses table
                    "FOREIGN KEY (student_number) REFERENCES students(student_number), " +  // Enforce student exists
                    "FOREIGN KEY (course_code) REFERENCES courses(course_code))";           // Enforce course exists
            stmt.executeUpdate(createEnrollmentsTable);
            
            // Insert default admin user for system administration
            String insertAdmin = "INSERT INTO students (student_number, name, password) VALUES " +
                    "('admin', 'Administrator', 'admin123')";  // Default admin credentials
            stmt.executeUpdate(insertAdmin);
            
            System.out.println("Database initialized successfully!");
            
        } catch (Exception e) {
            // If tables already exist, this exception is expected and can be ignored
            System.out.println("Database already exists or error during setup: " + e.getMessage());
        }
    }
}