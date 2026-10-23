package com.scc.ui;

import com.scc.dao.SupplyDao;
import com.scc.model.Supply;
import com.scc.model.User;
import java.util.Scanner;

public class PharmacistMenu {
    private final Scanner sc;
    private final SupplyDao supplyDao = new SupplyDao();

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
                    ConsoleUtil.printSupplies(supplyDao.getAllSupplies());
                    break;
                case "2":
                    String keyword = ConsoleUtil.readText(sc, "Search name: ");
                    ConsoleUtil.printSupplies(supplyDao.searchByName(keyword));
                    break;
                case "3":
                    dispense();
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

    private void dispense() {
        ConsoleUtil.printSupplies(supplyDao.getAllSupplies());
        int id = ConsoleUtil.readInt(sc, "Enter product ID to dispense (0 to cancel): ");
        if (id == 0) return;

        Supply s = supplyDao.getById(id);
        if (s == null) {
            System.out.println("Product not found.");
            return;
        }

        int qty = ConsoleUtil.readInt(sc, "Quantity to dispense: ");
        if (qty == 0) return;

        if (supplyDao.adjustQuantity(id, -qty)) {
            System.out.println("Dispensed " + qty + " of " + s.name() + ".");
        } else {
            System.out.println("Not enough stock. Available: " + s.quantity());
        }
    }
}