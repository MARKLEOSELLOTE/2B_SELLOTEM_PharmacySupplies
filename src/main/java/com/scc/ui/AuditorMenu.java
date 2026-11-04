package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class AuditorMenu {
    private final Scanner sc;
    private final UserDao userDao;

    public AuditorMenu(Scanner sc, UserDao userDao) {
        this.sc = sc;
        this.userDao = userDao;
    }

    public void display(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==========================================");
            System.out.println("   PHARMACY SUPPLIES MANAGEMENT");
            System.out.println("              AUDITOR MENU");
            System.out.println("==========================================");
            System.out.println(" [1] View All Supplies");
            System.out.println(" [2] View Low Stock");
            System.out.println(" [3] View Expired / Near Expiry (30 days)");
            System.out.println(" [4] View All Users");
            System.out.println(" [0] Logout");
            System.out.print("Enter choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    comingSoon("VIEW ALL SUPPLIES");
                    break;
                case "2":
                    comingSoon("VIEW LOW STOCK");
                    break;
                case "3":
                    comingSoon("VIEW EXPIRED / NEAR EXPIRY (30 DAYS)");
                    break;
                case "4":
                    comingSoon("VIEW ALL USERS");
                    break;
                case "0":
                    System.out.println("\nLogging out...\n");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void comingSoon(String title) {
        System.out.println("\n==========================================");
        System.out.println("   " + title);
        System.out.println("==========================================");
        System.out.println();
        System.out.println("            COMING SOON...");
        System.out.println();
        System.out.println("   This feature is still under development.");
        System.out.println("==========================================");
        System.out.print("\nPress Enter to go back...");
        sc.nextLine();
    }
}