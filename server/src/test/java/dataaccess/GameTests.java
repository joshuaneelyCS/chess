package dataaccess;

import dataaccess.databaseimplementation.DatabaseGameDAO;
import model.GameAlreadyTakenException;
import model.GameData;
import model.InvalidColorException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameTests {

    private DatabaseGameDAO gameDAO;

    @BeforeAll
    public void setUp() throws DataAccessException {
        gameDAO = new DatabaseGameDAO();
        gameDAO.deleteAllGames(); // Clear database before tests
    }

    @Test
    @Order(1)
    public void testCreateGameSuccess() throws DataAccessException {
        GameData game = new GameData(1, "New Game", null);
        gameDAO.createGame(game);

        GameData retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame);
        assertEquals("New Game", retrievedGame.getGameName());
    }

    @Test
    @Order(2)
    public void testJoinGameSuccess() throws DataAccessException {
        gameDAO.joinGame(1, "WHITE", "player1");
        GameData game = gameDAO.getGame(1);
        assertEquals("player1", game.getWhiteUsername());
    }

    @Test
    @Order(3)
    public void testJoinGameFailureSpotAlreadyTaken() throws DataAccessException {
        assertThrows(GameAlreadyTakenException.class, () -> gameDAO.joinGame(1, "WHITE", "player2"));
    }

    @Test
    @Order(4)
    public void testJoinGameFailureInvalidColor() {
        assertThrows(InvalidColorException.class, () -> gameDAO.joinGame(1, "GREEN", "player3"));
    }

    @Test
    @Order(5)
    public void testGetGameSuccess() throws DataAccessException {
        GameData game = gameDAO.getGame(1);
        assertNotNull(game);
    }

    @Test
    @Order(6)
    public void testGetGameFailureNonExistent() throws DataAccessException {
        assertNull(gameDAO.getGame(99));
    }

    @Test
    @Order(7)
    public void testGetAllGamesSuccess() throws DataAccessException {
        List<GameData> games = gameDAO.getAllGames();
        assertFalse(games.isEmpty());
    }

    @Test
    @Order(8)
    public void testDeleteAllGamesSuccess() throws DataAccessException {
        gameDAO.deleteAllGames();
        List<GameData> games = gameDAO.getAllGames();
        assertTrue(games.isEmpty());
    }
}
