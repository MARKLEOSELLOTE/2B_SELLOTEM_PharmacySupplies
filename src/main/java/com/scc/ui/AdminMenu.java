package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.SupplyDao;
import com.scc.dao.UserDao;
import com.scc.model.ActivityLog;
import com.scc.model.Supply;
import com.scc.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner sc;
    private final UserDao userDao;
    private final SupplyDao supplyDao = new SupplyDao();
    private final ActivityLogDao logDao = new ActivityLogDao();

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
            System.out.println(" [3] View System Audit Logs");
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
                    manageProducts(admin);
                    break;
                case 2:
                    manageUsers(admin);
                    break;
                case 3:
                    viewAuditLogs(admin);
                    break;
                case 0:
                    System.out.println("\nLogging out...\n");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select 0 to 3.\n");
            }
        }
    }

    private void log(User admin, String type, String description) {
        logDao.logAction(admin.user_Id(), type, description);
    }

    private void viewAuditLogs(User admin) {
        boolean back = false;

        while (!back) {
            System.out.println("\n----- SYSTEM AUDIT LOGS -----");
            System.out.println(" [1] View All System Logs");
            System.out.println(" [2] Filter Logs by User ID");
            System.out.println(" [3] Clear All Logs");
            System.out.println(" [0] Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    printLogs(logDao.getAllLogs());
                    break;
                case "2":
                    int userId = readInt("Enter user ID (0 to cancel): ");
                    if (userId != 0) {
                        printLogs(logDao.getLogsByUserId(userId));
                    }
                    break;
                case "3":
                    clearLogs(admin);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void clearLogs(User admin) {
        String confirm = readText("Type YES to permanently delete all logs: ");
        if (!confirm.equals("YES")) {
            System.out.println("Cancelled.");
            return;
        }

        if (logDao.clearLogs()) {
            System.out.println("All logs cleared.");
            log(admin, "LOGS_CLEARED", "Admin " + admin.user_Id() + " cleared all activity logs");
        } else {
            System.out.println("Failed to clear logs.");
        }
    }

    private void printLogs(List<ActivityLog> logs) {
        if (logs.isEmpty()) {
            System.out.println("\nNo logs found.");
            return;
        }

        String fmt = "%-5s | %-8s | %-18s | %-35s | %-20s%n";
        System.out.println();
        System.out.printf(fmt, "ID", "USER ID", "ACTION TYPE", "DESCRIPTION", "TIMESTAMP");
        System.out.println("-".repeat(98));

        for (ActivityLog l : logs) {
            List<String> lines = wrap(l.description(), 35);
            System.out.printf(fmt, l.id(), l.user_id(), l.action_type(), lines.get(0), l.timestamp());
            for (int i = 1; i < lines.size(); i++) {
                System.out.printf(fmt, "", "", "", lines.get(i), "");
            }
        }
    }

    private List<String> wrap(String text, int width) {
        List<String> lines = new ArrayList<>();
        String remaining = text;
        while (remaining.length() > width) {
            int cut = remaining.lastIndexOf(' ', width);
            if (cut <= 0) cut = width;
            lines.add(remaining.substring(0, cut));
            remaining = remaining.substring(cut).trim();
        }
        lines.add(remaining);
        return lines;
    }

    private void manageProducts(User admin) {
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
                    addProduct(admin);
                    break;
                case "3":
                    editProduct(admin);
                    break;
                case "4":
                    deleteProduct(admin);
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

    private void addProduct(User admin) {
        String name = readText("Product name: ");
        String category = readText("Category: ");
        int quantity = readInt("Quantity: ");
        double price = readDouble("Price: ");
        String expiry = readText("Expiry date (YYYY-MM-DD): ");
        int reorder = readInt("Reorder level: ");

        if (supplyDao.addSupply(name, category, quantity, price, expiry, reorder)) {
            System.out.println("Product added.");
            log(admin, "SUPPLY_ADDED",
                    "Admin " + admin.user_Id() + " added product " + name + " (Qty: " + quantity + ")");
        }
    }

    private void editProduct(User admin) {
        printSupplies(supplyDao.getAllSupplies());
        int id = readInt("Enter product ID to edit (0 to cancel): ");
        if (id == 0) return;

        Supply old = supplyDao.getById(id);
        if (old == null) {
            System.out.println("Product not found.");
            return;
        }

        String name = readText("New name: ");
        String category = readText("New category: ");
        double price = readDouble("New price: ");
        String expiry = readText("New expiry date (YYYY-MM-DD): ");
        int reorder = readInt("New reorder level: ");

        if (supplyDao.updateSupply(id, name, category, price, expiry, reorder)) {
            System.out.println("Product updated.");
            log(admin, "SUPPLY_UPDATED",
                    "Admin " + admin.user_Id() + " updated product #" + id + " (" + old.name() + ")");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void deleteProduct(User admin) {
        printSupplies(supplyDao.getAllSupplies());
        int id = readInt("Enter product ID to delete (0 to cancel): ");
        if (id == 0) return;

        Supply old = supplyDao.getById(id);
        if (old == null) {
            System.out.println("Product not found.");
            return;
        }

        String confirm = readText("Are you sure? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        if (supplyDao.deleteSupply(id)) {
            System.out.println("Product deleted.");
            log(admin, "SUPPLY_DELETED",
                    "Admin " + admin.user_Id() + " deleted product #" + id + " (" + old.name() + ")");
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
            System.out.println(" [4] Update User Role / Status");
            System.out.println(" [5] Delete User");
            System.out.println(" [0] Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addUser(admin);
                    break;
                case "2":
                    viewUsersWithFilter();
                    break;
                case "3":
                    approveOrReject(admin);
                    break;
                case "4":
                    updateUser(admin);
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

    private void viewUsersWithFilter() {
        List<User> all = userDao.getAllUsers();
        printUsers(all);

        boolean back = false;
        while (!back) {
            System.out.println("\nFilter by status:");
            System.out.println(" [1] PENDING");
            System.out.println(" [2] APPROVED");
            System.out.println(" [3] REJECTED");
            System.out.println(" [4] Show All");
            System.out.println(" [0] Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    printUsers(filterByStatus(all, "PENDING"));
                    break;
                case "2":
                    printUsers(filterByStatus(all, "APPROVED"));
                    break;
                case "3":
                    printUsers(filterByStatus(all, "REJECTED"));
                    break;
                case "4":
                    printUsers(all);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private List<User> filterByStatus(List<User> users, String status) {
        return users.stream()
                .filter(u -> u.status().equalsIgnoreCase(status))
                .toList();
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

    private void approveOrReject(User admin) {
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
            if (newStatus.equals("APPROVED")) {
                log(admin, "USER_APPROVED",
                        "Admin " + admin.user_Id() + " approved account for User ID " + id);
            } else {
                log(admin, "USER_REJECTED",
                        "Admin " + admin.user_Id() + " rejected account for User ID " + id);
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private void updateUser(User admin) {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
            return;
        }

        printUsers(users);
        int userId = readInt("Enter user ID to update (0 to cancel): ");

        if (userId == 0) {
            System.out.println("Cancelled.");
            return;
        }

        boolean exists = users.stream().anyMatch(u -> u.user_Id() == userId);
        if (!exists) {
            System.out.println("User not found.");
            return;
        }

        if (userId == admin.user_Id()) {
            System.out.println("[WARNING] Modifying the currently logged-in admin is restricted.");
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Cancelled.");
            return;
        }

        if (userDao.updateUserAndStatus(userId, newRole, newStatus)) {
            log(admin, "USER_UPDATED",
                    "Admin " + admin.user_Id() + " changed User " + userId
                            + " to " + newRole + " / " + newStatus);
        } else {
            System.out.println("Update failed.");
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.println("\nSelect Role:");
            System.out.println(" [1] ADMIN");
            System.out.println(" [2] PHARMACIST");
            System.out.println(" [3] INVENTORY_STAFF");
            System.out.println(" [4] AUDITOR");
            System.out.println(" [0] Cancel");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": return "ADMIN";
                case "2": return "PHARMACIST";
                case "3": return "INVENTORY_STAFF";
                case "4": return "AUDITOR";
                case "0": return null;
                default:
                    System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.println("\nSelect Status:");
            System.out.println(" [1] PENDING");
            System.out.println(" [2] APPROVED");
            System.out.println(" [3] REJECTED");
            System.out.println(" [0] Cancel");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": return "PENDING";
                case "2": return "APPROVED";
                case "3": return "REJECTED";
                case "0": return null;
                default:
                    System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    private void deleteUser(User admin) {
        List<User> users = userDao.getAllUsers();
        printUsers(users);
        int id = readInt("Enter user ID to delete (0 to cancel): ");
        if (id == 0) return;

        if (id == admin.user_Id()) {
            System.out.println("You cannot delete your own account.");
            return;
        }

        String target = users.stream()
                .filter(u -> u.user_Id() == id)
                .map(User::username)
                .findFirst()
                .orElse(null);
        if (target == null) {
            System.out.println("User not found.");
            return;
        }

        String confirm = readText("Are you sure? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        if (userDao.deleteUser(id)) {
            System.out.println("User deleted.");
            log(admin, "USER_DELETED",
                    "Admin " + admin.user_Id() + " deleted User ID " + id + " (" + target + ")");
        } else {
            System.out.println("User not found.");
        }
    }

    private void addUser(User admin) {
        System.out.println("\n----- ADD USER -----");

        String fullName = readText("Full name: ");
        String username = readText("Username: ");

        boolean taken = userDao.getAllUsers().stream()
                .anyMatch(u -> u.username().equalsIgnoreCase(username));
        if (taken) {
            System.out.println("Username already exists.");
            return;
        }

        String password = readText("Password: ");

        String role = readValidRole();
        if (role == null) {
            System.out.println("Cancelled.");
            return;
        }

        String status = readValidStatus();
        if (status == null) {
            System.out.println("Cancelled.");
            return;
        }

        if (userDao.addUser(fullName, username, password, role, status)) {
            System.out.println("User '" + username + "' added as " + role + " (" + status + ").");
            log(admin, "USER_CREATED",
                    "Admin " + admin.user_Id() + " created user " + username + " as " + role);
        } else {
            System.out.println("Failed to add user.");
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