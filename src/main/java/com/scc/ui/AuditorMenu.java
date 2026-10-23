package com.scc.ui;

import com.scc.dao.SupplyDao;
import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class AuditorMenu {
    private final Scanner sc;
    private final UserDao userDao;
    private final SupplyDao supplyDao = new SupplyDao();

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
                    ConsoleUtil.printSupplies(supplyDao.getAllSupplies());
                    break;
                case "2":
                    ConsoleUtil.printSupplies(supplyDao.getLowStock());
                    break;
                case "3":
                    ConsoleUtil.printSupplies(supplyDao.getExpiringSoon());
                    break;
                case "4":
                    printUsers();
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

    private void printUsers() {
        System.out.println();
        System.out.printf("%-5s %-25s %-15s %-18s %-10s%n", "ID", "FULL NAME", "USERNAME", "ROLE", "STATUS");
        System.out.println("-------------------------------------------------------------------------");
        for (User u : userDao.getAllUsers()) {
            System.out.printf("%-5d %-25s %-15s %-18s %-10s%n",
                    u.user_Id(), u.full_name(), u.username(), u.roles(), u.status());
        }
    }
}