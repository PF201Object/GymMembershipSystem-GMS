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
        viewMembers(); // Display members list for ID reference

        // Get Member ID (M_ID) and validate
        int memberId;
        while (true) {
            System.out.print("Enter Member ID: ");
            try {
                memberId = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid ID format. Please enter a number.");
                continue;
            }
            if (checkMemberExists(memberId)) { // Uses Members table validation
                break;
            } else {
                System.out.println("❌ Member ID " + memberId + " does not exist in the Members table. Please try again.");
            }
        }

        System.out.print("Enter Service Name (e.g., Personal Training, Zumba, Sauna): ");
        String serviceName = sc.nextLine();

        System.out.print("Enter Service Type (Monthly / Per Session): ");
        String serviceType = sc.nextLine();
        
        System.out.println("Monthly: 1050");
        System.out.println("Session: 250");
        System.out.print("Enter Amount: ");
        double amount;
        try {
            amount = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount format. Setting amount to 0.0.");
            amount = 0.0;
        }
        System.out.println("\nTrainers");
        System.out.println("1.Josh\n2.Ronbell\n3.Sarah");
        System.out.print("Enter Staff Assigned (N/A if none): ");
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
    // READ ALL SERVICES
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
            System.out.println("----------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.out.println("❌ Error viewing services: " + e.getMessage());
        }
    }

    // =============================
    // READ MEMBERS (FOR ID REFERENCE)
    // =============================
    private void viewMembers() {
        System.out.println("\n--- Existing Member IDs ---");
        // FIX: Using 'Name' as confirmed by the schema screenshot
        String sql = "SELECT M_ID, Name FROM Members ORDER BY M_ID ASC"; 

        try (Connection conn = config.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Check if there are any members
            if (!rs.isBeforeFirst()) {
                System.out.println("No members found.");
                return;
            }
            
            System.out.printf("%-8s %-30s%n", "M_ID", "Member Name"); 
            System.out.println("--------------------------------------");

            while (rs.next()) {
                System.out.printf("%-8d %-30s%n",
                        rs.getInt("M_ID"),
                        // FIX: Retrieving data using 'Name'
                        rs.getString("Name")); 
            }
            System.out.println("--------------------------------------");
        } catch (SQLException e) {
            System.out.println("❌ Error viewing members for reference: " + e.getMessage());
        }
    }


    // =============================
    // UPDATE
    // =============================
    public void updateService() {
        System.out.print("\nEnter Service ID to Update: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format.");
            return;
        }

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
        int id;
        try {
            id = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format.");
            return;
        }

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