/*
 * Course Data Access Object - handles all database operations for courses
 * This class acts as a bridge between the application and the course database table
 */


/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/


package com.dao;

import com.student.Course;
import com.DBConnection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    
    //  Add a new course to the database
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, title, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set the values for the SQL query
            pstmt.setString(1, course.getCourseCode());    // Course code
            pstmt.setString(2, course.getTitle());         // Course title
            pstmt.setString(3, course.getDescription());   // Course description
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Return true if course was added successfully
        } catch (SQLException e) {
            System.err.println("❌ Error adding course: " + e.getMessage());
            return false;
        }
    }
    
    // Get a specific course by its course code
    public Course getCourseByCode(String courseCode) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // If course found, create and return Course object
                return new Course(
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting course: " + e.getMessage());
        }
        return null;  // Return null if course not found
    }
    
    //  Get all courses from the database
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();  // List to hold all courses
        String sql = "SELECT * FROM courses ORDER BY course_code";  // SQL to get all courses sorted by code
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Loop through all rows in the result set
            while (rs.next()) {
                // Create Course object for each row and add to list
                courses.add(new Course(
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getString("description")
                ));
            }
            System.out.println("✅ Found " + courses.size() + " courses in database");
        } catch (SQLException e) {
            System.err.println("❌ Error getting all courses: " + e.getMessage());
        }
        return courses;  // Return the list of all courses
    }
    
    //  Update course information in the database
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ? WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getTitle());         // New title
            pstmt.setString(2, course.getDescription());   // New description
            pstmt.setString(3, course.getCourseCode());    // Course code to update
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Return true if course was updated
        } catch (SQLException e) {
            System.err.println("❌ Error updating course: " + e.getMessage());
            return false;
        }
    }
    
    // DELETE - Delete a course from the database (and its enrollments)
    public boolean deleteCourse(String courseCode) {
        // First delete enrollments for this course to maintain referential integrity
        String deleteEnrollmentsSQL = "DELETE FROM enrollments WHERE course_code = ?";
        String deleteCourseSQL = "DELETE FROM courses WHERE course_code = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            // Start transaction to ensure both operations succeed or fail together
            conn.setAutoCommit(false);
            
            try {
                // Delete enrollments first (due to foreign key constraint)
                try (PreparedStatement pstmt = conn.prepareStatement(deleteEnrollmentsSQL)) {
                    pstmt.setString(1, courseCode);
                    pstmt.executeUpdate();  // Remove all enrollments for this course
                }
                
                // Delete course record
                try (PreparedStatement pstmt = conn.prepareStatement(deleteCourseSQL)) {
                    pstmt.setString(1, courseCode);
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        conn.commit();    // Save changes if course was deleted
                        System.out.println("✅ Course deleted: " + courseCode);
                        return true;
                    } else {
                        conn.rollback();  // Undo changes if course not found
                        System.out.println("❌ Course not found: " + courseCode);
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();  // Undo changes if any error occurs
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error deleting course: " + e.getMessage());
            return false;
        }
    }
    
    // Check if a course exists in the database
    public boolean courseExists(String courseCode) {
        String sql = "SELECT 1 FROM courses WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Returns true if course exists
        } catch (SQLException e) {
            System.err.println("❌ Error checking course existence: " + e.getMessage());
            return false;
        }
    }
    
    // Get the total number of courses in the system
    public int getCourseCount() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);  // Return the count
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting course count: " + e.getMessage());
        }
        return 0;
    }
    
    // Search for courses by title (supports partial matching)
    public List<Course> searchCoursesByTitle(String searchTerm) {
        List<Course> courses = new ArrayList<>();
        // SQL with LIKE operator for partial text matching
        String sql = "SELECT * FROM courses WHERE title LIKE ? ORDER BY course_code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");  // % allows partial matching
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                courses.add(new Course(
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching courses: " + e.getMessage());
        }
        return courses;
    }
}