/*
 * Student Model Class - represents a student user in the enrollment system
 * This class stores student information and can be sent over the network
 */


/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/


package com.student;

import java.io.Serializable;

public class Student implements Serializable {
    private String studentNumber;  // Unique identifier for the student (like username)
    private String name;           // Full name of the student
    private String password;       // Password for logging in

    // Default constructor - required for serialization
    public Student() {}

    // Constructor to create a new student with all required information
    public Student(String studentNumber, String name, String password) {
        this.studentNumber = studentNumber;
        this.name = name;
        this.password = password;
    }

    // Getters and setters for accessing and modifying student properties
    
    // Get the student's ID number
    public String getStudentNumber() { 
        return studentNumber; 
    }
    
    // Set a new student number
    public void setStudentNumber(String studentNumber) { 
        this.studentNumber = studentNumber; 
    }
    
    // Get the student's full name
    public String getName() { 
        return name; 
    }
    
    // Set a new name for the student
    public void setName(String name) { 
        this.name = name; 
    }
    
    // Get the student's password (used for authentication)
    public String getPassword() { 
        return password; 
    }
    
    // Set a new password for the student
    public void setPassword(String password) { 
        this.password = password; 
    }

    // Convert student information to a readable string format
    // Used when displaying students in lists or messages
    @Override
    public String toString() {
        return name + " (" + studentNumber + ")";  // Format: "John Smith (S12345)"
    }
}