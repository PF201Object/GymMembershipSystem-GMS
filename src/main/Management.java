package main;

import Config.config;
import java.util.*;

public class Management {
    // Shared resources from main.java
    static Scanner sc = main.sc;
    static config conf = main.conf;

    // ==============================
    // HELPER: DISPLAY AVAILABLE USERS (AS POTENTIAL STAFF IDs)
    // ==============================
    /**
     * Helper method to display all usernames from the Users table for the Admin to choose from.
     */
    private void displayAvailableUsernames() {
        System.out.println("\n--- Available Usernames (Potential Staff IDs) ---");
        // Fetch all usernames and their roles from the Users table
        String sql = "SELECT Username, U_Type FROM Users ORDER BY Username";
        List<Map<String, Object>> userList = conf.fetchRecords(sql);

        if (userList.isEmpty()) {
            System.out.println("ℹ️ No registered users found.");
            return;
        }

        System.out.println("----------------------------------------");
        System.out.printf("| %-20s | %-10s |\n", "Username (Staff ID)", "Role");
        System.out.println("----------------------------------------");
        
        for (Map<String, Object> user : userList) {
            String username = (String) user.get("Username");
            // Assuming U_Type is a number (0 or 1)
            int role = ((Number) user.get("U_Type")).intValue();
            String roleName = (role == 0) ? "Staff" : "Admin";
            System.out.printf("| %-20s | %-10s |\n", username, roleName);
        }
        System.out.println("----------------------------------------");
    }

    // ==============================
    // HELPER: VIEW STAFF RECORD BY ID (kept for completeness)
    // ==============================
    public boolean viewManagementRecordByID(String staffId) {
        String sql = "SELECT StaffID, Role_Position, DateOfHire, Salary_PayRate, ContactNumber, WorkEmail FROM Management WHERE StaffID = ?";
        List<Map<String, Object>> staffList = conf.fetchRecords(sql, staffId);

        if (staffList.isEmpty()) {
            System.out.println("❌ Staff ID " + staffId + " not found in Management records.");
            return false;
        }
        
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-15s | %-30s |\n", 
                             "Staff ID", "Role/Position", "Date of Hire", "Salary/PayRate", "Contact Number", "Work Email");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");

        for (Map<String, Object> staff : staffList) {
            System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-15s | %-30s |\n",
                staff.get("StaffID"),
                staff.get("Role_Position"),
                staff.get("DateOfHire"),
                staff.get("Salary_PayRate"),
                staff.get("ContactNumber"),
                staff.get("WorkEmail"));
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        return true;
    }
    
    // ==============================
    // ADD STAFF RECORD
    // ==============================
    public void addManagementRecord() {
        System.out.println("\n=== ADD NEW STAFF RECORD ===");
        
        // 1. Display list of potential Staff IDs (Usernames)
        displayAvailableUsernames(); 
        
        // 2. Prompt for Staff ID
        System.out.print("Enter Staff ID: ");
        String staffId = sc.nextLine();
        
        // Check 1: Check if Staff ID already has a Management record
        String checkMgmtSql = "SELECT * FROM Management WHERE StaffID = ?";
        List<Map<String, Object>> existingRecord = conf.fetchRecords(checkMgmtSql, staffId);

        if (!existingRecord.isEmpty()) {
            System.out.println("❌ Error: Staff ID " + staffId + " already has a Management record.");
            // Show the existing record
            viewManagementRecordByID(staffId);
            return;
        }
        
        // Check 2: Check if the Staff ID exists in the Users table (CORRECTED QUERY)
        // This ensures a management record is only created for a registered user.
        String checkUserSql = "SELECT * FROM Users WHERE Username = ?"; 
        List<Map<String, Object>> existingUser = conf.fetchRecords(checkUserSql, staffId);
        
        if (existingUser.isEmpty()) {
            System.out.println("❌ Error: Staff ID " + staffId + " does not match any registered user in the system.");
            return;
        }
        
        System.out.println("\nFront Desk Staff / Member Services Associate");
        System.out.println("Gym Attendant / Entry-Level Staff");
        System.out.print("Enter Role/Position: ");
        String role = sc.nextLine();
        System.out.print("Enter DateOfHire (YYYY-MM-DD): ");
        String dateOfHire = sc.nextLine();
        System.out.println("\nFront Desk Staff / Member Services Associate");
        System.out.println("*₱16,000 – ₱20,000 per month");
        System.out.println("Gym Attendant / Entry-Level Staff");
        System.out.println("*₱12,000 – ₱15,000 per month");
        System.out.print("Enter Salary/PayRate: ");
        String salary = sc.nextLine();
        System.out.print("Enter ContactNumber: ");
        String contactNumber = sc.nextLine();
        System.out.print("Enter WorkEmail: ");
        String workEmail = sc.nextLine();

        String sql = "INSERT INTO Management (StaffID, Role_Position, DateOfHire, Salary_PayRate, ContactNumber, WorkEmail) VALUES (?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, staffId, role, dateOfHire, salary, contactNumber, workEmail);

        System.out.println("✅ Staff record for ID " + staffId + " added successfully.");
    }
    
    // ==============================
    // VIEW STAFF RECORDS (ALL)
    // ==============================
    public void viewManagementRecords() {
        System.out.println("\n=== VIEW ALL STAFF RECORDS ===");
        String sql = "SELECT * FROM Management";
        List<Map<String, Object>> staffList = conf.fetchRecords(sql);

        if (staffList.isEmpty()) {
            System.out.println("ℹ️ No staff records found.");
            return;
        }

        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-15s | %-30s |\n",
                             "Staff ID", "Role/Position", "Date of Hire", "Salary/PayRate", "Contact Number", "Work Email");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");

        for (Map<String, Object> staff : staffList) {
            System.out.printf("| %-10s | %-20s | %-15s | %-15s | %-15s | %-30s |\n",
                staff.get("StaffID"),
                staff.get("Role_Position"),
                staff.get("DateOfHire"),
                staff.get("Salary_PayRate"),
                staff.get("ContactNumber"),
                staff.get("WorkEmail"));
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
    }
    
    // ==============================
    // UPDATE STAFF RECORD
    // ==============================
    public void updateManagementRecord() {
        System.out.println("\n=== UPDATE STAFF RECORD ===");
        System.out.print("Enter Staff ID to update: ");
        String staffId = sc.nextLine();

        // Check if Staff ID exists
        String checkSql = "SELECT * FROM Management WHERE StaffID = ?";
        List<Map<String, Object>> existingRecord = conf.fetchRecords(checkSql, staffId);

        if (existingRecord.isEmpty()) {
            System.out.println("❌ Staff ID " + staffId + " not found. Cannot update.");
            return;
        }
        
        System.out.println("Enter new details (leave blank to keep current value):");
        
        System.out.print("Enter new Role/Position: ");
        String newRole = sc.nextLine();
        
        System.out.print("Enter new DateOfHire (YYYY-MM-DD): ");
        String newDateOfHire = sc.nextLine();
        
        System.out.print("Enter new Salary/PayRate: ");
        String newSalary = sc.nextLine();
        
        System.out.print("Enter new ContactNumber: ");
        String newContactNumber = sc.nextLine();
        
        System.out.print("Enter new WorkEmail: ");
        String newWorkEmail = sc.nextLine();

        // Use the existing values if the new input is blank
        Map<String, Object> current = existingRecord.get(0);
        String role = newRole.isEmpty() ? (String)current.get("Role_Position") : newRole;
        String dateOfHire = newDateOfHire.isEmpty() ? (String)current.get("DateOfHire") : newDateOfHire;
        String salary = newSalary.isEmpty() ? (String)current.get("Salary_PayRate") : newSalary;
        String contactNumber = newContactNumber.isEmpty() ? (String)current.get("ContactNumber") : newContactNumber;
        String workEmail = newWorkEmail.isEmpty() ? (String)current.get("WorkEmail") : newWorkEmail;

        String sql = "UPDATE Management SET Role_Position = ?, DateOfHire = ?, Salary_PayRate = ?, ContactNumber = ?, WorkEmail = ? WHERE StaffID = ?";
        conf.updateRecord(sql, role, dateOfHire, salary, contactNumber, workEmail, staffId);

        System.out.println("✅ Staff record for ID " + staffId + " updated successfully.");
    }
    // ==============================
    // DELETE STAFF RECORD
    // ==============================
    public void deleteManagementRecord() {
        System.out.println("\n=== DELETE STAFF RECORD ===");
        System.out.print("Enter Staff ID to delete: ");
        String staffId = sc.nextLine();

        // Check if Staff ID exists
        String checkSql = "SELECT * FROM Management WHERE StaffID = ?";
        List<Map<String, Object>> existingRecord = conf.fetchRecords(checkSql, staffId);

        if (existingRecord.isEmpty()) {
            System.out.println("❌ Staff ID " + staffId + " not found. Cannot delete.");
            return;
        }
        
        System.out.print("⚠️ Are you sure you want to delete staff record for ID " + staffId + "? (yes/no): ");
        String confirmation = sc.nextLine().toLowerCase();

        if (confirmation.equals("yes")) {
            String sql = "DELETE FROM Management WHERE StaffID = ?";
            conf.deleteRecord(sql, staffId);
            System.out.println("✅ Staff record for ID " + staffId + " deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}