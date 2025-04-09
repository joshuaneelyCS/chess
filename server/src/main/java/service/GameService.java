package service;
import chess.ChessGame;
import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
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

    public record CreateGameRequest(String token, String gameName) {}

    public record CreateGameResult(int gameID) {}

    public record JoinGameRequest(String token, String playerColor, int gameID) {};

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public List<GameData> listGames(String token) throws DataAccessException {
        try {
            if (authDAO.getAuth(token) == null) {
                throw new DataAccessException("Error: Unauthorized access - Invalid token");
            };
            return gameDAO.getAllGames(); // Return the list of games if authenticated
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: Could not get games from database");
        }
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        try {
            authDAO.getAuth(req.token);
            int id = createID();
            gameDAO.createGame(new GameData(id, req.gameName, new ChessGame()));
            return new CreateGameResult(id);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: Unauthorized access - Invalid token");
        }
    }

    public void joinGame(JoinGameRequest req) throws DataAccessException, GameAlreadyTakenException, InvalidColorException {
        String username = getUsername(req.token);
        gameDAO.joinGame(req.gameID, req.playerColor, username);
    }

    public boolean validGame(int gameID) throws DataAccessException {
        List<GameData> games = gameDAO.getAllGames();
        for (GameData gameData : games) {
            if (gameData.getGameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    public String getUsername(String token) throws DataAccessException {
        return authDAO.getAuth(token).getUsername();
    }

    public String getPlayerColor(String username, int gameID) throws DataAccessException {
        List<GameData> games = gameDAO.getAllGames();
        if (games != null) {
            for (GameData gameData : games) {
                if (gameData.getGameID() == gameID) {
                    if (username.equals(gameData.getWhiteUsername())) {
                        return "WHITE";
                    } else if (username.equals(gameData.getBlackUsername())) {
                        return "BLACK";
                    } else {
                        return null;
                    }
                }
            }
            return null;
        } else {
            throw new DataAccessException("There were no games in database");
        }
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        List<GameData> games = gameDAO.getAllGames();
        for (GameData gameData : games) {
            if (gameData.getGameID() == gameID) {
                return gameData.getGame();
            }
        }
        return null;
    }

    public void setGame(int gameID, ChessGame game) throws DataAccessException {
        gameDAO.setGame(gameID, game);
    }

    public void clearDatabase() throws DataAccessException {

        gameDAO.deleteAllGames();
        userDAO.deleteAllUsers();
        authDAO.deleteAllAuth();

    }
}
