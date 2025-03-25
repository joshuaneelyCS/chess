package server;

import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public record RegisterRequest(String username, String password, String email) {};
    public record LoginRequest(String username, String password) {};
    public record ListGameResponse(GameData[] games) { }

    public record CreateGameRequest(String gameName) { }
    public record CreateGameResponse(int gameID) { };

    public record JoinGameRequest(String token, String playerColor, int gameID) { }

    public AuthData register(String username, String password, String email) throws Exception {
        var path = "/user";
        var request = new RegisterRequest(username, password, email);
        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var request = new LoginRequest(username, password);
        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public void clearApplication() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public void logout(String token) throws Exception {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, token);
    }

    public GameData[] listGames(String token) throws Exception {
        var path = "/game";
        var response = this.makeRequest("GET", path, null, ListGameResponse.class, token);
        return response.games();
    }

    public int createGame(String token, String gameName) throws Exception {
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        var response = this.makeRequest("POST", path, request, CreateGameResponse.class, token);
        return response.gameID();
    }

    public void joinGame(String token, String playerColor, int gameID) throws Exception {
        var path = "/game";
        var request = new JoinGameRequest(token, playerColor, gameID);
        this.makeRequest("PUT", path, request, null, token);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String token) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            if (token != null) {
                http.addRequestProperty("Authorization", token);
            }
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
