package DAO;

import Model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message;";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    

    public boolean doesUserExist(int accountId) {
        String query = "SELECT COUNT(*) FROM account WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public Message createMessage(Message message) {
        if (!doesUserExist(message.getPosted_by())) {
            return null; 
        }
    
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getPosted_by());
            stmt.setString(2, message.getMessage_text());
            stmt.setLong(3, message.getTime_posted_epoch());
            stmt.executeUpdate();
    
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                message.setMessage_id(rs.getInt(1));
                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message deleteMessageById(int messageId) {
        String selectQuery = "SELECT * FROM message WHERE message_id = ?";
        String deleteQuery = "DELETE FROM message WHERE message_id = ?";
    
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, messageId);
            ResultSet rs = selectStmt.executeQuery();
    
            if (rs.next()) {
                System.out.println("Message found before deletion: " +
                    rs.getInt("message_id") + ", " +
                    rs.getInt("posted_by") + ", " +
                    rs.getString("message_text") + ", " +
                    rs.getLong("time_posted_epoch"));
    
                Message deletedMessage = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
    
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, messageId);
                    int rowsDeleted = deleteStmt.executeUpdate();
    
                    if (rowsDeleted > 0) {
                        System.out.println("Message successfully deleted.");
                        return deletedMessage; 
                    }
                }
            } else {
                System.out.println("Message not found before deletion.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if the message was not found
    }   
}
