/*
 * Client Handler - manages communication between client applications and the server
 * This class sends requests to the server and processes responses for the client side
 */


/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/

package com.client;

import com.student.Student;
import com.student.Course;
import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler {

    // Server connection details
    private static final String SERVER_HOST = "localhost";  // Server address
    private static final int SERVER_PORT = 12346;           // Server port

    private Socket socket;              // Connection to the server
    private ObjectOutputStream output;  // Stream for sending objects to server
    private ObjectInputStream input;    // Stream for receiving objects from server
    private boolean connected = false;  // Tracks connection status

    //  Automatically connects to server when created
    public ClientHandler() {
        connectToServer();
    }

    // Establishes connection to the server
    private void connectToServer() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);  // Connect to server
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;
            System.out.println("✅ Connected to server successfully");
        } catch (IOException e) {
            System.err.println("❌ Failed to connect to server: " + e.getMessage());
            connected = false;
        }
    }

    // Checks if connection to server is active
    public boolean isConnected() {
        return connected;
    }

    // Student methods
    // Verifies student login credentials with the server
    public Student authenticate(String studentNumber, String password) {
        if (!connected) {
            System.err.println("Not connected to server");
            return null;
        }

        try {
            output.writeObject("AUTHENTICATE");    // Tell server we want to authenticate
            output.writeObject(studentNumber);     // Send student ID
            output.writeObject(password);          // Send password
            output.flush();

            return (Student) input.readObject();   // Receive Student object or null
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }

    // Sends a request to add a new student to the database
    public boolean addStudent(Student student) {
        if (!connected) {
            return false;
        }

        try {
            output.writeObject("ADD_STUDENT");  // Tell server we want to add a student
            output.writeObject(student);        // Send the student object
            output.flush();

            return (Boolean) input.readObject(); // Receive success/failure
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Add student error: " + e.getMessage());
            return false;
        }
    }

    // Requests a list of all students from the server
    public List<Student> getAllStudents() {
        if (!connected) {
            System.err.println("❌ Not connected to server");
            return null;
        }

        try {
            output.writeObject("GET_ALL_STUDENTS");  // Request all students
            output.flush();

            List<Student> students = (List<Student>) input.readObject();
            System.out.println("✅ Received " + (students != null ? students.size() : 0) + " students from server");
            return students;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Get all students error: " + e.getMessage());
            return null;
        }
    }

    // Course methods
    
    // Sends a request to add a new course to the database
    public boolean addCourse(Course course) {
        if (!connected) {
            return false;
        }

        try {
            output.writeObject("ADD_COURSE");  // Tell server we want to add a course
            output.writeObject(course);        // Send the course object
            output.flush();

            return (Boolean) input.readObject(); // Receive success/failure
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Add course error: " + e.getMessage());
            return false;
        }
    }

    // Requests a list of all courses from the server
    public List<Course> getAllCourses() {
        if (!connected) {
            System.err.println("❌ Not connected to server");
            return null;
        }

        try {
            output.writeObject("GET_COURSES");  // Request all courses
            output.flush();

            List<Course> courses = (List<Course>) input.readObject();
            System.out.println("✅ Received " + (courses != null ? courses.size() : 0) + " courses from server");
            return courses;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Get all courses error: " + e.getMessage());
            return null;
        }
    }

    // Enrollment methods
    
    // Sends a request to enroll a student in a course
    public boolean enrollStudent(String studentNumber, String courseCode) {
        if (!connected) {
            return false;
        }

        try {
            output.writeObject("ENROLL_STUDENT");  // Tell server we want to enroll
            output.writeObject(studentNumber);     // Send student ID
            output.writeObject(courseCode);        // Send course code
            output.flush();

            return (Boolean) input.readObject();   // Receive success/failure
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Enrollment error: " + e.getMessage());
            return false;
        }
    }

    // Requests a list of all courses a specific student is enrolled in
    public List<Course> getStudentCourses(String studentNumber) {
        if (!connected) {
            return null;
        }

        try {
            output.writeObject("GET_STUDENT_COURSES");  // Request student's courses
            output.writeObject(studentNumber);          // Send student ID
            output.flush();

            return (List<Course>) input.readObject();   // Receive course list
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Get student courses error: " + e.getMessage());
            return null;
        }
    }

    // Requests a list of all students enrolled in a specific course
    public List<Student> getCourseStudents(String courseCode) {
        if (!connected) {
            System.err.println("❌ Not connected to server");
            return null;
        }

        try {
            output.writeObject("GET_COURSE_STUDENTS");  // Request course's students
            output.writeObject(courseCode);             // Send course code
            output.flush();

            List<Student> students = (List<Student>) input.readObject();
            System.out.println("✅ Received " + (students != null ? students.size() : 0) + " students for course " + courseCode);
            return students;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Get course students error: " + e.getMessage());
            return null;
        }
    }

    // Sends a request to delete a student from the database
    public boolean deleteStudent(String studentNumber) {
        if (!connected) {
            System.err.println("❌ Not connected to server");
            return false;
        }

        try {
            output.writeObject("DELETE_STUDENT");  // Tell server we want to delete a student
            output.writeObject(studentNumber);     // Send student ID to delete
            output.flush();

            boolean success = (Boolean) input.readObject();
            System.out.println("✅ Delete student result: " + success);
            return success;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Delete student error: " + e.getMessage());
            return false;
        }
    }

    // Sends a request to delete a course from the database
    public boolean deleteCourse(String courseCode) {
        if (!connected) {
            System.err.println("❌ Not connected to server");
            return false;
        }

        try {
            output.writeObject("DELETE_COURSE");  // Tell server we want to delete a course
            output.writeObject(courseCode);       // Send course code to delete
            output.flush();

            boolean success = (Boolean) input.readObject();
            System.out.println("✅ Delete course result: " + success);
            return success;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Delete course error: " + e.getMessage());
            return false;
        }
    }

    
    
    // Requests a list of all enrollment records from the server
    public List<String[]> getAllEnrollments() {
        if (!connected) {
            System.err.println("❌ Not connected to server");
            return null;
        }

        try {
            output.writeObject("GET_ALL_ENROLLMENTS");  // Request all enrollments
            output.flush();

            List<String[]> enrollments = (List<String[]>) input.readObject();
            System.out.println("✅ Received " + (enrollments != null ? enrollments.size() : 0) + " enrollments from server");
            return enrollments;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Get all enrollments error: " + e.getMessage());
            return null;
        }
    }

    // Cleanly closes the connection to the server
    public void close() {
        try {
            if (output != null) {
                output.writeObject("EXIT");  // Notify server we're disconnecting
                output.flush();
            }
            if (output != null) {
                output.close();  // Close output stream
            }
            if (input != null) {
                input.close();   // Close input stream
            }
            if (socket != null) {
                socket.close();  // Close socket connection
            }
            connected = false;
            System.out.println("🔌 Disconnected from server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}