package server;

import dataaccess.DataAccessException;
import dataaccess.databaseImplimentation.DatabaseDAO;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.DAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memoryImplimentation.MemoryAuthDAO;
import dataaccess.memoryImplimentation.MemoryDAO;
import dataaccess.memoryImplimentation.MemoryUserDAO;
import dataaccess.memoryImplimentation.MemoryGameDAO;
import server.handlers.GameHandler;
import server.handlers.UserHandler;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) throws DataAccessException {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // One line of code to switch from memory to database
        DAO dao = new DatabaseDAO();

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
