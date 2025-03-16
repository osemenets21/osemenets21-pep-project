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
            System.out.println("Error: Message text cannot be empty.");
            return null;
        }
        if (message.getMessage_text().length() > 255) {
            System.out.println("Error: Message text exceeds 255 characters.");
            return null;
        }
        if (message.getPosted_by() <= 0) {
            System.out.println("Error: Invalid user ID.");
            return null;
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
        Message deletedMessage = messageDAO.deleteMessageById(messageId);
        if (deletedMessage == null) {
            System.out.println("Error: Message not found for deletion.");
        }
        return deletedMessage;
    }

    /**
     * Updates a message text by its ID.
     */
    public Message updateMessage(int messageId, String newText) {
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            System.out.println("Error: Invalid new message text.");
            return null;
        }
        return messageDAO.updateMessage(messageId, newText);
    }
}
