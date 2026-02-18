/*
 * Main Server Class - handles client connections and coordinates all system operations
 * This is the backbone of the Student Enrollment System that clients connect to
 */


/* Authors
    Redah Gamieldien-222641681
    Qaasim Isaacs- 222544422
    Angelo Adams- 230450431*/



package com.server;

import com.DBConnection.DBTables;
import com.dao.*;
import com.student.Student;
import com.student.Course;
import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    private static final int PORT = 12346;  // Port number the server checks on
    private ServerSocket serverSocket;      // Checks for incoming client connections
    private boolean running;                // Controls whether server should keep running
    
    //  Handles database operations
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;

    //  initializes the server with database connections
    public Server() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    // Starts the server and begins accepting client connections
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);  // Create server socket on specified port
            running = true;
            System.out.println("✅ Server started on port " + PORT);
            
            //  Server loop it continuously accepts new client connections
            while (running) {
                Socket clientSocket = serverSocket.accept();  // Waits for client to connect
                System.out.println("🔗 Client connected: " + clientSocket.getInetAddress());
                
                // Handle each client in a separate thread so multiple clients can connect at once
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }

    // Class that handles the communication with a single client
    private class ClientHandler extends Thread {
        private Socket clientSocket;    // Connection to the specific client
        private ObjectInputStream input;   // Stream for receiving objects from client
        private ObjectOutputStream output; // Stream for sending objects to client

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        // Main method that runs in a separate thread for each client
        @Override
        public void run() {
            try {
                // Set up communication streams with the client
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                input = new ObjectInputStream(clientSocket.getInputStream());
                
                // Continuously process client requests until they disconnect
                while (true) {
                    String action = (String) input.readObject();  // Reads what the client wants to do
                    System.out.println("📨 Received action: " + action);
                    
                    // Takes the request and goes to the appropriate handler based on the action
                    switch (action) {
                        case "AUTHENTICATE":
                            handleAuthentication();  // Verify login credentials
                            break;
                        case "GET_COURSES":
                            handleGetCourses();      // Get all courses
                            break;
                        case "GET_ALL_STUDENTS":
                            handleGetAllStudents();  // Get all students (admin only)
                            break;
                        case "ENROLL_STUDENT":
                            handleEnrollStudent();   // Enroll student in course
                            break;
                        case "ADD_STUDENT":
                            handleAddStudent();      // Add new student
                            break;
                        case "ADD_COURSE":
                            handleAddCourse();       // Add new course
                            break;
                        case "GET_STUDENT_COURSES":
                            handleGetStudentCourses();  // Get courses for a student
                            break;
                        case "GET_COURSE_STUDENTS":
                            handleGetCourseStudents();  // Get students in a course
                            break;
                        case "DELETE_STUDENT":
                            handleDeleteStudent();   // Delete a student
                            break;
                        case "DELETE_COURSE":
                            handleDeleteCourse();    // Delete a course
                            break;
                        case "GET_ALL_ENROLLMENTS":
                            handleGetAllEnrollments();  // Get all enrollment records
                            break;
                        case "EXIT":
                            System.out.println("🔌 Client requested disconnect");
                            return;  // End this client session
                        default:
                            System.out.println("❌ Unknown action: " + action);
                            output.writeObject(null);  // Send null for unknown actions
                            output.flush();
                    }
                }
            } catch (EOFException e) {
                System.out.println("🔌 Client disconnected unexpectedly");
            } catch (SocketException e) {
                System.out.println("🔌 Client connection closed");
            } catch (Exception e) {
                System.err.println("❌ Client handler error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Always clean up resources when client disconnects
                try {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("❌ Error closing client socket: " + e.getMessage());
                }
            }
        }

        // Handles student login authentication requests
        private void handleAuthentication() throws IOException, ClassNotFoundException {
            String studentNumber = (String) input.readObject();  // Get student ID from client
            String password = (String) input.readObject();       // Get password from client
            
            System.out.println("🔐 Authentication attempt: " + studentNumber);
            
            // Verify credentials in database
            Student student = studentDAO.authenticate(studentNumber, password);
            output.writeObject(student);  // Send result back to client
            output.flush();
            
            if (student != null) {
                System.out.println("✅ Authentication successful for: " + studentNumber);
            } else {
                System.out.println("❌ Authentication failed for: " + studentNumber);
            }
        }

        // Handles requests for all courses in the system
        private void handleGetCourses() throws IOException {
            try {
                List<Course> courses = courseDAO.getAllCourses();
                output.writeObject(courses);  // Send course list to client
                output.flush();
                System.out.println("📚 Sent " + courses.size() + " courses to client");
            } catch (Exception e) {
                System.err.println("❌ Error in handleGetCourses: " + e.getMessage());
                output.writeObject(null);  // Send null if error occurs
                output.flush();
            }
        }

        // Handles requests for all students in the system can only be used ny the admin
        private void handleGetAllStudents() throws IOException {
            try {
                List<Student> students = studentDAO.getAllStudents();
                output.writeObject(students);
                output.flush();
                System.out.println("👥 Sent " + students.size() + " students to client");
            } catch (Exception e) {
                System.err.println("❌ Error in handleGetAllStudents: " + e.getMessage());
                output.writeObject(null);
                output.flush();
            }
        }

        // Handles student enrollment in courses
        private void handleEnrollStudent() throws IOException, ClassNotFoundException {
            try {
                String studentNumber = (String) input.readObject();  // Get student ID
                String courseCode = (String) input.readObject();     // Get course code
                
                boolean success = enrollmentDAO.enrollStudent(studentNumber, courseCode);
                output.writeObject(success);  // Send success/failure result
                output.flush();
                
                if (success) {
                    System.out.println("✅ Enrollment successful: " + studentNumber + " in " + courseCode);
                } else {
                    System.out.println("❌ Enrollment failed: " + studentNumber + " in " + courseCode);
                }
            } catch (Exception e) {
                System.err.println("❌ Error in handleEnrollStudent: " + e.getMessage());
                output.writeObject(false);  // Send false if error occurs
                output.flush();
            }
        }

        // Handles adding new students to the system
        private void handleAddStudent() throws IOException, ClassNotFoundException {
            try {
                Student student = (Student) input.readObject();  // Get student object from client
                boolean success = studentDAO.addStudent(student);
                output.writeObject(success);
                output.flush();
                
                if (success) {
                    System.out.println("✅ Student added: " + student.getStudentNumber());
                } else {
                    System.out.println("❌ Failed to add student: " + student.getStudentNumber());
                }
            } catch (Exception e) {
                System.err.println("❌ Error in handleAddStudent: " + e.getMessage());
                output.writeObject(false);
                output.flush();
            }
        }

        // Handles adding new courses to the system
        private void handleAddCourse() throws IOException, ClassNotFoundException {
            try {
                Course course = (Course) input.readObject();  // Get course object from client
                boolean success = courseDAO.addCourse(course);
                output.writeObject(success);
                output.flush();
                
                if (success) {
                    System.out.println("✅ Course added: " + course.getCourseCode());
                } else {
                    System.out.println("❌ Failed to add course: " + course.getCourseCode());
                }
            } catch (Exception e) {
                System.err.println("❌ Error in handleAddCourse: " + e.getMessage());
                output.writeObject(false);
                output.flush();
            }
        }

        // Handles requests for courses a specific student is enrolled in
        private void handleGetStudentCourses() throws IOException, ClassNotFoundException {
            try {
                String studentNumber = (String) input.readObject();
                List<Course> courses = enrollmentDAO.getStudentCourses(studentNumber);
                output.writeObject(courses);
                output.flush();
                System.out.println("📖 Sent " + courses.size() + " courses for student: " + studentNumber);
            } catch (Exception e) {
                System.err.println("❌ Error in handleGetStudentCourses: " + e.getMessage());
                output.writeObject(null);
                output.flush();
            }
        }

        // Handles requests for students enrolled in a specific course
        private void handleGetCourseStudents() throws IOException, ClassNotFoundException {
            try {
                String courseCode = (String) input.readObject();
                List<Student> students = enrollmentDAO.getCourseStudents(courseCode);
                output.writeObject(students);
                output.flush();
                System.out.println("👥 Sent " + students.size() + " students for course: " + courseCode);
            } catch (Exception e) {
                System.err.println("❌ Error in handleGetCourseStudents: " + e.getMessage());
                output.writeObject(null);
                output.flush();
            }
        }

        // Handles deleting students from the database
        private void handleDeleteStudent() throws IOException, ClassNotFoundException {
            try {
                String studentNumber = (String) input.readObject();
                boolean success = studentDAO.deleteStudent(studentNumber);
                output.writeObject(success);
                output.flush();
                
                if (success) {
                    System.out.println("✅ Student deleted: " + studentNumber);
                } else {
                    System.out.println("❌ Failed to delete student: " + studentNumber);
                }
            } catch (Exception e) {
                System.err.println("❌ Error in handleDeleteStudent: " + e.getMessage());
                output.writeObject(false);
                output.flush();
            }
        }

        // Handles deleting courses from the system
        private void handleDeleteCourse() throws IOException, ClassNotFoundException {
            try {
                String courseCode = (String) input.readObject();
                boolean success = courseDAO.deleteCourse(courseCode);
                output.writeObject(success);
                output.flush();
                
                if (success) {
                    System.out.println("✅ Course deleted: " + courseCode);
                } else {
                    System.out.println("❌ Failed to delete course: " + courseCode);
                }
            } catch (Exception e) {
                System.err.println("❌ Error in handleDeleteCourse: " + e.getMessage());
                output.writeObject(false);
                output.flush();
            }
        }

        // Handles requests for all enrollment records in the database
        private void handleGetAllEnrollments() throws IOException {
            try {
                List<String[]> enrollments = enrollmentDAO.getAllEnrollments();
                output.writeObject(enrollments);
                output.flush();
                System.out.println("📊 Sent " + enrollments.size() + " enrollments to client");
            } catch (Exception e) {
                System.err.println("❌ Error in handleGetAllEnrollments: " + e.getMessage());
                output.writeObject(null);
                output.flush();
            }
        }
    }

    // Main method to run application
    public static void main(String[] args) {
        System.out.println("🔄 Initializing database...");
        DBTables.initializeDatabase();  // Ensure database tables exist
        
        System.out.println("🚀 Starting server...");
        new Server().start();  // Create and start the server
    }
}