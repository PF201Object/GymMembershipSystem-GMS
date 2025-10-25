package main;

import Config.config;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Management {
    Scanner sc = new Scanner(System.in);
    config conf = new config();

    // =============================
    // CREATE
    // =============================
    public void addManagementRecord() {
        System.out.println("\n=== ADD MANAGEMENT RECORD ===");
        System.out.print("Enter User ID (Staff/Admin): ");
        int userId = Integer.parseInt(sc.nextLine());
        System.out.print("Enter Activity Type (Registration / Update / Cancellation): ");
        String activity = sc.nextLine();
        System.out.print("Enter Action Details: ");
        String details = sc.nextLine();
        System.out.print("Enter Remarks: ");
        String remarks = sc.nextLine();
        String status = "Pending";

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String sql = "INSERT INTO Management (U_ID, Activity_Type, Action_Details, Action_Date, Remarks, Status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, activity);
            pstmt.setString(3, details);
            pstmt.setString(4, date);
            pstmt.setString(5, remarks);
            pstmt.setString(6, status);
            pstmt.executeUpdate();
            System.out.println("✅ Management record added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error adding management record: " + e.getMessage());
        }
    }

    // =============================
    // READ
    // =============================
    public void viewManagementRecords() {
        System.out.println("\n=== MANAGEMENT RECORDS ===");
        String sql = "SELECT * FROM Management ORDER BY Action_Date DESC";

        try (Connection conn = config.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-5s %-8s %-20s %-30s %-20s %-25s %-15s%n",
                    "ID", "U_ID", "Activity", "Details", "Date", "Remarks", "Status");
            System.out.println("-------------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-8d %-20s %-30s %-20s %-25s %-15s%n",
                        rs.getInt("MG_ID"),
                        rs.getInt("U_ID"),
                        rs.getString("Activity_Type"),
                        rs.getString("Action_Details"),
                        rs.getString("Action_Date"),
                        rs.getString("Remarks"),
                        rs.getString("Status"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error viewing management records: " + e.getMessage());
        }
    }

    // =============================
    // UPDATE
    // =============================
    public void updateManagementRecord() {
        System.out.print("\nEnter Management Record ID to Update: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Enter New Remarks: ");
        String remarks = sc.nextLine();
        System.out.print("Enter New Status (Completed / Pending / Cancelled): ");
        String status = sc.nextLine();

        String sql = "UPDATE Management SET Remarks = ?, Status = ? WHERE MG_ID = ?";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, remarks);
            pstmt.setString(2, status);
            pstmt.setInt(3, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Management record updated successfully!");
            else
                System.out.println("⚠️ Record not found!");
        } catch (SQLException e) {
            System.out.println("❌ Error updating record: " + e.getMessage());
        }
    }

    // =============================
    // DELETE
    // =============================
    public void deleteManagementRecord() {
        System.out.print("\nEnter Management Record ID to Delete: ");
        int id = Integer.parseInt(sc.nextLine());

        String sql = "DELETE FROM Management WHERE MG_ID = ?";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("🗑️ Record deleted successfully!");
            else
                System.out.println("⚠️ Record not found!");
        } catch (SQLException e) {
            System.out.println("❌ Error deleting record: " + e.getMessage());
        }
    }
}
