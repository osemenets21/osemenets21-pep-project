package DAO;

import Model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private final Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves all messages from the database.
     */
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
            System.err.println("Error retrieving messages: " + e.getMessage());
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Checks if a user exists in the database.
     */
    public boolean doesUserExist(int accountId) {
        String query = "SELECT COUNT(*) FROM account WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new message if the user exists.
     */
    public Message createMessage(Message message) {
        if (!doesUserExist(message.getPosted_by())) {
            System.out.println("User does not exist: " + message.getPosted_by());
            return null;
        }

        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getPosted_by());
            stmt.setString(2, message.getMessage_text());
            stmt.setLong(3, message.getTime_posted_epoch());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating message failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setMessage_id(rs.getInt(1));
                    return message;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating message: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a message by its ID and returns the deleted message.
     */
    public Message deleteMessageById(int messageId) {
        String selectQuery = "SELECT * FROM message WHERE message_id = ?";
        String deleteQuery = "DELETE FROM message WHERE message_id = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, messageId);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
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
                    System.out.println("Message not found, cannot delete.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting message: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Message getMessageById(int messageId) {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getInt("message_id"),
                            rs.getInt("posted_by"),
                            rs.getString("message_text"),
                            rs.getLong("time_posted_epoch")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving message by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if message is not found
    }

    public Message updateMessage(int messageId, String newText) {
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
    
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newText);
            stmt.setInt(2, messageId);
    
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return getMessageById(messageId); // Fetch the updated message
            }
        } catch (SQLException e) {
            System.err.println("Error updating message: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if update failed
    }
    
    
}
