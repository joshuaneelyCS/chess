package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class GameHandler {

    private static final Gson gson = new Gson();
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Route clearApplication() {
        return (Request req, Response res) -> {
            res.type("application/json");
            System.out.println("Here");
            try {
                String token = req.headers("Authorization");

                if (token == null || token.isEmpty()) {
                    res.status(401);
                    return gson.toJson("Unauthorized");
                }

                gameService.clearDatabase();
                res.status(200);
                return gson.toJson("success");

            } catch (DataAccessException e) {
                res.status(500);
                return gson.toJson("Unable to clear Application");
            }
        };
    }
}
