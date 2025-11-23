package Config;

import java.sql.*;
import java.util.*;

public class config {
    // =======================================================
    // DATABASE CONNECTION
    // =======================================================
    /**
     * Establishes a connection to the SQLite database file GMS.db.
     * @return A valid Connection object, or null if connection fails.
     */
    public static Connection connectDB() {
        Connection con = null;
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            // Establish the connection
            con = DriverManager.getConnection("jdbc:sqlite:GMS.db");
            // System.out.println("Connection Successful"); // Commented out to reduce console clutter
        } catch (ClassNotFoundException e) {
            System.out.println("Connection Failed: SQLite JDBC driver not found.");
        } catch (SQLException e) {
            System.out.println("Connection Failed: Could not connect to database: " + e.getMessage());
        }
        return con;
    }

    public void addRecord(String sql, Object... values) {
        try (Connection conn = config.connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop through values and set them in the prepared statement
            for (int i = 0; i < values.length; i++) {
                // Simplified object setting for flexibility
                pstmt.setObject(i + 1, values[i]); 
            }

            pstmt.executeUpdate();
            System.out.println("Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    // =======================================================
    // CRUD: SELECT (for viewing tables)
    // =======================================================
    /**
     * Executes a SELECT query and prints the results using custom column headers.
     */
    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = config.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            // Dynamic header printing
            StringBuilder headerLine = new StringBuilder();
            headerLine.append("--------------------------------------------------------------------------------\n| ");
            for (String header : columnHeaders) {
                headerLine.append(String.format("%-20s | ", header));
            }
            headerLine.append("\n--------------------------------------------------------------------------------");
            System.out.println(headerLine.toString());

            // Dynamic row printing
            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String colName : columnNames) {
                    String value = rs.getString(colName);
                    row.append(String.format("%-20s | ", value != null ? value : ""));
                }
                System.out.println(row.toString());
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    // =======================================================
    // CRUD: UPDATE
    // =======================================================
    /**
     * Executes an UPDATE query with dynamic parameter binding.
     */
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = config.connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop through values and set them in the prepared statement
            for (int i = 0; i < values.length; i++) {
                // Simplified object setting for flexibility
                pstmt.setObject(i + 1, values[i]);
            }

            int rowsAffected = pstmt.executeUpdate();
             if (rowsAffected > 0) {
                 System.out.println("Record updated successfully!");
             } else {
                 System.out.println("No records were updated. Check the ID.");
             }
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    // =======================================================
    // CRUD: DELETE
    // =======================================================
    /**
     * Executes a DELETE query with dynamic parameter binding.
     */
    public void deleteRecord(String sql, Object... values) {
        try (Connection conn = config.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop through values and set them in the prepared statement
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Record deleted successfully!");
            } else {
                System.out.println("No record found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    // =======================================================
    // CRUD: SELECT (for fetching data to process, e.g., login)
    // =======================================================
    /**
     * Executes a SELECT query and returns the results as a list of maps.
     * Used internally by other classes (like main.java) for login checks.
     */
    public List<Map<String, Object>> fetchRecords(String sqlQuery, Object... values) {
        List<Map<String, Object>> records = new ArrayList<>();

        try (Connection conn = config.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                records.add(row);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching records: " + e.getMessage());
        }

        return records;
    }
    
    // =======================================================
    // PASSWORD UTILITY
    // =======================================================
    /**
     * Hashes passwords using SHA-256 for secure storage.
     */
    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
}