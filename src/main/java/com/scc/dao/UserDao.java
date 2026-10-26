    package com.scc.dao;

    import com.scc.model.User;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class UserDao {

        public UserDao() {

            try (Connection conn = DatabaseConnection.getConnection()) {
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

        public boolean registerUser(String full_name, String username, String password, String roles) {

            String sql = "INSERT INTO users(full_name, username, password, roles, status) VALUES(?, ?, ?, ?, 'PENDING')";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, full_name);
                pstmt.setString(2, username);
                pstmt.setString(3, password);
                pstmt.setString(4, roles);
                pstmt.executeUpdate();
                System.out.println("\n -> Successful Registration, Waiting for Approval!");
                return true;
            } catch (SQLException e) {
                System.err.println("Failed " + e.getMessage());
                return false;
            }
        }

        public User login(String username, String password) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        if ("PENDING".equalsIgnoreCase(status)) {
                            System.out.println("\n -> Account is pending, Waiting for Approval");
                            return null;
                        }
                        if ("REJECTED".equalsIgnoreCase(status)) {
                            System.out.println("\n -> Your account was rejected. Contact the administrator.");
                            return null;
                        }
                        return mapUser(rs);
                    } else {
                        System.out.println("\n -> Invalid username or password");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
            return null;
        }

        private User mapUser(ResultSet rs) throws SQLException {
            return new User(
                    rs.getInt("user_Id"),
                    rs.getString("full_Name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("roles"),
                    rs.getString("status"),
                    rs.getString("date_created")
            );
        }

        public List<User> getAllUsers() {
            List<User> list = new ArrayList<>();
            String sql = "SELECT * FROM users ORDER BY user_id";
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            } catch (SQLException e) {
                System.out.println("Load users failed: " + e.getMessage());
            }
            return list;
        }

        public List<User> getPendingUsers() {
            List<User> list = new ArrayList<>();
            String sql = "SELECT * FROM users WHERE status = 'PENDING' ORDER BY user_id";
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            } catch (SQLException e) {
                System.out.println("Load pending users failed: " + e.getMessage());
            }
            return list;
        }

        public boolean updateStatus(int userId, String status) {
            String sql = "UPDATE users SET status = ? WHERE user_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, userId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Update status failed: " + e.getMessage());
                return false;
            }
        }

        public boolean deleteUser(int userId) {
            String sql = "DELETE FROM users WHERE user_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Delete user failed: " + e.getMessage());
                return false;
            }
        }

        public boolean updateUserRoleAndStatus(int targetUserId, String newRole, String newStatus) {
            String sql = "UPDATE users SET roles = ?, status = ? WHERE user_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newRole.toUpperCase());
                ps.setString(2, newStatus.toUpperCase());
                ps.setInt(3, targetUserId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Update user failed: " + e.getMessage());
                return false;
            }
        }

        public boolean createUser(String fullName, String username, String password, String role, String status) {
            String sql = "INSERT INTO users(full_name, username, password, roles, status) VALUES(?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, role.toUpperCase());
                ps.setString(5, status.toUpperCase());
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                    System.out.println("Username already exists.");
                } else {
                    System.out.println("Create user failed: " + e.getMessage());
                }
                return false;
            }
        }
    }