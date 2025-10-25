package main;

import Config.config;
import java.sql.*;
import java.util.Scanner;

public class Services {
    Scanner sc = new Scanner(System.in);
    config conf = new config();

    // =============================
    // CREATE with VALIDATION
    // =============================
    public void addService() {
        System.out.println("\n=== ADD NEW SERVICE ===");

        int memberId;
        while (true) {
            System.out.print("Enter Member ID: ");
            memberId = Integer.parseInt(sc.nextLine());

            // 🔍 Validate if Member exists
            if (checkMemberExists(memberId)) {
                break;
            } else {
                System.out.println("❌ Member ID " + memberId + " does not exist. Please try again.");
            }
        }

        System.out.print("Enter Service Name (e.g., Personal Training, Zumba, Sauna): ");
        String serviceName = sc.nextLine();

        System.out.print("Enter Service Type (Monthly / Per Session / Premium): ");
        String serviceType = sc.nextLine();

        System.out.print("Enter Amount: ");
        double amount = Double.parseDouble(sc.nextLine());

        System.out.print("Enter Staff Assigned (leave blank if none): ");
        String staff = sc.nextLine();

        String paymentStatus = "Pending";

        String sql = "INSERT INTO Services (M_ID, Service_Name, Service_Type, Payment_Status, Amount, Staff_Assigned) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setString(2, serviceName);
            pstmt.setString(3, serviceType);
            pstmt.setString(4, paymentStatus);
            pstmt.setDouble(5, amount);
            pstmt.setString(6, staff);
            pstmt.executeUpdate();
            System.out.println("✅ Service record added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error adding service record: " + e.getMessage());
        }
    }

    // =============================
    // CHECK MEMBER EXISTS
    // =============================
    private boolean checkMemberExists(int memberId) {
        String sql = "SELECT COUNT(*) AS count FROM Members WHERE M_ID = ?";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error checking Member ID: " + e.getMessage());
        }
        return false;
    }

    // =============================
    // READ
    // =============================
    public void viewServices() {
        System.out.println("\n=== SERVICE RECORDS ===");
        String sql = "SELECT * FROM Services ORDER BY S_ID DESC";

        try (Connection conn = config.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-5s %-8s %-25s %-15s %-12s %-10s %-20s%n",
                    "ID", "M_ID", "Service Name", "Type", "Payment", "Amount", "Staff Assigned");
            System.out.println("----------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-8d %-25s %-15s %-12s %-10.2f %-20s%n",
                        rs.getInt("S_ID"),
                        rs.getInt("M_ID"),
                        rs.getString("Service_Name"),
                        rs.getString("Service_Type"),
                        rs.getString("Payment_Status"),
                        rs.getDouble("Amount"),
                        rs.getString("Staff_Assigned"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error viewing services: " + e.getMessage());
        }
    }

    // =============================
    // UPDATE
    // =============================
    public void updateService() {
        System.out.print("\nEnter Service ID to Update: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("Enter New Payment Status (Paid / Unpaid / Pending): ");
        String status = sc.nextLine();
        System.out.print("Enter New Staff Assigned (leave blank to skip): ");
        String staff = sc.nextLine();

        String sql = "UPDATE Services SET Payment_Status = ?, Staff_Assigned = ? WHERE S_ID = ?";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, staff);
            pstmt.setInt(3, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0)
                System.out.println("✅ Service record updated successfully!");
            else
                System.out.println("⚠️ Record not found!");
        } catch (SQLException e) {
            System.out.println("❌ Error updating service record: " + e.getMessage());
        }
    }

    // =============================
    // DELETE
    // =============================
    public void deleteService() {
        System.out.print("\nEnter Service ID to Delete: ");
        int id = Integer.parseInt(sc.nextLine());

        String sql = "DELETE FROM Services WHERE S_ID = ?";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("🗑️ Service record deleted successfully!");
            else
                System.out.println("⚠️ Record not found!");
        } catch (SQLException e) {
            System.out.println("❌ Error deleting service record: " + e.getMessage());
        }
    }
}
