package dataaccess.interfaces;
import dataaccess.DataAccessException;
import model.GameData;
import java.util.List;

public interface GameDAO {

    List<GameData> getAllGames() throws DataAccessException;
    int createGame(GameData game) throws DataAccessException;
    GameData getGame(int id) throws DataAccessException;
    void deleteAllGames() throws DataAccessException;
}
