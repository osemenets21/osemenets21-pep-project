package Controller;

import Service.MessageService;
import Service.AccountService;
import Model.Message;
import DAO.AccountDAO;
import DAO.MessageDAO;
import Util.ConnectionUtil;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.sql.Connection;
import java.util.List;

public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;
    private Gson gson = new Gson();

    public SocialMediaController() {
        Connection conn = ConnectionUtil.getConnection();
        this.accountService = new AccountService(new AccountDAO(conn));
        this.messageService = new MessageService(new MessageDAO(conn));
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Message API Endpoints
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);

        return app;
    }

    /**
     * Handle message creation with validation.
     */
    private void createMessageHandler(Context ctx) {
        Message message = gson.fromJson(ctx.body(), Message.class);
        Message createdMessage = messageService.createMessage(message);

        if (createdMessage != null) {
            ctx.status(200);
            ctx.json(createdMessage); // Return full message (including message_id)
        } else {
            ctx.status(400);
            ctx.result(""); // Return empty body for failed validation
        }
    }

    /**
     * Handle retrieving all messages.
     */
    private void getAllMessagesHandler(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }
}
