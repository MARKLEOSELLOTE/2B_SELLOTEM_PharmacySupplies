package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class AdminMenu {
    private final Scanner sc;
    private final UserDao userDao;

    public AdminMenu(Scanner sc, UserDao userDao){
        this.userDao = userDao;
        this.sc = sc;
    }

    public void display (User admin){
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==========================================");
            System.out.println("   PHARMACY SUPPLIES INVENTORY SYSTEM");
            System.out.println("              ADMIN MENU");
            System.out.println("==========================================");
            System.out.println();
            System.out.println(" [1] Manage Products");
            System.out.println(" [2] Manage Suppliers");
            System.out.println(" [3] Stock In (Record Delivery)");
            System.out.println(" [4] Stock Out / Adjust Stock");
            System.out.println(" [5] Manage User Accounts");
            System.out.println(" [6] Reports");
            System.out.println(" [7] View Audit Logs");
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
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    System.out.println("\nComing soon...\n");
                    break;
                case 0:
                    System.out.println("\nLogging out...\n");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select from 0 to 7.\n");
            }
        }
    }
}
