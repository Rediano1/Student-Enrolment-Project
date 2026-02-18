/*
 * Enrollment Data Access Object - handles all database operations for course enrollments
 * Manages the relationship between students and courses in the enrollment system
 */


/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/


package com.dao;

import com.student.Student;
import com.student.Course;
import com.DBConnection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    
    // CREATE - Enroll a student in a course
    public boolean enrollStudent(String studentNumber, String courseCode) {
        // Check if student is already enrolled to prevent duplicates
        if (isEnrolled(studentNumber, courseCode)) {
            System.out.println("❌ Student " + studentNumber + " is already enrolled in " + courseCode);
            return false;
        }
        
        // SQL query to insert new enrollment record
        String sql = "INSERT INTO enrollments (student_number, course_code) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters for the enrollment
            pstmt.setString(1, studentNumber);  // Student ID
            pstmt.setString(2, courseCode);     // Course code
            
            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Student " + studentNumber + " enrolled in " + courseCode);
            } else {
                System.out.println("❌ Failed to enroll student " + studentNumber + " in " + courseCode);
            }
            
            return success;
        } catch (SQLException e) {
            System.err.println("❌ Error enrolling student: " + e.getMessage());
            return false;
        }
    }
    
    // READ - Check if a student is already enrolled in a specific course
    public boolean isEnrolled(String studentNumber, String courseCode) {
        String sql = "SELECT 1 FROM enrollments WHERE student_number = ? AND course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            pstmt.setString(2, courseCode);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Returns true if a record exists (student is enrolled)
        } catch (SQLException e) {
            System.err.println("❌ Error checking enrollment: " + e.getMessage());
            return false;
        }
    }
    
    // READ - Get all courses that a specific student is enrolled in
    public List<Course> getStudentCourses(String studentNumber) {
        List<Course> courses = new ArrayList<>();
        // SQL join to get course details for a student's enrollments
        String sql = "SELECT c.* FROM courses c " +
                    "JOIN enrollments e ON c.course_code = e.course_code " +
                    "WHERE e.student_number = ? " +
                    "ORDER BY c.course_code";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            
            // Create Course objects for each enrolled course
            while (rs.next()) {
                courses.add(new Course(
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getString("description")
                ));
            }
            System.out.println("✅ Found " + courses.size() + " courses for student " + studentNumber);
        } catch (SQLException e) {
            System.err.println("❌ Error getting student courses: " + e.getMessage());
        }
        return courses;
    }
    
    // READ - Get all students enrolled in a specific course
    public List<Student> getCourseStudents(String courseCode) {
        List<Student> students = new ArrayList<>();
        // SQL join to get student details for a course's enrollments
        String sql = "SELECT s.* FROM students s " +
                    "JOIN enrollments e ON s.student_number = e.student_number " +
                    "WHERE e.course_code = ? " +
                    "ORDER BY s.student_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            
            // Create Student objects for each enrolled student
            while (rs.next()) {
                students.add(new Student(
                    rs.getString("student_number"),
                    rs.getString("name"),
                    rs.getString("password")
                ));
            }
            System.out.println("✅ Found " + students.size() + " students in course " + courseCode);
        } catch (SQLException e) {
            System.err.println("❌ Error getting course students: " + e.getMessage());
        }
        return students;
    }
    
    // DELETE - Unenroll a student from a course
    public boolean unenrollStudent(String studentNumber, String courseCode) {
        String sql = "DELETE FROM enrollments WHERE student_number = ? AND course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            pstmt.setString(2, courseCode);
            
            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Student " + studentNumber + " unenrolled from " + courseCode);
            } else {
                System.out.println("❌ Failed to unenroll student " + studentNumber + " from " + courseCode);
            }
            
            return success;
        } catch (SQLException e) {
            System.err.println("❌ Error unenrolling student: " + e.getMessage());
            return false;
        }
    }
    
    // DELETE - Remove all enrollments for a specific student (used when deleting student)
    public boolean removeAllStudentEnrollments(String studentNumber) {
        String sql = "DELETE FROM enrollments WHERE student_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.println("✅ Removed " + rowsAffected + " enrollments for student " + studentNumber);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error removing student enrollments: " + e.getMessage());
            return false;
        }
    }
    
    // DELETE - Remove all enrollments for a specific course (used when deleting course)
    public boolean removeAllCourseEnrollments(String courseCode) {
        String sql = "DELETE FROM enrollments WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.println("✅ Removed " + rowsAffected + " enrollments for course " + courseCode);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error removing course enrollments: " + e.getMessage());
            return false;
        }
    }
    
    // Get the number of courses a student is enrolled in
    public int getStudentEnrollmentCount(String studentNumber) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);  // Return the count
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting student enrollment count: " + e.getMessage());
        }
        return 0;
    }
    
    // Get the number of students enrolled in a course
    public int getCourseEnrollmentCount(String courseCode) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);  // Return the count
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting course enrollment count: " + e.getMessage());
        }
        return 0;
    }
    
    // Get all enrollment records for reporting purposes
    public List<String[]> getAllEnrollments() {
        List<String[]> enrollments = new ArrayList<>();
        // Complex join to get comprehensive enrollment information
        String sql = "SELECT e.student_number, s.name as student_name, e.course_code, c.title as course_title " +
                    "FROM enrollments e " +
                    "JOIN students s ON e.student_number = s.student_number " +
                    "JOIN courses c ON e.course_code = c.course_code " +
                    "ORDER BY e.student_number, e.course_code";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Create string arrays for each enrollment record
            while (rs.next()) {
                String[] enrollment = {
                    rs.getString("student_number"),   // Student ID
                    rs.getString("student_name"),     // Student name
                    rs.getString("course_code"),      // Course code
                    rs.getString("course_title")      // Course title
                };
                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all enrollments: " + e.getMessage());
        }
        return enrollments;
    }
}