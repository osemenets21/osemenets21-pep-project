package DAO;

import Model.Account;
import java.sql.*;

public class AccountDAO {
    private final Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new account in the database.
     */
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword()); // TODO: Hash the password for security!

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setAccount_id(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves an account by username.
     */
    public Account getAccountByUsername(String username) {
        String sql = "SELECT * FROM account WHERE username = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving account by username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verifies login credentials.
     */
    public Account verifyLogin(String username, String password) {
        String sql = "SELECT * FROM account WHERE username = ? AND password = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // TODO: Compare hashed password instead of plain text.

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
