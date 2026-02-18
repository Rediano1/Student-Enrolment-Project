/*
 * Student Data Access Object - handles all database operations for students
 * This class acts as a bridge between the application and the student database table
 */


/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/


package com.dao;

import com.student.Student;
import com.DBConnection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // Adds a new student to the database
    public boolean addStudent(Student student) {
        // SQL query to insert a new student record
        String sql = "INSERT INTO students (student_number, name, password) VALUES (?, ?, ?)";
        
        // Try-with-resources automatically closes the connection
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the parameter values for the SQL query
            pstmt.setString(1, student.getStudentNumber());  // Student ID
            pstmt.setString(2, student.getName());           // Student name
            pstmt.setString(3, student.getPassword());       // Student password

            // Execute the insert and return true if successful (rows affected > 0)
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if any error occurs
        }
    }

    // Verifies student login credentials against the database
    public Student authenticate(String studentNumber, String password) {
        // SQL query to find student with matching credentials
        String sql = "SELECT * FROM students WHERE student_number = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the search parameters
            pstmt.setString(1, studentNumber);  // Student ID to search for
            pstmt.setString(2, password);       // Password to verify

            // Execute query and process results
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // If student found, create and return Student object
                return new Student(
                        rs.getString("student_number"),  // Get student number from result
                        rs.getString("name"),            // Get name from result
                        rs.getString("password")         // Get password from result
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Return null if no matching student found
    }

    // Retrieves all students from the database
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();  // List to hold all students
        String sql = "SELECT * FROM students";       // SQL to get all students
        
        try (Connection conn = DBConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop through all rows in the result set
            while (rs.next()) {
                // Create Student object for each row and add to list
                students.add(new Student(
                        rs.getString("student_number"),  // Student ID from database
                        rs.getString("name"),            // Student name from database
                        rs.getString("password")         // Student password from database
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;  // Return the list of all students
    }

    // Deletes a student from the database (and their enrollments)
    public boolean deleteStudent(String studentNumber) {
        // First delete enrollments for this student to maintain referential integrity
        String deleteEnrollmentsSQL = "DELETE FROM enrollments WHERE student_number = ?";
        String deleteStudentSQL = "DELETE FROM students WHERE student_number = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Start transaction to ensure both operations succeed or fail together
            conn.setAutoCommit(false);

            try {
                // Delete enrollments first (due to foreign key constraint)
                try (PreparedStatement pstmt = conn.prepareStatement(deleteEnrollmentsSQL)) {
                    pstmt.setString(1, studentNumber);
                    pstmt.executeUpdate();  // Remove all enrollments for this student
                }

                // Delete student record
                try (PreparedStatement pstmt = conn.prepareStatement(deleteStudentSQL)) {
                    pstmt.setString(1, studentNumber);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        conn.commit();    // Save changes if student was deleted
                        return true;
                    } else {
                        conn.rollback();  // Undo changes if student not found
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();  // Undo changes if any error occurs
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }
}