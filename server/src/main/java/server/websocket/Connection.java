package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;
    public boolean locked = false;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }

    public void send(ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    public void close() throws IOException {
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }
}