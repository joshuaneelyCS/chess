package server;

import dataaccess.DataAccessException;
import dataaccess.databaseImplimentation.databaseDAO;
import dataaccess.interfaces.DAO;
import dataaccess.memoryImplimentation.MemoryDAO;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private DAO dao;

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // One line of code to switch from memory to database
        try {
            dao = new databaseDAO();
        } catch (DataAccessException e) {
            dao = new MemoryDAO();
        }

        UserService userService = new UserService(dao.getAuthDAO(), dao.getUserDAO());
        UserHandler userHandler = new UserHandler(userService);

        GameService gameService = new GameService(dao.getAuthDAO(), dao.getGameDAO(), dao.getUserDAO());
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        // Clears the application
        // Spark.delete("/db", (request, response) -> true);
        Spark.post("/user", userHandler.createUser());
        Spark.post("/session", userHandler.login());
        Spark.delete("/session", userHandler.logout());
        Spark.delete("/db", gameHandler.clearApplication());
        Spark.get("/game", gameHandler.listGames());
        Spark.post("/game", gameHandler.createGame());
        Spark.put("/game", gameHandler.joinGame());

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public static void main(String[] args) {

    }

    private static Object handleHello(Request req, Response res) {
        return "Hello BYU!";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
