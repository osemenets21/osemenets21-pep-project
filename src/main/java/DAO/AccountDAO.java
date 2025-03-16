package DAO; 

import Model.Account; 
import java.sql.*;

public class AccountDAO {
    private Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean createAccount(Account account) {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?) RETURNING account_id;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                account.setAccount_id(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccountByUsername(String username) {
        String sql = "SELECT * FROM account WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account verifyLogin(String username, String password) {
        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
