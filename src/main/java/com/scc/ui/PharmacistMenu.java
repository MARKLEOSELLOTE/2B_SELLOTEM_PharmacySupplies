package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.StockTransactionDao;
import com.scc.dao.SupplyDao;
import com.scc.model.StockTransaction;
import com.scc.model.Supply;
import com.scc.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PharmacistMenu {
    private final Scanner sc;
    private final SupplyDao supplyDao = new SupplyDao();
    private final StockTransactionDao txnDao = new StockTransactionDao();
    private final ActivityLogDao logDao = new ActivityLogDao();

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
            System.out.println(" [3] Dispense Supply (Stock-Out)");
            System.out.println(" [4] Void Dispense Transaction");
            System.out.println(" [0] Logout");
            System.out.print("Enter choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    ConsoleUtil.printSupplies(availableOnly(supplyDao.getAllSupplies()));
                    break;
                case "2":
                    String keyword = ConsoleUtil.readText(sc, "Search name: ");
                    ConsoleUtil.printSupplies(supplyDao.searchByName(keyword));
                    break;
                case "3":
                    dispense(user);
                    break;
                case "4":
                    voidDispense(user);
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

    private List<Supply> availableOnly(List<Supply> all) {
        return all.stream().filter(s -> s.quantity() > 0).toList();
    }

    private void dispense(User user) {
        ConsoleUtil.printSupplies(availableOnly(supplyDao.getAllSupplies()));
        int id = ConsoleUtil.readInt(sc, "Enter product ID to dispense (0 to cancel): ");
        if (id == 0) return;

        Supply s = supplyDao.getById(id);
        if (s == null) {
            System.out.println("Product not found.");
            return;
        }

        if (s.expiry_date() != null && s.expiry_date().compareTo(LocalDate.now().toString()) < 0) {
            System.out.println("Cannot dispense. Product is expired (" + s.expiry_date() + ").");
            return;
        }

        String customer = ConsoleUtil.readText(sc, "Customer name: ");
        int qty = ConsoleUtil.readInt(sc, "Quantity to dispense (0 to cancel): ");
        if (qty == 0) return;

        if (qty > s.quantity()) {
            System.out.println("Insufficient stock. Available: " + s.quantity());
            return;
        }

        if (supplyDao.adjustQuantity(id, -qty)) {
            double total = qty * s.price();
            int txnId = txnDao.addTransaction(id, user.user_Id(), "STOCK_OUT", qty, customer, total);
            System.out.printf("Dispensed %d x %s to %s. Total: P%.2f (Transaction #%d)%n",
                    qty, s.name(), customer, total, txnId);
            logDao.logAction(user.user_Id(), "DISPENSE",
                    String.format("Dispensed %d x %s (Txn #%d) to %s - Total: P%.2f",
                            qty, s.name(), txnId, customer, total));
        } else {
            System.out.println("Dispense failed.");
        }
    }

    private void voidDispense(User user) {
        List<StockTransaction> list = txnDao.getActiveStockOut();
        if (list.isEmpty()) {
            System.out.println("\nNo active dispense transactions to void.");
            return;
        }
        printTransactions(list);

        int id = ConsoleUtil.readInt(sc, "Enter transaction ID to void (0 to cancel): ");
        if (id == 0) return;

        StockTransaction t = txnDao.getById(id);
        if (t == null || !t.transaction_type().equals("STOCK_OUT")) {
            System.out.println("Transaction not found.");
            return;
        }
        if (t.is_voided()) {
            System.out.println("Transaction is already voided.");
            return;
        }

        String reason = ConsoleUtil.readText(sc, "Reason for voiding (required): ");
        String confirm = ConsoleUtil.readText(sc, "Void Txn #" + id + " and restore stock? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            return;
        }

        if (txnDao.voidTransaction(id, reason)) {
            supplyDao.adjustQuantity(t.supply_id(), t.quantity());
            System.out.println("Transaction voided. Stock restored: +" + t.quantity() + " " + t.supply_name());
            logDao.logAction(user.user_Id(), "VOID_TRANSACTION",
                    String.format("Updated Txn #%d (%s x%d) status from ACTIVE to VOIDED - Reason: %s",
                            id, t.supply_name(), t.quantity(), reason));
        } else {
            System.out.println("Void failed.");
        }
    }

    private void printTransactions(List<StockTransaction> list) {
        String fmt = "%-6s %-22s %-5s %-18s %-10s %-19s%n";
        System.out.println();
        System.out.printf(fmt, "TXN", "PRODUCT", "QTY", "CUSTOMER", "TOTAL", "DATE");
        System.out.println("-".repeat(84));
        for (StockTransaction t : list) {
            System.out.printf(fmt, t.transaction_id(), t.supply_name(), t.quantity(),
                    t.customer() == null ? "-" : t.customer(),
                    String.format("%.2f", t.total()), t.transaction_date());
        }
    }
}