package Service;

import DAO.MessageDAO;
import Model.Message;
import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO;

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
        if (!messageDAO.doesUserExist(message.getPosted_by())) {
            return null; 
        }
        return messageDAO.createMessage(message);
    }
    


    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }


    public Message deleteMessageById(int messageId) {
        return messageDAO.deleteMessageById(messageId);
    }


    public Message updateMessage(int messageId, String newText) {
        if (newText == null || newText.isBlank() || newText.length() > 255) {
            return null; 
        }
        if (messageDAO.getMessageById(messageId) == null) {
            return null;
        }
        return messageDAO.updateMessage(messageId, newText);
    }

  
    public List<Message> getMessagesByUser(int accountId) {
        if (!messageDAO.doesUserExist(accountId)) {
            return List.of(); 
        }
        return messageDAO.getMessagesByUser(accountId);
    }
}
