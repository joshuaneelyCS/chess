package dataaccess.memoryImplementation;

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
    public void joinGame(int id, String playerColor, String username) throws DataAccessException {
        GameData game = getGame(id);
        game.setTeam(playerColor, username);
    }

    @Override
    public void deleteAllGames() {
        games.clear();
    }

}
