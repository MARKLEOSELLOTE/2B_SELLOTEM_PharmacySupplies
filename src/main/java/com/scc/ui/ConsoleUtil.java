package com.scc.ui;

import com.scc.model.Supply;
import java.util.List;
import java.util.Scanner;

public class ConsoleUtil {

    public static void printSupplies(List<Supply> supplies) {
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

    public static String readText(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Cannot be empty.");
        }
    }

    public static int readInt(Scanner sc, String prompt) {
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

    public static double readDouble(Scanner sc, String prompt) {
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