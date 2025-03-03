package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameAlreadyTakenException;
import model.GameData;
import model.InvalidColorException;

import java.util.List;
import java.util.Random;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    private int createID() {
        Random random = new Random();
        return random.nextInt(1_000_000); // Generates a random number between 0 and 999,999
    }

    public record createGameRequest(String gameName) {}

    public record createGameResult(int gameID) {}

    public record joinGameRequest(String token, String playerColor, int gameID) {};

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public List<GameData> listGames() throws DataAccessException {
        try {
            return gameDAO.getAllGames(); // Return the list of games if authenticated
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: Unauthorized access - Invalid token");
        }
    }

    public createGameResult createGame(createGameRequest req) throws DataAccessException {
        try {
            int id = createID();
            gameDAO.createGame(new GameData(id, req.gameName));
            return new createGameResult(id);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: Unauthorized access - Invalid token");
        }
    }

    public void joinGame(joinGameRequest req) throws DataAccessException, GameAlreadyTakenException, InvalidColorException {

        String username = authDAO.getAuth(req.token).getUsername();
        GameData game = gameDAO.getGame(req.gameID);

        game.setTeam(req.playerColor, username);
    }

    public void clearDatabase() throws DataAccessException {
        try {
            gameDAO.deleteAllGames();
            userDAO.deleteAllUsers();
            authDAO.deleteAllAuth();
        } catch (DataAccessException e) {
            throw new DataAccessException("Could not access and clear data");
        }
    }
}
