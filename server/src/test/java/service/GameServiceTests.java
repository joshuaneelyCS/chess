package service;

import dataaccess.DataAccessException;
import dataaccess.databaseimplementation.DatabaseDAO;
import dataaccess.interfaces.DAO;
import model.GameData;
import model.GameAlreadyTakenException;
import model.InvalidColorException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private GameService testgameService;
    private UserService testuserService;
    private DAO testdao;

    @BeforeEach
    public void setup() throws DataAccessException {
        testdao = new DatabaseDAO();
        testuserService = new UserService(testdao.getAuthDAO(), testdao.getUserDAO());
        testgameService = new GameService(testdao.getAuthDAO(), testdao.getGameDAO(), testdao.getUserDAO());
        testgameService.clearDatabase();
    }

    // Positive

    @Test
    @DisplayName("Successfully Lists Games")
    public void listGamesSuccess() throws DataAccessException, IncorrectPasswordException {
        // Register a user and get their token
        String token;

        UserService.RegisterRequest request = new UserService.RegisterRequest("user1", "password123", "user1@email.com");
        UserService.RegisterResult result = testuserService.register(request);
        token = result.authToken();

        // Check that initially there are no games
        List<GameData> games = testgameService.listGames(token);
        assertTrue(games.isEmpty(), "Game list should be empty initially");

        // Create two games
        GameService.CreateGameRequest request1 = new GameService.CreateGameRequest(token, "game1");
        GameService.CreateGameRequest request2 = new GameService.CreateGameRequest(token, "game2");

        testgameService.createGame(request1);
        testgameService.createGame(request2);

        // Fetch the updated list of games
        games = testgameService.listGames(token);

        assertEquals(2, games.size(), "There should be exactly 2 games in the list");
    }

    @Test
    @DisplayName("Create Game Successfully")
    public void createGameSuccess() throws DataAccessException, IncorrectPasswordException {

        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("user2", "password456", "user2@email.com");
        UserService.RegisterResult result = testuserService.register(request);
        String token = result.authToken();

        // Create a game
        GameService.CreateGameRequest createRequest = new GameService.CreateGameRequest(token, "MyChessGame");
        GameService.CreateGameResult gameResult = testgameService.createGame(createRequest);

        assertNotNull(gameResult, "Game creation result should not be null");
        assertTrue(gameResult.gameID() > 0, "Game ID should be a positive integer");

        // Verify that the game exists
        List<GameData> games = testgameService.listGames(token);
        assertEquals(1, games.size(), "There should be one game in the list");
        assertEquals("MyChessGame", games.get(0).getGameName(), "Game name should match the request");
    }

    @Test
    @DisplayName("Join Game Successfully")
    public void joinGameSuccess() throws DataAccessException, IncorrectPasswordException, GameAlreadyTakenException, InvalidColorException {
        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("player1", "password789", "player1@email.com");
        UserService.RegisterResult result = testuserService.register(request);
        String token = result.authToken();

        // Create a game
        GameService.CreateGameRequest createRequest = new GameService.CreateGameRequest(token, "JoinableGame");
        GameService.CreateGameResult gameResult = testgameService.createGame(createRequest);
        int gameID = gameResult.gameID();

        // Join as white player
        GameService.JoinGameRequest joinRequest = new GameService.JoinGameRequest(token, "WHITE", gameID);
        testgameService.joinGame(joinRequest);

        // Verify that the user is set as the white player
        GameData game = testdao.getGameDAO().getGame(gameID);
        assertEquals("player1", game.getWhiteUsername(), "White player should be set correctly");
    }

    @Test
    @DisplayName("Successfully Clears Database")
    public void clearDatabaseSuccess() throws DataAccessException {
        testgameService.clearDatabase();

        // Verify that all data has been removed
        assertTrue(testdao.getGameDAO().getAllGames().isEmpty(), "Games should be cleared");
        assertTrue(testdao.getUserDAO().getAllUsers().isEmpty(), "Users should be cleared");
        assertTrue(testdao.getAuthDAO().getAllAuth().isEmpty(), "Auth data should be cleared");
    }

    // Negative

    @Test
    @DisplayName("List Games with Invalid Token (Fail)")
    public void listGamesInvalidToken() {
        assertThrows(DataAccessException.class, () -> {
            testgameService.listGames("invalid-token");
        }, "Should throw DataAccessException for unauthorized access");
    }

    @Test
    @DisplayName("Create Game with Invalid Token (Fail)")
    public void createGameInvalidToken() {
        GameService.CreateGameRequest createRequest = new GameService.CreateGameRequest("invalid-token", "InvalidGame");

        assertThrows(DataAccessException.class, () -> {
            testgameService.createGame(createRequest);
        }, "Should throw DataAccessException for unauthorized game creation");
    }

    @Test
    @DisplayName("Join Game with Invalid Token (Fail)")
    public void joinGameInvalidToken() {
        GameService.JoinGameRequest joinRequest = new GameService.JoinGameRequest("invalid-token", "white", 12345);

        assertThrows(DataAccessException.class, () -> {
            testgameService.joinGame(joinRequest);
        }, "Should throw DataAccessException for joining with an invalid token");
    }

    @Test
    @DisplayName("Join Non-Existent Game (Fail)")
    public void joinNonExistentGame() throws DataAccessException, IncorrectPasswordException {
        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("player2", "password789", "player2@email.com");
        UserService.RegisterResult result = testuserService.register(request);
        String token = result.authToken();

        // Try to join a game that doesn't exist
        GameService.JoinGameRequest joinRequest = new GameService.JoinGameRequest(token, "BLACK", 99999);

        assertThrows(DataAccessException.class, () -> {
            testgameService.joinGame(joinRequest);
        }, "Should throw DataAccessException for joining a non-existent game");
    }
}
