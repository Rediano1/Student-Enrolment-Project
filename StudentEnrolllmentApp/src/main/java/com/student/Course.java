/*
 * Course Model Class - represents a course in the enrollment system
 * This class stores all information about a course and can be sent over the network
 */

/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/


package com.student;

import java.io.Serializable;

public class Course implements Serializable {
    private String courseCode;    // Unique identifier for the course (e.g., "CS101")
    private String title;         // Full name of the course (e.g., "Introduction to Computer Science")
    private String description;   // Detailed description of what the course covers

    // Default constructor - required for serialization
    public Course() {}

    // Constructor to create a new course with all details
    public Course(String courseCode, String title, String description) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
    }

    // Getters and setters for accessing and modifying course properties
    
    // Get the course code (unique identifier)
    public String getCourseCode() { 
        return courseCode; 
    }
    
    // Set a new course code
    public void setCourseCode(String courseCode) { 
        this.courseCode = courseCode; 
    }
    
    // Get the full title of the course
    public String getTitle() { 
        return title; 
    }
    
    // Set a new title for the course
    public void setTitle(String title) { 
        this.title = title; 
    }
    
    // Get the course description
    public String getDescription() { 
        return description; 
    }
    
    // Set a new description for the course
    public void setDescription(String description) { 
        this.description = description; 
    }

    // Convert course information to a readable string format
    // Used when displaying courses in lists or dropdowns
    @Override
    public String toString() {
        return courseCode + " - " + title;  // Format: "CS101 - Introduction to Computer Science"
    }
}