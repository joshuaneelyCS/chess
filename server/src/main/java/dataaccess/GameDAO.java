package dataaccess;
import model.GameData;
import java.util.List;

public interface GameDAO {

    List<GameData> getAllGames();
    int createGame(GameData game);
    GameData getGame(int id);
    void deleteAllGames();
}
