package main;

import Config.config;
import java.util.*;

public class main {
    static Scanner sc = new Scanner(System.in);
    static config conf = new config();
    
    // Static variable to store the role of the currently logged-in user (0=Staff, 1=Admin)
    private static int current_user_role = -1; 

    public static void main(String[] args) {
        int choice = -1;
        do {
            System.out.println("\n=== GYM MEMBERSHIP SYSTEM ===");
            System.out.println("[1] Login");
            System.out.println("[2] Register");
            System.out.println("[0] Exit");
            System.out.print("Enter choice: ");
            
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                choice = -1; // Forces invalid choice handling
            }

            switch (choice) {
                case 1:
                    loginUser();
                    break;
                case 2:
                    registerUser();
                    break;
                case 0:
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    // ==============================
    // LOGIN FUNCTION
    // ==============================
    public static void loginUser() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        String hashed = config.hashPassword(password);

        String sql = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        List<Map<String, Object>> userList = conf.fetchRecords(sql, username, hashed);

        if (!userList.isEmpty()) {
            Map<String, Object> user = userList.get(0);
            String name = (String) user.get("Username");
            // Cast to int, assuming U_Type is stored as a numerical type (0 or 1)
            int role = ((Number) user.get("U_Type")).intValue();
            
            // Store the logged-in user's role
            current_user_role = role; 

            String roleName = (role == 0) ? "Staff" : "Admin";
            System.out.println("\n✅ Welcome, " + name + "! You are logged in as " + roleName + ".");
            showMainMenu();
            
            // After the user logs out (showMainMenu returns), reset the role
            current_user_role = -1;
        } else {
            System.out.println("❌ Invalid credentials. Try again.");
        }
    }

    // ==============================
    // REGISTER FUNCTION
    // ==============================
    public static void registerUser() {
        System.out.println("\n=== REGISTER NEW ACCOUNT ===");
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        System.out.print("Confirm Password: ");
        String confirm = sc.nextLine();

        if (!password.equals(confirm)) {
            System.out.println("❌ Passwords do not match!");
            return;
        }

        int role = -1;
        System.out.print("Enter Role [0 = Staff, 1 = Admin]: ");
        try {
            role = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid role input. Must be 0 or 1.");
            return;
        }
        
        if (role != 0 && role != 1) {
            System.out.println("❌ Role must be 0 (Staff) or 1 (Admin).");
            return;
        }

        String hashed = config.hashPassword(password);

        String sql = "INSERT INTO Users (Username, Email, Password, U_Type, Join_Date, Members_Status) VALUES (?, ?, ?, ?, date('now'), 'Active')";
        conf.addRecord(sql, username, email, hashed, role);

        System.out.println("✅ Registration successful! You can now login.");
    }

    // ==============================
    // MAIN MENU AFTER LOGIN
    // ==============================
    public static void showMainMenu() {
        int choice = -1;
        // Dynamic menu options
        final int membersOption = 1;
        final int servicesOptionForStaff = 2;
        final int servicesOptionForAdmin = 3;
        final int managementOption = 2; // For Admin only
        final int logoutOption = 0;
        
        do {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("[" + membersOption + "] Members");
            
            int currentServicesOption = servicesOptionForStaff;

            // Conditional display of Management for Admin (role == 1)
            if (current_user_role == 1) {
                System.out.println("[" + managementOption + "] Management");
                currentServicesOption = servicesOptionForAdmin; // Services moves to [3]
            }

            System.out.println("[" + currentServicesOption + "] Services");
            System.out.println("[" + logoutOption + "] Logout");
            System.out.print("Enter choice: ");
            
            // Input reading with robust error handling
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                choice = -1; // Forces invalid choice handling
            }

            if (choice == membersOption) {
                memberMenu();
            } else if (current_user_role == 1 && choice == managementOption) {
                // Admin access to Management
                managementMenu();
            } else if (choice == currentServicesOption) {
                servicesMenu();
            } else if (choice == logoutOption) {
                System.out.println("Logging out...");
                break; // Exit the loop
            } else {
                System.out.println("Invalid choice. Try again.");
            }
            
        } while (choice != 0);
    }

    // ==============================
    // MEMBER MENU
    // ==============================
    public static void memberMenu() {
        Members member = new Members();
        int choice = -1;
        do {
            System.out.println("\n=== MEMBER MANAGEMENT ===");
            System.out.println("[1] Add Member");
            System.out.println("[2] View Members");
            System.out.println("[3] Update Member");
            System.out.println("[4] Delete Member");
            System.out.println("[0] Back");
            System.out.print("Enter choice: ");
            
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    member.addMember();
                    break;
                case 2:
                    member.viewMembers();
                    break;
                case 3:
                    member.updateMember();
                    break;
                case 4:
                    member.deleteMember();
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        } while (choice != 0);
    }
    
    // MANAGEMENT MENU (Only accessible by Admin via showMainMenu logic)
    public static void managementMenu() {
        Management management = new Management();
        int choice = -1;
        do {
            System.out.println("\n=== MANAGEMENT OPERATIONS ===");
            System.out.println("[1] Add Record");
            System.out.println("[2] View Records");
            System.out.println("[3] Update Record");
            System.out.println("[4] Delete Record");
            System.out.println("[0] Back");
            System.out.print("Enter choice: ");
            
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    management.addManagementRecord();
                    break;
                case 2:
                    management.viewManagementRecords();
                    break;
                case 3:
                    management.updateManagementRecord();
                    break;
                case 4:
                    management.deleteManagementRecord();
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    public static void servicesMenu() {
        Services services = new Services();
        int choice = -1;
        do {
            System.out.println("\n=== SERVICES MANAGEMENT ===");
            System.out.println("[1] Add Service");
            System.out.println("[2] View Services");
            System.out.println("[3] Update Service");
            System.out.println("[4] Delete Service");
            System.out.println("[0] Back");
            System.out.print("Enter choice: ");
            
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    services.addService();
                    break;
                case 2:
                    services.viewServices();
                    break;
                case 3:
                    services.updateService();
                    break;
                case 4:
                    services.deleteService();
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

}