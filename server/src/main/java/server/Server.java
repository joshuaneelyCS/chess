package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        UserService userService = new UserService(authDAO, userDAO);
        UserHandler userHandler = new UserHandler(userService);

        GameService gameService = new GameService(authDAO, gameDAO, userDAO);
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
