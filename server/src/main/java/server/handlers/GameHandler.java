package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.GameAlreadyTakenException;
import model.GameData;
import model.InvalidColorException;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameHandler {

    private static final Gson gson = new Gson();
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    private boolean tokenAuthorize(Request req) {
        String token = req.headers("Authorization");
        if (token == null || token.isEmpty()) {
            return false;
        }
        return true;
    }

    public Route clearApplication() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {

                gameService.clearDatabase();

                res.status(200);
                return gson.toJson(Map.of("message", "Application cleared successfully"));

            } catch (DataAccessException e) {
                res.status(401);
                return gson.toJson(Map.of("message", "Unauthorized"));
            }
        };
    }

    public Route listGames() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {

                String token = req.headers("Authorization");

                List<GameData> games = gameService.listGames(token);

                return gson.toJson(Map.of("games", games));

            } catch (DataAccessException e) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unable to list games"));
            }
        };
    }

    public Route createGame() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {
                if (!tokenAuthorize(req)) {
                    res.status(401);
                    return gson.toJson(Map.of("message", "Error: Unauthorized"));
                }

                String token = req.headers("Authorization");

                GameService.CreateGameRequest createGameRequest = gson.fromJson(req.body(), GameService.createGameRequest.class);
                createGameRequest = new GameService.CreateGameRequest(token, createGameRequest.gameName());
                GameService.CreateGameResult result = gameService.createGame(createGameRequest);

                res.status(200);
                return gson.toJson(result);

            } catch (JsonSyntaxException e) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: Bad request - Invalid JSON format"));
            } catch (DataAccessException e) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unauthorized"));
            }
        };
    }

    public Route joinGame() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {
                if (!tokenAuthorize(req)) {
                    res.status(401);
                    return gson.toJson(Map.of("message", "Error: Unauthorized"));
                }

                String token = req.headers("Authorization");

                // Parse JSON request body safely
                GameService.joinGameRequest requestBody;
                try {
                    requestBody = gson.fromJson(req.body(), GameService.joinGameRequest.class);

                    // Validate required fields
                    if (requestBody == null || requestBody.playerColor() == null || requestBody.gameID() == 0) {
                        throw new IllegalArgumentException("Missing required fields");
                    }

                } catch (JsonSyntaxException | IllegalArgumentException e) {
                    res.status(400);
                    return gson.toJson(Map.of("message", "Error: Bad request - Missing or invalid fields"));
                }

                GameService.joinGameRequest joinGameRequest =
                        new GameService.joinGameRequest(token, requestBody.playerColor(), requestBody.gameID());

                gameService.joinGame(joinGameRequest);

                res.status(200);
                return gson.toJson(Map.of("message", "Success: Joined game"));

            } catch (DataAccessException e) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: Unable to join game"));
            } catch (GameAlreadyTakenException e) {
                res.status(403);
                return gson.toJson(Map.of("message", "Error: Game already taken"));
            } catch (InvalidColorException e) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: Invalid color selection"));
            }
        };
    }
}
