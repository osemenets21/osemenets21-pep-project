package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    /**
     * Creates a new message with validation.
     */
    public Message createMessage(Message message) {
        if (message.getMessage_text() == null || message.getMessage_text().isBlank()) {
            return null; // Message text cannot be empty
        }
        if (message.getMessage_text().length() > 255) {
            return null; // Message text exceeds character limit
        }
        if (!messageDAO.doesUserExist(message.getPosted_by())) {
            return null; // User does not exist
        }
        return messageDAO.createMessage(message);
    }

    /**
     * Retrieves all messages from the database.
     */
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    /**
     * Retrieves a message by its ID.
     */
    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }

    /**
     * Deletes a message by its ID.
     */
    public Message deleteMessageById(int messageId) {
        return messageDAO.deleteMessageById(messageId);
    }

    /**
     * Updates a message text by its ID.
     */
    public Message updateMessage(int messageId, String newText) {
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            return null; // Invalid new message text
        }
        if (messageDAO.getMessageById(messageId) == null) {
            return null; // Message does not exist
        }
        return messageDAO.updateMessage(messageId, newText);
    }

    /**
     * Retrieves all messages posted by a specific user.
     */
    public List<Message> getMessagesByUser(int accountId) {
        if (!messageDAO.doesUserExist(accountId)) {
            return List.of(); // Return empty list if user doesn't exist
        }
        return messageDAO.getMessagesByUser(accountId);
    }
}
