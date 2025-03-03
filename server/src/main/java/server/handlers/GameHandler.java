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
                return gson.toJson("success");

            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson("Unable to clear Application");
            }
        };
    }

    public Route listGames() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {
                if (!tokenAuthorize(req)) {
                    res.status(401);
                    return gson.toJson("Unauthorized");
                }

                List<GameData> games = gameService.listGames();

                Map<String, List<GameData>> response = new HashMap<>();
                response.put("games", games);

                res.status(200);
                return gson.toJson(response);

            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson("Unable to list games");
            }
        };
    }

    public Route createGame() {
        return (Request req, Response res) -> {
            res.type("application/json");

            try {

                if (!tokenAuthorize(req)) {
                    res.status(401);
                    return gson.toJson("Unauthorized");
                }

                GameService.createGameRequest createGameRequest = gson.fromJson(req.body(), GameService.createGameRequest.class);
                GameService.createGameResult result = gameService.createGame(createGameRequest);

                res.status(200);
                return gson.toJson(result);

            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson("Unable to create game");
            }
        };
    }

    public Route joinGame() {
        return (Request req, Response res) -> {
            res.type("application/json");
            try {
                if (!tokenAuthorize(req)) {
                    res.status(401);
                    return gson.toJson("unauthorized");
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
                    return gson.toJson("bad request");
                }

                GameService.joinGameRequest joinGameRequest =
                        new GameService.joinGameRequest(token, requestBody.playerColor(), requestBody.gameID());

                gameService.joinGame(joinGameRequest);
                res.status(200);
                return gson.toJson("");

            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson("unable to join game");
            } catch (GameAlreadyTakenException e) {
                res.status(403);
                return gson.toJson("already taken");
            } catch (InvalidColorException e) {
                res.status(500);
                return gson.toJson(e.getMessage());
            }
        };
    }
}
