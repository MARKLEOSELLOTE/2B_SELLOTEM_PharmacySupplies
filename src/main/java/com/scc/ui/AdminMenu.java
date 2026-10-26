package com.scc.ui;

import com.scc.dao.SupplyDao;
import com.scc.dao.UserDao;
import com.scc.model.Supply;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    // GI-ILISAN: i-match ni sa exact nga roles nga gi-store sa imong database
    private static final String[] VALID_ROLES = {"ADMIN", "PHARMACIST", "INVENTORY STAFF", "AUDITOR"};
    private static final String[] VALID_STATUSES = {"APPROVED", "PENDING"};

    private final Scanner sc;
    private final UserDao userDao;
    private final SupplyDao supplyDao = new SupplyDao();

    public AdminMenu(Scanner sc, UserDao userDao) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display(User admin) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==========================================");
            System.out.println("   PHARMACY SUPPLIES MANAGEMENT");
            System.out.println("              ADMIN MENU");
            System.out.println("==========================================");
            System.out.println();
            System.out.println(" [1] Manage Products");
            System.out.println(" [2] Manage User Accounts");
            System.out.println(" [0] Logout");
            System.out.println();
            System.out.print("Enter choice: ");

            String input = sc.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    manageProducts();
                    break;
                case 2:
                    manageUsers(admin);
                    break;
                case 0:
                    System.out.println("\nLogging out...\n");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select 0 to 2.\n");
            }
        }
    }

    private void manageProducts() {
        boolean back = false;

        while (!back) {
            System.out.println("\n----- MANAGE PRODUCTS -----");
            System.out.println(" [1] View All Products");
            System.out.println(" [2] Add Product");
            System.out.println(" [3] Edit Product");
            System.out.println(" [4] Delete Product");
            System.out.println(" [0] Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    printSupplies(supplyDao.getAllSupplies());
                    break;
                case "2":
                    addProduct();
                    break;
                case "3":
                    editProduct();
                    break;
                case "4":
                    deleteProduct();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void printSupplies(List<Supply> supplies) {
        if (supplies.isEmpty()) {
            System.out.println("\nNo products found.");
            return;
        }
        System.out.println();
        System.out.printf("%-5s %-25s %-15s %-8s %-10s %-12s %-8s%n", "ID", "NAME", "CATEGORY", "QTY", "PRICE", "EXPIRY", "REORDER");
        System.out.println("-----------------------------------------------------------------------------------");
        for (Supply s : supplies) {
            System.out.printf("%-5d %-25s %-15s %-8d %-10.2f %-12s %-8d%n",
                    s.supply_id(), s.name(), s.category(), s.quantity(), s.price(),
                    s.expiry_date() == null ? "-" : s.expiry_date(), s.reorder_level());
        }
    }

    private void addProduct() {
        String name = readText("Product name: ");
        String category = readText("Category: ");
        int quantity = readInt("Quantity: ");
        double price = readDouble("Price: ");
        String expiry = readText("Expiry date (YYYY-MM-DD): ");
        int reorder = readInt("Reorder level: ");

        if (supplyDao.addSupply(name, category, quantity, price, expiry, reorder)) {
            System.out.println("Product added.");
        }
    }

    private void editProduct() {
        printSupplies(supplyDao.getAllSupplies());
        int id = readInt("Enter product ID to edit (0 to cancel): ");
        if (id == 0) return;

        String name = readText("New name: ");
        String category = readText("New category: ");
        double price = readDouble("New price: ");
        String expiry = readText("New expiry date (YYYY-MM-DD): ");
        int reorder = readInt("New reorder level: ");

        if (supplyDao.updateSupply(id, name, category, price, expiry, reorder)) {
            System.out.println("Product updated.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void deleteProduct() {
        printSupplies(supplyDao.getAllSupplies());
        int id = readInt("Enter product ID to delete (0 to cancel): ");
        if (id == 0) return;

        String confirm = readText("Are you sure? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        if (supplyDao.deleteSupply(id)) {
            System.out.println("Product deleted.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void manageUsers(User admin) {
        boolean back = false;

        while (!back) {
            System.out.println("\n----- MANAGE USER ACCOUNTS -----");
            System.out.println(" [1] Add User");
            System.out.println(" [2] View All Users");
            System.out.println(" [3] Approve / Reject User");
            System.out.println(" [4] Update User Role & Status");
            System.out.println(" [5] Delete User");
            System.out.println(" [0] Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addUser();
                    break;
                case "2":
                    printUsers(userDao.getAllUsers());
                    break;
                case "3":
                    approveOrReject();
                    break;
                case "4":
                    updateUserRoleAndStatus(admin);
                    break;
                case "5":
                    deleteUser(admin);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }
        System.out.println();
        System.out.printf("%-5s %-25s %-15s %-18s %-10s%n", "ID", "FULL NAME", "USERNAME", "ROLE", "STATUS");
        System.out.println("-------------------------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d %-25s %-15s %-18s %-10s%n",
                    u.user_Id(), u.full_name(), u.username(), u.roles(), u.status());
        }
    }

    private void approveOrReject() {
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("\nNo pending users.");
            return;
        }

        printUsers(pending);
        int id = readInt("Enter user ID (0 to cancel): ");
        if (id == 0) return;

        boolean isPending = pending.stream().anyMatch(u -> u.user_Id() == id);
        if (!isPending) {
            System.out.println("That ID is not in the pending list.");
            return;
        }

        String action = readText("Approve or Reject? (A/R): ");
        String newStatus;

        if (action.equalsIgnoreCase("A")) {
            newStatus = "APPROVED";
        } else if (action.equalsIgnoreCase("R")) {
            newStatus = "REJECTED";
        } else {
            System.out.println("Invalid choice. Cancelled.");
            return;
        }

        if (userDao.updateStatus(id, newStatus)) {
            System.out.println("User " + id + " is now " + newStatus + ".");
        } else {
            System.out.println("User not found.");
        }
    }

    private void deleteUser(User admin) {
        printUsers(userDao.getAllUsers());
        int id = readInt("Enter user ID to delete (0 to cancel): ");
        if (id == 0) return;

        if (id == admin.user_Id()) {
            System.out.println("You cannot delete your own account.");
            return;
        }

        String confirm = readText("Are you sure? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        if (userDao.deleteUser(id)) {
            System.out.println("User deleted.");
        } else {
            System.out.println("User not found.");
        }
    }

    // ===== Add User (Admin) =====
    private void addUser() {
        System.out.println("\n----- ADD USER (type 0 to cancel) -----");

        String fullName = readText("Full name: ");
        if (fullName.equals("0")) {
            System.out.println("Add user cancelled.");
            return;
        }

        String username = readText("Username: ");
        if (username.equals("0")) {
            System.out.println("Add user cancelled.");
            return;
        }

        String password = readText("Password: ");
        if (password.equals("0")) {
            System.out.println("Add user cancelled.");
            return;
        }

        String role = readValidRole();
        if (role == null) {
            System.out.println("Add user cancelled.");
            return;
        }

        String status = readValidStatus();
        if (status == null) {
            System.out.println("Add user cancelled.");
            return;
        }

        if (userDao.createUser(fullName, username, password, role, status)) {
            System.out.println("SUCCESS: User '" + username + "' created as " + role + " (" + status + ").");
        }
    }

    // ===== DELIVERABLE 7: Update User Role & Status =====
    private void updateUserRoleAndStatus(User admin) {
        List<User> users = userDao.getAllUsers();
        printUsers(users);

        int targetUserId = readIntInput("\nEnter User ID to update (0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        // Self-edit restriction
        if (targetUserId == admin.user_Id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        boolean exists = users.stream().anyMatch(u -> u.user_Id() == targetUserId);
        if (!exists) {
            System.out.println("User not found.");
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus)) {
            System.out.println("SUCCESS: User " + targetUserId + " updated to role "
                    + newRole + " with status " + newStatus + ".");
        } else {
            System.out.println("Update failed. User not found.");
        }
    }

    // ===== Input Validation Wrappers =====

    // Dili mo-crash kung letra/symbol; mo-re-prompt hangtod numero
    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Mo-return og valid role, o null kung gi-type ang 0 (cancel)
    // Pwede mo-type og number (1-4) o ang mismong ngan sa role
    private String readValidRole() {
        while (true) {
            System.out.println("\nSelect new role:");
            for (int i = 0; i < VALID_ROLES.length; i++) {
                System.out.println(" [" + (i + 1) + "] " + VALID_ROLES[i]);
            }
            System.out.println(" [0] Cancel");
            System.out.print("Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("0")) return null;

            for (int i = 0; i < VALID_ROLES.length; i++) {
                if (input.equals(String.valueOf(i + 1)) || input.equals(VALID_ROLES[i])) {
                    return VALID_ROLES[i];
                }
            }
            System.out.println("Invalid role. Try again.");
        }
    }

    // Mo-return og valid status, o null kung gi-type ang 0 (cancel)
    // Pwede mo-type og number (1-2) o ang mismong ngan sa status
    private String readValidStatus() {
        while (true) {
            System.out.println("\nSelect new status:");
            for (int i = 0; i < VALID_STATUSES.length; i++) {
                System.out.println(" [" + (i + 1) + "] " + VALID_STATUSES[i]);
            }
            System.out.println(" [0] Cancel");
            System.out.print("Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("0")) return null;

            for (int i = 0; i < VALID_STATUSES.length; i++) {
                if (input.equals(String.valueOf(i + 1)) || input.equals(VALID_STATUSES[i])) {
                    return VALID_STATUSES[i];
                }
            }
            System.out.println("Invalid status. Try again.");
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Cannot be empty.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value >= 0) return value;
                System.out.println("Must not be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(sc.nextLine().trim());
                if (value >= 0) return value;
                System.out.println("Must not be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}