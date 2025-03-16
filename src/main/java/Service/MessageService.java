package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public Message createMessage(Message message) {
        if (message.getMessage_text() == null || message.getMessage_text().isBlank()) {
            return null;
        }
        if (message.getMessage_text().length() > 255) {
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
}
