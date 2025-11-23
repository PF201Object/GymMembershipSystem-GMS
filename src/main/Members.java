package main;

import Config.config;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Members {
    Scanner sc = new Scanner(System.in);
    config conf = new config();

    // CREATE
    public void addMember() {
        System.out.println("\n=== ADD NEW MEMBER ===");
        System.out.print("Full Name: ");
        String name = sc.nextLine();
        System.out.print("Gender: ");
        String gender = sc.nextLine();
        System.out.print("Contact No: ");
        String contact = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Address: ");
        String address = sc.nextLine();
        System.out.println("\n--Prices of Gym Membership--\n");
        System.out.println("*Access Basic Packages");
        System.out.println("Bronze:  300P");
        System.out.println("*Access Mid Tier Packages");
        System.out.println("Gold:    500P");
        System.out.println("*Access All Packages");
        System.out.println("Diamond: 850P\n");
        System.out.print("\nMembership Type (Bronze / Gold / Diamond): ");
        String uType = sc.nextLine();
        String joinDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String status = "Active";

        String sql = "INSERT INTO Members (Name, Gender, Contact_No, Email, Address, U_Type, Join_Date, Membership_Status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setString(3, contact);
            pstmt.setString(4, email);
            pstmt.setString(5, address);
            pstmt.setString(6, uType);
            pstmt.setString(7, joinDate);
            pstmt.setString(8, status);
            pstmt.executeUpdate();
            System.out.println("✅ Member added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error adding member: " + e.getMessage());
        }
    }

    // READ
    public void viewMembers() {
        System.out.println("\n=== MEMBER RECORDS ===");
        String sql = "SELECT * FROM Members";
        try (Connection conn = config.connectDB(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-5s %-20s %-10s %-15s %-25s %-25s %-15s %-15s %-15s\n",
                    "ID", "Name", "Gender", "Contact", "Email", "Address", "Type", "Join_Date", "Status");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-10s %-15s %-25s %-25s %-15s %-15s %-15s\n",
                        rs.getInt("M_ID"),
                        rs.getString("Name"),
                        rs.getString("Gender"),
                        rs.getString("Contact_No"),
                        rs.getString("Email"),
                        rs.getString("Address"),
                        rs.getString("U_Type"),
                        rs.getString("Join_Date"),
                        rs.getString("Membership_Status"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error viewing members: " + e.getMessage());
        }
    }

    // UPDATE
    public void updateMember() {
        System.out.print("\nEnter Member ID to Update: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("New Contact No: ");
        String contact = sc.nextLine();
        System.out.print("New Address: ");
        String address = sc.nextLine();
        System.out.print("New Membership Status (Active / Inactive / Expired): ");
        String status = sc.nextLine();

        String sql = "UPDATE Members SET Contact_No=?, Address=?, Membership_Status=? WHERE M_ID=?";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact);
            pstmt.setString(2, address);
            pstmt.setString(3, status);
            pstmt.setInt(4, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Member updated successfully!");
            else
                System.out.println("⚠️ Member not found!");
        } catch (SQLException e) {
            System.out.println("❌ Error updating member: " + e.getMessage());
        }
    }

    // DELETE
    public void deleteMember() {
        System.out.print("\nEnter Member ID to Delete: ");
        int id = Integer.parseInt(sc.nextLine());

        String sql = "DELETE FROM Members WHERE M_ID=?";
        try (Connection conn = config.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("🗑️ Member deleted successfully!");
            else
                System.out.println("⚠️ Member not found!");
        } catch (SQLException e) {
            System.out.println("❌ Error deleting member: " + e.getMessage());
        }
    }
}
