package main;

import Config.config;
import java.util.*;

public class main {
    static Scanner sc = new Scanner(System.in);
    static config conf = new config();

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n=== GYM MEMBERSHIP SYSTEM ===");
            System.out.println("[1] Login");
            System.out.println("[2] Register");
            System.out.println("[0] Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

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
            int role = (int) user.get("U_Type");

            String roleName = (role == 0) ? "Staff" : "Admin";
            System.out.println("\n✅ Welcome, " + name + "! You are logged in as " + roleName + ".");
            showMainMenu();
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

        System.out.print("Enter Role [0 = Staff, 1 = Admin]: ");
        int role = sc.nextInt();
        sc.nextLine(); // consume newline

        String hashed = config.hashPassword(password);

        String sql = "INSERT INTO Users (Username, Email, Password, U_Type, Join_Date, Members_Status) VALUES (?, ?, ?, ?, date('now'), 'Active')";
        conf.addRecord(sql, username, email, hashed, role);

        System.out.println("✅ Registration successful! You can now login.");
    }

    // ==============================
    // MAIN MENU AFTER LOGIN
    // ==============================
    public static void showMainMenu() {
        int choice;
        do {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("[1] Members");
            System.out.println("[2] Management");
            System.out.println("[3] Services");
            System.out.println("[0] Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    memberMenu();
                    break;
                case 2:
                    managementMenu();
                    break;
                case 3:
                    servicesMenu();
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    // ==============================
    // MEMBER MENU
    // ==============================
    public static void memberMenu() {
        Members member = new Members();
        int choice;
        do {
            System.out.println("\n=== MEMBER MANAGEMENT ===");
            System.out.println("[1] Add Member");
            System.out.println("[2] View Members");
            System.out.println("[3] Update Member");
            System.out.println("[4] Delete Member");
            System.out.println("[0] Back");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

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
    
    public static void managementMenu() {
        Management management = new Management();
        int choice;
        do {
            System.out.println("\n=== MANAGEMENT OPERATIONS ===");
            System.out.println("[1] Add Record");
            System.out.println("[2] View Records");
            System.out.println("[3] Update Record");
            System.out.println("[4] Delete Record");
            System.out.println("[0] Back");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

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
        int choice;
        do {
            System.out.println("\n=== SERVICES MANAGEMENT ===");
            System.out.println("[1] Add Service");
            System.out.println("[2] View Services");
            System.out.println("[3] Update Service");
            System.out.println("[4] Delete Service");
            System.out.println("[0] Back");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

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
