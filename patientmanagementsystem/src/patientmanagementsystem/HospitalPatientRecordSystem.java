package patientmanagementsystem;
import java.sql.*;
import java.util.Scanner;

public class HospitalPatientRecordSystem {
	

	    private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
	    private static final String USER = "root";
	    private static final String PASSWORD = "root"; // Replace with your MySQL password
	    private static Connection connection;
	    private static boolean isLoggedIn = false;
	    private static String currentUser;
	    private static String userRole;
	    private static int patientIdForCurrentUser = -1; // Tracks patient ID for logged-in Patient role

	    public static void main(String[] args) {
	        try {
	            // Load MySQL JDBC Driver
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            // Establish connection
	            connection = DriverManager.getConnection(URL, USER, PASSWORD);
	            System.out.println("Connected to database successfully!");

	            // Create tables
	            createUsersTable();
	            createPatientsTable();
	            createDoctorsTable();
	            createAppointmentsTable();

	            // Scanner for user input
	            Scanner scanner = new Scanner(System.in);
	            while (true) {
	                if (!isLoggedIn) {
	                    System.out.println("\nHospital System Login");
	                    System.out.println("1. Register");
	                    System.out.println("2. Login");
	                    System.out.println("3. Exit");
	                    System.out.print("Enter choice: ");

	                    int choice = scanner.nextInt();
	                    scanner.nextLine(); // Consume newline

	                    switch (choice) {
	                        case 1:
	                            registerUser(scanner);
	                            break;
	                        case 2:
	                            loginUser(scanner);
	                            break;
	                        case 3:
	                            System.out.println("Exiting...");
	                            connection.close();
	                            scanner.close();
	                            return;
	                        default:
	                            System.out.println("Invalid choice!");
	                    }
	                } else {
	                    if (userRole.equals("Doctor")) {
	                        doctorMenu(scanner);
	                    } else if (userRole.equals("Patient")) {
	                        patientMenu(scanner);
	                    } else if (userRole.equals("User")) {
	                        userMenu(scanner);
	                    }
	                }
	            }
	        } catch (ClassNotFoundException e) {
	            System.out.println("JDBC Driver not found: " + e.getMessage());
	        } catch (SQLException e) {
	            System.out.println("Database error: " + e.getMessage());
	        }
	    }

	    private static void createUsersTable() throws SQLException {
	        String sql = "CREATE TABLE IF NOT EXISTS users (" +
	                     "id INT AUTO_INCREMENT PRIMARY KEY," +
	                     "username VARCHAR(50) UNIQUE NOT NULL," +
	                     "password VARCHAR(50) NOT NULL," +
	                     "role VARCHAR(20) NOT NULL)"; // Added role column
	        try (Statement stmt = connection.createStatement()) {
	            stmt.execute(sql);
	            System.out.println("Users table ready.");
	        }
	    }

	    private static void createPatientsTable() throws SQLException {
	        String sql = "CREATE TABLE IF NOT EXISTS patients (" +
	                     "id INT AUTO_INCREMENT PRIMARY KEY," +
	                     "name VARCHAR(100) NOT NULL," +
	                     "age INT NOT NULL," +
	                     "diagnosis VARCHAR(255) NOT NULL," +
	                     "user_id INT, FOREIGN KEY (user_id) REFERENCES users(id))";
	        try (Statement stmt = connection.createStatement()) {
	            stmt.execute(sql);
	            System.out.println("Patients table ready.");
	        }
	    }

	    private static void createDoctorsTable() throws SQLException {
	        String sql = "CREATE TABLE IF NOT EXISTS doctors (" +
	                     "id INT AUTO_INCREMENT PRIMARY KEY," +
	                     "name VARCHAR(100) NOT NULL," +
	                     "specialization VARCHAR(100) NOT NULL)";
	        try (Statement stmt = connection.createStatement()) {
	            stmt.execute(sql);
	            System.out.println("Doctors table ready.");
	        }
	    }

	    private static void createAppointmentsTable() throws SQLException {
	        String sql = "CREATE TABLE IF NOT EXISTS appointments (" +
	                     "id INT AUTO_INCREMENT PRIMARY KEY," +
	                     "patient_id INT, FOREIGN KEY (patient_id) REFERENCES patients(id)," +
	                     "doctor_id INT, FOREIGN KEY (doctor_id) REFERENCES doctors(id)," +
	                     "appointment_date VARCHAR(50) NOT NULL)";
	        try (Statement stmt = connection.createStatement()) {
	            stmt.execute(sql);
	            System.out.println("Appointments table ready.");
	        }
	    }

	    private static void registerUser(Scanner scanner) throws SQLException {
	        System.out.print("Enter username: ");
	        String username = scanner.nextLine();
	        System.out.print("Enter password: ");
	        String password = scanner.nextLine();
	        System.out.println("Select role: 1. Doctor, 2. Patient, 3. User");
	        System.out.print("Enter choice: ");
	        int roleChoice = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	        String role = switch (roleChoice) {
	            case 1 -> "Doctor";
	            case 2 -> "Patient";
	            case 3 -> "User";
	            default -> {
	                System.out.println("Invalid role! Defaulting to User.");
	                yield "User";
	            }
	        };

	        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            pstmt.setString(1, username);
	            pstmt.setString(2, password);
	            pstmt.setString(3, role);
	            int rows = pstmt.executeUpdate();
	            System.out.println(rows + " user(s) registered. Please login.");
	            if (role.equals("Patient")) {
	                ResultSet rs = pstmt.getGeneratedKeys();
	                if (rs.next()) {
	                    int userId = rs.getInt(1);
	                    registerAsPatient(scanner, userId);
	                }
	            }
	        } catch (SQLException e) {
	            if (e.getErrorCode() == 1062) { // Duplicate entry
	                System.out.println("Username already exists!");
	            } else {
	                System.out.println("Registration error: " + e.getMessage());
	            }
	        }
	    }

	    private static void loginUser(Scanner scanner) throws SQLException {
	        System.out.print("Enter username: ");
	        String username = scanner.nextLine();
	        System.out.print("Enter password: ");
	        String password = scanner.nextLine();

	        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setString(1, username);
	            pstmt.setString(2, password);
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next()) {
	                isLoggedIn = true;
	                currentUser = username;
	                userRole = rs.getString("role");
	                if (userRole.equals("Patient")) {
	                    // Fetch patient ID for the logged-in user
	                    String patientSql = "SELECT id FROM patients WHERE user_id = ?";
	                    try (PreparedStatement patientPstmt = connection.prepareStatement(patientSql)) {
	                        patientPstmt.setInt(1, rs.getInt("id"));
	                        ResultSet patientRs = patientPstmt.executeQuery();
	                        if (patientRs.next()) {
	                            patientIdForCurrentUser = patientRs.getInt("id");
	                        }
	                    }
	                }
	                System.out.println("Login successful! Role: " + userRole);
	            } else {
	                System.out.println("Invalid username or password!");
	            }
	        }
	    }

	    private static void doctorMenu(Scanner scanner) throws SQLException {
	        while (true) {
	            System.out.println("\nDoctor Menu (Logged in as: " + currentUser + ")");
	            System.out.println("1. Manage Patients");
	            System.out.println("2. View Appointments");
	            System.out.println("3. Logout");
	            System.out.print("Enter choice: ");

	            int choice = scanner.nextInt();
	            scanner.nextLine(); // Consume newline

	            switch (choice) {
	                case 1:
	                    managePatients(scanner);
	                    break;
	                case 2:
	                    viewAppointmentsForDoctor();
	                    break;
	                case 3:
	                    System.out.println("Logging out...");
	                    isLoggedIn = false;
	                    currentUser = null;
	                    userRole = null;
	                    patientIdForCurrentUser = -1;
	                    return;
	                default:
	                    System.out.println("Invalid choice!");
	            }
	        }
	    }

	    private static void patientMenu(

	Scanner scanner) throws SQLException {
	        while (true) {
	            System.out.println("\nPatient Menu (Logged in as: " + currentUser + ")");
	            System.out.println("1. Update My Patient Info");
	            System.out.println("2. Book Appointment");
	            System.out.println("3. View My Appointments");
	            System.out.println("4. Logout");
	            System.out.print("Enter choice: ");

	            int choice = scanner.nextInt();
	            scanner.nextLine(); // Consume newline

	            switch (choice) {
	                case 1:
	                    updatePatient(scanner, patientIdForCurrentUser);
	                    break;
	                case 2:
	                    bookAppointment(scanner, patientIdForCurrentUser);
	                    break;
	                case 3:
	                    viewAppointmentsForPatient(patientIdForCurrentUser);
	                    break;
	                case 4:
	                    System.out.println("Logging out...");
	                    isLoggedIn = false;
	                    currentUser = null;
	                    userRole = null;
	                    patientIdForCurrentUser = -1;
	                    return;
	                default:
	                    System.out.println("Invalid choice!");
	            }
	        }
	    }

	    private static void userMenu(Scanner scanner) throws SQLException {
	        while (true) {
	            System.out.println("\nUser Menu (Logged in as: " + currentUser + ")");
	            System.out.println("1. Register as Patient");
	            System.out.println("2. Book Appointment");
	            System.out.println("3. Logout");
	            System.out.print("Enter choice: ");

	            int choice = scanner.nextInt();
	            scanner.nextLine(); // Consume newline

	            switch (choice) {
	                case 1:
	                    String sql = "SELECT id FROM users WHERE username = ?";
	                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	                        pstmt.setString(1, currentUser);
	                        ResultSet rs = pstmt.executeQuery();
	                        if (rs.next()) {
	                            registerAsPatient(scanner, rs.getInt("id"));
	                            userRole = "Patient"; // Update role to Patient
	                            patientIdForCurrentUser = getPatientIdForUser(rs.getInt("id"));
	                        }
	                    }
	                    break;
	                case 2:
	                    if (patientIdForCurrentUser == -1) {
	                        System.out.println("You must register as a patient first!");
	                    } else {
	                        bookAppointment(scanner, patientIdForCurrentUser);
	                    }
	                    break;
	                case 3:
	                    System.out.println("Logging out...");
	                    isLoggedIn = false;
	                    currentUser = null;
	                    userRole = null;
	                    patientIdForCurrentUser = -1;
	                    return;
	                default:
	                    System.out.println("Invalid choice!");
	            }
	        }
	    }

	    private static void registerAsPatient(Scanner scanner, int userId) throws SQLException {
	        System.out.print("Enter patient name: ");
	        String name = scanner.nextLine();
	        System.out.print("Enter patient age: ");
	        int age = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	        System.out.print("Enter patient diagnosis: ");
	        String diagnosis = scanner.nextLine();

	        String sql = "INSERT INTO patients (name, age, diagnosis, user_id) VALUES (?, ?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            pstmt.setString(1, name);
	            pstmt.setInt(2, age);
	            pstmt.setString(3, diagnosis);
	            pstmt.setInt(4, userId);
	            int rows = pstmt.executeUpdate();
	            System.out.println(rows + " patient(s) registered.");
	            ResultSet rs = pstmt.getGeneratedKeys();
	            if (rs.next()) {
	                patientIdForCurrentUser = rs.getInt(1);
	            }
	        }
	    }

	    private static int getPatientIdForUser(int userId) throws SQLException {
	        String sql = "SELECT id FROM patients WHERE user_id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setInt(1, userId);
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next()) {
	                return rs.getInt("id");
	            }
	        }
	        return -1;
	    }

	    private static void managePatients(Scanner scanner) throws SQLException {
	        while (true) {
	            System.out.println("\nPatient Management");
	            System.out.println("1. Add Patient");
	            System.out.println("2. View All Patients");
	            System.out.println("3. Update Patient");
	            System.out.println("4. Delete Patient");
	            System.out.println("5. Back");
	            System.out.print("Enter choice: ");

	            int choice = scanner.nextInt();
	            scanner.nextLine(); // Consume newline

	            switch (choice) {
	                case 1:
	                    addPatient(scanner);
	                    break;
	                case 2:
	                    viewPatients();
	                    break;
	                case 3:
	                    System.out.print("Enter patient ID to update: ");
	                    int patientId = scanner.nextInt();
	                    scanner.nextLine(); // Consume newline
	                    updatePatient(scanner, patientId);
	                    break;
	                case 4:
	                    deletePatient(scanner);
	                    break;
	                case 5:
	                    return;
	                default:
	                    System.out.println("Invalid choice!");
	            }
	        }
	    }

	    private static void addPatient(Scanner scanner) throws SQLException {
	        System.out.print("Enter patient name: ");
	        String name = scanner.nextLine();
	        System.out.print("Enter patient age: ");
	        int age = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	        System.out.print("Enter patient diagnosis: ");
	        String diagnosis = scanner.nextLine();

	        String sql = "INSERT INTO patients (name, age, diagnosis, user_id) VALUES (?, ?, ?, NULL)";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setString(1, name);
	            pstmt.setInt(2, age);
	            pstmt.setString(3, diagnosis);
	            int rows = pstmt.executeUpdate();
	            System.out.println(rows + " patient(s) added.");
	        }
	    }

	    private static void viewPatients() throws SQLException {
	        String sql = "SELECT * FROM patients";
	        try (Statement stmt = connection.createStatement();
	             ResultSet rs = stmt.executeQuery(sql)) {
	            System.out.println("\nPatient Records:");
	            System.out.println("ID | Name | Age | Diagnosis");
	            System.out.println("---|------|-----|----------");
	            while (rs.next()) {
	                System.out.printf("%d | %s | %d | %s%n",
	                        rs.getInt("id"),
	                        rs.getString("name"),
	                        rs.getInt("age"),
	                        rs.getString("diagnosis"));
	            }
	        }
	    }

	    private static void updatePatient(Scanner scanner, int patientId) throws SQLException {
	        System.out.print("Enter new name (leave blank to keep unchanged): ");
	        String name = scanner.nextLine();
	        System.out.print("Enter new age (enter 0 to keep unchanged): ");
	        int age = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	        System.out.print("Enter new diagnosis (leave blank to keep unchanged): ");
	        String diagnosis = scanner.nextLine();

	        StringBuilder sql = new StringBuilder("UPDATE patients SET ");
	        boolean first = true;
	        if (!name.isEmpty()) {
	            sql.append("name = ?");
	            first = false;
	        }
	        if (age > 0) {
	            if (!first) sql.append(", ");
	            sql.append("age = ?");
	            first = false;
	        }
	        if (!diagnosis.isEmpty()) {
	            if (!first) sql.append(", ");
	            sql.append("diagnosis = ?");
	        }
	        sql.append(" WHERE id = ?");

	        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
	            int paramIndex = 1;
	            if (!name.isEmpty()) pstmt.setString(paramIndex++, name);
	            if (age > 0) pstmt.setInt(paramIndex++, age);
	            if (!diagnosis.isEmpty()) pstmt.setString(paramIndex++, diagnosis);
	            pstmt.setInt(paramIndex, patientId);
	            int rows = pstmt.executeUpdate();
	            System.out.println(rows + " patient(s) updated.");
	        }
	    }

	    private static void deletePatient(Scanner scanner) throws SQLException {
	        System.out.print("Enter patient ID to delete: ");
	        int id = scanner.nextInt();
	        String sql = "DELETE FROM patients WHERE id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setInt(1, id);
	            int rows = pstmt.executeUpdate();
	            System.out.println(rows + " patient(s) deleted.");
	        }
	    }

	    private static void bookAppointment(Scanner scanner, int patientId) throws SQLException {
	        viewDoctors(); // Show available doctors
	        System.out.print("Enter doctor ID: ");
	        int doctorId = scanner.nextInt();
	        scanner.nextLine(); // Consume newline
	        System.out.print("Enter appointment date (e.g., 2025-06-12): ");
	        String appointmentDate = scanner.nextLine();

	        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setInt(1, patientId);
	            pstmt.setInt(2, doctorId);
	            pstmt.setString(3, appointmentDate);
	            int rows = pstmt.executeUpdate();
	            System.out.println(rows + " appointment(s) booked.");
	        }
	    }

	    private static void viewAppointmentsForDoctor() throws SQLException {
	        String sql = "SELECT a.id, a.patient_id, a.appointment_date, p.name AS patient_name " +
	                     "FROM appointments a JOIN patients p ON a.patient_id = p.id " +
	                     "WHERE a.doctor_id = (SELECT id FROM doctors WHERE name = ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setString(1, currentUser); // Assuming doctor's username matches their name in doctors table
	            ResultSet rs = pstmt.executeQuery();
	            System.out.println("\nAppointments:");
	            System.out.println("ID | Patient Name | Appointment Date");
	            System.out.println("---|--------------|-----------------");
	            while (rs.next()) {
	                System.out.printf("%d | %s | %s%n",
	                        rs.getInt("id"),
	                        rs.getString("patient_name"),
	                        rs.getString("appointment_date"));
	            }
	        }
	    }

	    private static void viewAppointmentsForPatient(int patientId) throws SQLException {
	        String sql = "SELECT a.id, a.doctor_id, a.appointment_date, d.name AS doctor_name " +
	                     "FROM appointments a JOIN doctors d ON a.doctor_id = d.id " +
	                     "WHERE a.patient_id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setInt(1, patientId);
	            ResultSet rs = pstmt.executeQuery();
	            System.out.println("\nAppointments:");
	            System.out.println("ID | Doctor Name | Appointment Date");
	            System.out.println("---|-------------|-----------------");
	            while (rs.next()) {
	                System.out.printf("%d | %s | %s%n",
	                        rs.getInt("id"),
	                        rs.getString("doctor_name"),
	                        rs.getString("appointment_date"));
	            }
	        }
	    }

	    private static void viewDoctors() throws SQLException {
	        String sql = "SELECT * FROM doctors";
	        try (Statement stmt = connection.createStatement();
	             ResultSet rs = stmt.executeQuery(sql)) {
	            System.out.println("\nDoctor Records:");
	            System.out.println("ID | Name | Specialization");
	            System.out.println("---|------|--------------");
	            while (rs.next()) {
	                System.out.printf("%d | %s | %s%n",
	                        rs.getInt("id"),
	                        rs.getString("name"),
	                        rs.getString("specialization"));
	            }
	        }
	    }
	}

