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
    public record ListGameResponse(GameData[] gameData) { }
    public record CreateGameRequest(String token, String gameName) { }

    public AuthData register(String username, String password, String email) throws Exception {
        var path = "/user";
        var request = new RegisterRequest(username, password, email);
        return this.makeRequest("POST", path, request, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var request = new LoginRequest(username, password);
        return this.makeRequest("POST", path, request, AuthData.class);
    }

    public void clearApplication() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    public void logout() throws Exception {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }

    public GameData[] listGames() throws Exception {
        var path = "/game";
        var response = this.makeRequest("GET", path, null, ListGameResponse.class);
        return response.gameData();
    }

    public void createGame(String token, String GameName) throws Exception {
        var path = "/game";
        var request = new CreateGameRequest(token, GameName);
        this.makeRequest("POST", path, request, null);
    }

    public void joinGame() throws Exception {
        var path = "/game";
        this.makeRequest("PUT", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
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
