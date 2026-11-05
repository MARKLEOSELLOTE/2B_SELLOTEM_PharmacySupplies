package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String users = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "full_name TEXT NOT NULL, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "roles TEXT NOT NULL, "
                + "status TEXT NOT NULL DEFAULT 'PENDING', "
                + "date_created TEXT DEFAULT CURRENT_TIMESTAMP"
                + ")";

        String supplies = "CREATE TABLE IF NOT EXISTS supplies ("
                + "supply_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "category TEXT NOT NULL, "
                + "quantity INTEGER NOT NULL DEFAULT 0, "
                + "price REAL NOT NULL DEFAULT 0, "
                + "expiry_date TEXT, "
                + "reorder_level INTEGER NOT NULL DEFAULT 10"
                + ")";

        String stockTransactions = "CREATE TABLE IF NOT EXISTS stock_transactions ("
                + "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "supply_id INTEGER NOT NULL, "
                + "user_id INTEGER NOT NULL, "
                + "transaction_type TEXT NOT NULL, "
                + "quantity INTEGER NOT NULL, "
                + "customer TEXT, "
                + "total REAL NOT NULL DEFAULT 0, "
                + "transaction_date TEXT NOT NULL DEFAULT (datetime('now','localtime')), "
                + "remarks TEXT, "
                + "is_voided INTEGER NOT NULL DEFAULT 0, "
                + "FOREIGN KEY (supply_id) REFERENCES supplies(supply_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                + ")";

        String activityLogs = "CREATE TABLE IF NOT EXISTS activity_logs ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "action_type TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "timestamp TEXT NOT NULL DEFAULT (datetime('now','localtime')), "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                + ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(users);
            stmt.execute(supplies);
            stmt.execute(stockTransactions);
            stmt.execute(activityLogs);
            System.out.println("-> Database tables verified.");
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
}