package Controller;

import Service.MessageService;
import Service.AccountService;
import Model.Message;
import Model.Account;
import DAO.AccountDAO;
import DAO.MessageDAO;
import Util.ConnectionUtil;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.sql.Connection;
import java.util.List;

public class SocialMediaController {
    private final AccountService accountService;
    private final MessageService messageService;
    private final Gson gson = new Gson();

    public SocialMediaController() {
        Connection conn = ConnectionUtil.getConnection();
        this.accountService = new AccountService(new AccountDAO(conn));
        this.messageService = new MessageService(new MessageDAO(conn));
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Account Endpoints
        app.post("/register", this::registerUserHandler);
        app.post("/login", this::loginUserHandler);

        // Message Endpoints
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUserHandler);

        return app;
    }

    /**
     * Registers a new user.
     */
    private void registerUserHandler(Context ctx) {
        Account account = gson.fromJson(ctx.body(), Account.class);
        Account createdAccount = accountService.register(account);
        if (createdAccount != null) {
            ctx.status(200).json(createdAccount);
        } else {
            ctx.status(400).result("");
        }
    }

    /**
     * Logs in a user.
     */
    private void loginUserHandler(Context ctx) {
        Account account = gson.fromJson(ctx.body(), Account.class);
        Account loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
        if (loggedInAccount != null) {
            ctx.status(200).json(loggedInAccount);
        } else {
            ctx.status(401).result("");
        }
    }

    /**
     * Creates a new message.
     */
    private void createMessageHandler(Context ctx) {
        Message message = gson.fromJson(ctx.body(), Message.class);
        Message createdMessage = messageService.createMessage(message);
        if (createdMessage != null) {
            ctx.status(200).json(createdMessage);
        } else {
            ctx.status(400).result("");
        }
    }

    /**
     * Retrieves all messages.
     */
    private void getAllMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    /**
     * Retrieves a message by ID.
     */
    private void getMessageByIdHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            ctx.json(message);
        } else {
            ctx.status(200).result("");
        }
    }

    /**
     * Deletes a message by ID.
     */
    private void deleteMessageHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessageById(messageId);
        if (deletedMessage != null) {
            ctx.json(deletedMessage);
        } else {
            ctx.status(200).result("");
        }
    }

    /**
     * Updates a message text by ID.
     */
    private void updateMessageHandler(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = gson.fromJson(ctx.body(), Message.class);
        Message updatedMessage = messageService.updateMessage(messageId, message.getMessage_text());
        if (updatedMessage != null) {
            ctx.json(updatedMessage);
        } else {
            ctx.status(400).result("");
        }
    }

    /**
     * Retrieves all messages posted by a specific user.
     */
    private void getMessagesByUserHandler(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessagesByUser(accountId);
        ctx.json(messages);
    }
}
