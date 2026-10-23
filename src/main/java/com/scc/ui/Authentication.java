package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n==========================");
            System.out.println(" PHARMACY SUPPLIES SYSTEM");
            System.out.println("==========================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please choose 1, 2, or 3.");
            }
        }
    }

    private String selectRole() {
        while (true) {
            System.out.println("\nLogin as:");
            System.out.println(" [1] Pharmacy Administrator");
            System.out.println(" [2] Pharmacist");
            System.out.println(" [3] Inventory Staff");
            System.out.println(" [4] Auditor");
            System.out.println(" [0] Back");
            System.out.print("Select role: ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1":
                    return "ADMIN";
                case "2":
                    return "PHARMACIST";
                case "3":
                    return "INVENTORY STAFF";
                case "4":
                    return "AUDITOR";
                case "0":
                    return null;
                default:
                    System.out.println("Invalid choice. Please enter 0 to 4.");
            }
        }
    }

    private void handleRegister() {
        String fullname;
        String username;
        String password;
        String role;

        while (true) {
            System.out.print("Full name: ");
            fullname = sc.nextLine().trim();
            if (!fullname.isEmpty()) break;
            System.out.println("Full name cannot be empty. Try again.");
        }

        while (true) {
            System.out.print("Username (4-20 letters): ");
            username = sc.nextLine().trim();
            if (username.matches("[A-Za-z0-9_]{4,20}")) break;
            System.out.println("Invalid username. Use 4-20 letters, numbers, or underscore only.");
        }

        while (true) {
            System.out.print("Password (at least 5 characters): ");
            password = sc.nextLine();
            if (password.length() >= 5) break;
            System.out.println("Password is too short. Try again.");
        }

        while (true) {
            System.out.print("Select Role [1] Pharmacy Administrator | [2] Pharmacist | [3] Inventory Staff | [4] Auditor: ");
            String roleInput = sc.nextLine().trim();
            if (roleInput.equals("1")) {
                role = "ADMIN";
                break;
            } else if (roleInput.equals("2")) {
                role = "PHARMACIST";
                break;
            } else if (roleInput.equals("3")) {
                role = "INVENTORY STAFF";
                break;
            } else if (roleInput.equals("4")) {
                role = "AUDITOR";
                break;
            }
            System.out.println("Invalid choice. Please enter 1, 2, 3, or 4 only.");
        }

        userDao.registerUser(fullname, username, password, role);
    }

    public void handleLogin() {
        String selectedRole = selectRole();
        if (selectedRole == null) return;

        System.out.print("Enter Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = userDao.login(username, password);
        if (user == null) return;

        if (!user.roles().equals(selectedRole)) {
            System.out.println("\n -> This account is not a " + selectedRole + " account.");
            return;
        }

        System.out.println("\n -> Welcome " + user.full_name() + " | ROLE: " + user.roles());

        switch (user.roles()) {
            case "ADMIN" -> new AdminMenu(sc, userDao).display(user);
            case "PHARMACIST" -> new PharmacistMenu(sc).display(user);
            case "INVENTORY STAFF" -> new InventoryStaffMenu(sc).display(user);
            case "AUDITOR" -> new AuditorMenu(sc, userDao).display(user);
            default -> System.out.println("Account role not found!");
        }
    }
}