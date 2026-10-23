package com.scc.dao;

import com.scc.model.Supply;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplyDao {

    private Supply map(ResultSet rs) throws SQLException {
        return new Supply(
                rs.getInt("supply_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price"),
                rs.getString("expiry_date"),
                rs.getInt("reorder_level"));
    }

    private List<Supply> query(String sql) {
        List<Supply> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            System.out.println("Load supplies failed: " + e.getMessage());
        }
        return list;
    }

    public boolean addSupply(String name, String category, int quantity, double price, String expiry, int reorder) {
        String sql = "INSERT INTO supplies (name, category, quantity, price, expiry_date, reorder_level) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);
            ps.setString(5, expiry);
            ps.setInt(6, reorder);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Add supply failed: " + e.getMessage());
            return false;
        }
    }

    public List<Supply> getAllSupplies() {
        return query("SELECT * FROM supplies ORDER BY supply_id");
    }

    public List<Supply> getLowStock() {
        return query("SELECT * FROM supplies WHERE quantity <= reorder_level ORDER BY quantity");
    }

    public List<Supply> getExpiringSoon() {
        return query("SELECT * FROM supplies WHERE expiry_date IS NOT NULL AND expiry_date <= date('now', '+30 day') ORDER BY expiry_date");
    }

    public List<Supply> searchByName(String keyword) {
        List<Supply> list = new ArrayList<>();
        String sql = "SELECT * FROM supplies WHERE name LIKE ? ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
        return list;
    }

    public Supply getById(int id) {
        String sql = "SELECT * FROM supplies WHERE supply_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Find supply failed: " + e.getMessage());
        }
        return null;
    }

    public boolean adjustQuantity(int id, int delta) {
        String sql = "UPDATE supplies SET quantity = quantity + ? WHERE supply_id = ? AND quantity + ? >= 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, id);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Adjust quantity failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSupply(int id, String name, String category, double price, String expiry, int reorder) {
        String sql = "UPDATE supplies SET name = ?, category = ?, price = ?, expiry_date = ?, reorder_level = ? WHERE supply_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setString(4, expiry);
            ps.setInt(5, reorder);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update supply failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSupply(int id) {
        String sql = "DELETE FROM supplies WHERE supply_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Delete supply failed: " + e.getMessage());
            return false;
        }
    }
}