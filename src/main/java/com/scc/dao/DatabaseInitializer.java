package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initailize() {
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

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(users);
            stmt.execute(supplies);
            System.out.println("-> Database tables verified.");
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
}