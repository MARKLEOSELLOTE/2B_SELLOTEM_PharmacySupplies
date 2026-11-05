package com.scc.dao;

import com.scc.model.StockTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StockTransactionDao {

    private static final String SELECT =
            "SELECT t.*, s.name AS supply_name FROM stock_transactions t "
                    + "JOIN supplies s ON s.supply_id = t.supply_id ";

    private StockTransaction map(ResultSet rs) throws SQLException {
        return new StockTransaction(
                rs.getInt("transaction_id"),
                rs.getInt("supply_id"),
                rs.getString("supply_name"),
                rs.getInt("user_id"),
                rs.getString("transaction_type"),
                rs.getInt("quantity"),
                rs.getString("customer"),
                rs.getDouble("total"),
                rs.getString("transaction_date"),
                rs.getString("remarks"),
                rs.getInt("is_voided") == 1);
    }

    /** Returns the new transaction ID, or -1 if it failed. */
    public int addTransaction(int supplyId, int userId, String type, int quantity,
                              String customer, double total) {
        String sql = "INSERT INTO stock_transactions (supply_id, user_id, transaction_type, quantity, customer, total) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, supplyId);
            ps.setInt(2, userId);
            ps.setString(3, type);
            ps.setInt(4, quantity);
            ps.setString(5, customer);
            ps.setDouble(6, total);
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Add transaction failed: " + e.getMessage());
        }
        return -1;
    }

    public List<StockTransaction> getActiveStockOut() {
        List<StockTransaction> list = new ArrayList<>();
        String sql = SELECT + "WHERE t.transaction_type = 'STOCK_OUT' AND t.is_voided = 0 "
                + "ORDER BY t.transaction_id DESC LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            System.out.println("Load transactions failed: " + e.getMessage());
        }
        return list;
    }

    public StockTransaction getById(int id) {
        String sql = SELECT + "WHERE t.transaction_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.out.println("Find transaction failed: " + e.getMessage());
        }
        return null;
    }

    public boolean voidTransaction(int id, String reason) {
        String sql = "UPDATE stock_transactions SET is_voided = 1, remarks = ? "
                + "WHERE transaction_id = ? AND is_voided = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "VOIDED: " + reason);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Void transaction failed: " + e.getMessage());
            return false;
        }
    }
}