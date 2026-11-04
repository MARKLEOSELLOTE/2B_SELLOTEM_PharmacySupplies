package com.scc.ui;

import com.scc.model.User;
import java.util.Scanner;

public class PharmacistMenu {
    private final Scanner sc;

    public PharmacistMenu(Scanner sc) {
        this.sc = sc;
    }

    public void display(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==========================================");
            System.out.println("   PHARMACY SUPPLIES MANAGEMENT");
            System.out.println("            PHARMACIST MENU");
            System.out.println("==========================================");
            System.out.println(" [1] View Available Supplies");
            System.out.println(" [2] Search Supply");
            System.out.println(" [3] Dispense Supply");
            System.out.println(" [0] Logout");
            System.out.print("Enter choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    comingSoon("VIEW AVAILABLE SUPPLIES");
                    break;
                case "2":
                    comingSoon("SEARCH SUPPLY");
                    break;
                case "3":
                    comingSoon("DISPENSE SUPPLY");
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