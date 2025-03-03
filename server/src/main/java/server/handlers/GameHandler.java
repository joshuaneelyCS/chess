package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

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
}
