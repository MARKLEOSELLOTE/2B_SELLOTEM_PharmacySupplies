package com.scc.dao;

import com.scc.model.User;

import java.sql.*;

public class UserDao {

    public UserDao() {

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            defaultAdminGenerator(conn);
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }

    }

    public void defaultAdminGenerator(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE roles ='ADMIN'";
        String insertSql = "INSERT INTO users(full_name, username, password, roles, status) VALUES('Pharmacist Administrator', 'admin', 'admin123', 'ADMIN', 'APPROVED')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertSql);
                System.out.println("\n -> Default Admin created");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }

    }

    public boolean registerUser(String full_name,String username, String password, String roles ){

        String sql= "INSERT INTO users(full_name, username, password, roles, status) VALUES(?, ?, ?, ?, 'PENDING')";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, full_name);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, roles);
            pstmt.executeUpdate();
            System.out.println("\n -> Successful Registration, Waiting for Approval!");
            return true;
        }catch (SQLException e) {
            System.err.println("Failed " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password){
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, password);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    String status = rs.getString("status");
                    if("PENDING".equalsIgnoreCase(status)){
                        System.out.println("\n -> Account is pending, Waiting for Approval");
                        return null;
                    }
                    return new User(
                            rs.getInt("user_Id"),
                            rs.getString("full_Name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status"),
                            rs.getString("date_created")
                    );
                }else{
                    System.out.println("\n -> Invalid username or password");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

}
