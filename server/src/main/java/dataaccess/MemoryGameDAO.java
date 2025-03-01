package dataaccess;

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
    public GameData getGame(int id) {
        return games.get(id);
    }

    @Override
    public void deleteAllGames() {
        games.clear();
    }

}
