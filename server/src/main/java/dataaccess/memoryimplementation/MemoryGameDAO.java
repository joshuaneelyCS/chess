package dataaccess.memoryimplementation;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {

    private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public int createGame(GameData game) {
        games.put(game.getGameID(), game);
        return game.getGameID();
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        if (!games.containsKey(id)) {
            throw new DataAccessException("Game with ID " + id + " not found");
        }
        return games.get(id);
    }

    @Override
    public void setGame(int gameID, ChessGame game) throws DataAccessException {
        for (GameData gameData : games.values()) {
            if (gameData.getGameID() == gameID) {
                gameData.setGame(game);
            }
        }
    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game with ID " + gameID + " not found");
        }
        games.remove(gameID);
    }

    @Override
    public void removePlayerFromGame(String playerColor, int gameID) throws DataAccessException {

    }

    @Override
    public void joinGame(int id, String playerColor, String username) throws DataAccessException {
        GameData game = getGame(id);
        // TODO implement check if the right player is joining
        game.setTeam(playerColor, username);
    }

    @Override
    public void deleteAllGames() {
        games.clear();
    }

}
