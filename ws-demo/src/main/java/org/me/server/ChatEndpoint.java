package org.me.server;


import org.me.model.Message;
import org.me.transform.MessageDecoder;
import org.me.transform.MessageEncoder;


import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/chat/{username}", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEndpoint {

    private Session session;
    private static Set<ChatEndpoint> chatEndpoints
            = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
        // Get session and WebSocket connection
        System.out.println("onOpen");
        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);
        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        // Handle new messages
        message.setFrom(users.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        // WebSocket connection closes
        chatEndpoints.remove(this);
        users.remove(session.getId());

        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {
        chatEndpoints.forEach( chatEndpoint -> {
            try {
                chatEndpoint.session.getBasicRemote().sendObject(message);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        });
    }
}