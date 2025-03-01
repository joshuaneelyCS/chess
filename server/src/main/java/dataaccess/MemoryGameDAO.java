package dataaccess;

import model.GameData;

import java.util.List;

public class MemoryGameDAO implements GameDAO {

    @Override
    public List<GameData> getAllGames() {
        return null;
    }

    @Override
    public int createGame(GameData game) {
        return 0;
    }

    @Override
    public GameData getGame(int id) {
        return null;
    }

    @Override
    public void deleteAllGames() {

    }

}
