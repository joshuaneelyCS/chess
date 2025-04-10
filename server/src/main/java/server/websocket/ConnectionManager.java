package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    @Override
    public String toString() {
        return "ConnectionManager{" +
                "games=" + games +
                '}';
    }

    public final ConcurrentHashMap<Integer, ArrayList<Connection>> games = new ConcurrentHashMap<>();

    // Adds a connection to the WebSocket
    public void add(int gameID, String authToken, Session session) {
        var connection = new Connection(authToken, session);

        // Use computeIfAbsent to avoid overwriting existing lists
        games.computeIfAbsent(gameID, k -> new ArrayList<>()).add(connection);
    }

    // Removes a connection from the WebSocket
    public void remove(String authToken) {

        for (var connections : games.values()) {
            for (var connection : connections) {
                if (connection.authToken.equals(authToken)) {
                    connections.remove(connection);
                    return;
                }
            }
        }
    }

    public Session getSession(String authToken) {
        for (var connections : games.values()) {
            for (var connection : connections) {
                if (connection.authToken.equals(authToken)) {
                    return connection.session;
                }
            }
        }
        return null; // Not found
    }


    // Broadcasts to all connections to the WebSocket
    public void broadcast(String authToken, ServerMessage serverMessage, boolean exclude) throws IOException {
        Integer gameID = null;

        // Step 1: Find the gameID for the given authToken
        for (var entry : games.entrySet()) {
            for (var connection : entry.getValue()) {
                if (connection.authToken.equals(authToken)) {
                    gameID = entry.getKey();
                    break;
                }
            }
            if (gameID != null) break;
        }

        if (gameID == null) { return; } // AuthToken not found in any game

        // Step 2: Get all connections for that gameID
        var connections = games.get(gameID);
        if (connections == null) { return; }

        List<Connection> toRemove = new ArrayList<>();

        // Step 3: Send the message to everyone except the sender
        for (var connection : connections) {
            if (exclude == false || !connection.authToken.equals(authToken)) {
                if (connection.session.isOpen()) {
                    connection.send(serverMessage);
                } else {
                    toRemove.add(connection); // Mark closed connections for removal
                }
            }
        }

        // Step 4: Remove any closed connections
        connections.removeAll(toRemove);

    }
}
