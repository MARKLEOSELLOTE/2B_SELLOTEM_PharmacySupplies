package com.scc.ui;

import com.scc.dao.SupplyDao;
import com.scc.model.Supply;
import com.scc.model.User;
import java.util.Scanner;

public class InventoryStaffMenu {
    private final Scanner sc;
    private final SupplyDao supplyDao = new SupplyDao();

    public InventoryStaffMenu(Scanner sc) {
        this.sc = sc;
    }

    public void display(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==========================================");
            System.out.println("   PHARMACY SUPPLIES MANAGEMENT");
            System.out.println("          INVENTORY STAFF MENU");
            System.out.println("==========================================");
            System.out.println(" [1] View All Supplies");
            System.out.println(" [2] Add New Supply");
            System.out.println(" [3] Stock In (Add Quantity)");
            System.out.println(" [4] View Low Stock");
            System.out.println(" [0] Logout");
            System.out.print("Enter choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    ConsoleUtil.printSupplies(supplyDao.getAllSupplies());
                    break;
                case "2":
                    addSupply();
                    break;
                case "3":
                    stockIn();
                    break;
                case "4":
                    ConsoleUtil.printSupplies(supplyDao.getLowStock());
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

    private void addSupply() {
        String name = ConsoleUtil.readText(sc, "Product name: ");
        String category = ConsoleUtil.readText(sc, "Category: ");
        int quantity = ConsoleUtil.readInt(sc, "Quantity: ");
        double price = ConsoleUtil.readDouble(sc, "Price: ");
        String expiry = ConsoleUtil.readText(sc, "Expiry date (YYYY-MM-DD): ");
        int reorder = ConsoleUtil.readInt(sc, "Reorder level: ");

        if (supplyDao.addSupply(name, category, quantity, price, expiry, reorder)) {
            System.out.println("Product added.");
        }
    }

    private void stockIn() {
        ConsoleUtil.printSupplies(supplyDao.getAllSupplies());
        int id = ConsoleUtil.readInt(sc, "Enter product ID (0 to cancel): ");
        if (id == 0) return;

        Supply s = supplyDao.getById(id);
        if (s == null) {
            System.out.println("Product not found.");
            return;
        }

        int qty = ConsoleUtil.readInt(sc, "Quantity received: ");
        if (qty == 0) return;

        if (supplyDao.adjustQuantity(id, qty)) {
            System.out.println("Added " + qty + " to " + s.name() + ".");
        }
    }
}