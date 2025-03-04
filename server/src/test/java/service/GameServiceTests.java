package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.GameAlreadyTakenException;
import model.InvalidColorException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private GameService gameService;
    private UserService userService;
    private MemoryAuthDAO memoryAuthDAO;
    private MemoryUserDAO memoryUserDAO;
    private MemoryGameDAO memoryGameDAO;

    @BeforeEach
    public void setup() {
        memoryAuthDAO = new MemoryAuthDAO();
        memoryGameDAO = new MemoryGameDAO();
        memoryUserDAO = new MemoryUserDAO();
        userService = new UserService(memoryAuthDAO, memoryUserDAO);
        gameService = new GameService(memoryAuthDAO, memoryGameDAO, memoryUserDAO);
    }

    // Positive

    @Test
    @DisplayName("Successfully Lists Games")
    public void listGamesSuccess() throws DataAccessException, IncorrectPasswordException {
        // Register a user and get their token
        UserService.RegisterRequest request = new UserService.RegisterRequest("user1", "password123", "user1@email.com");
        UserService.RegisterResult result = userService.register(request);
        String token = result.authToken();

        // Check that initially there are no games
        List<GameData> games = gameService.listGames(token);
        assertTrue(games.isEmpty(), "Game list should be empty initially");

        // Create two games
        GameService.createGameRequest request1 = new GameService.createGameRequest(token, "game1");
        GameService.createGameRequest request2 = new GameService.createGameRequest(token, "game2");

        gameService.createGame(request1);
        gameService.createGame(request2);

        // Fetch the updated list of games
        games = gameService.listGames(token);

        assertEquals(2, games.size(), "There should be exactly 2 games in the list");
    }

    @Test
    @DisplayName("Create Game Successfully")
    public void createGameSuccess() throws DataAccessException, IncorrectPasswordException {
        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("user2", "password456", "user2@email.com");
        UserService.RegisterResult result = userService.register(request);
        String token = result.authToken();

        // Create a game
        GameService.createGameRequest createRequest = new GameService.createGameRequest(token, "MyChessGame");
        GameService.createGameResult gameResult = gameService.createGame(createRequest);

        assertNotNull(gameResult, "Game creation result should not be null");
        assertTrue(gameResult.gameID() > 0, "Game ID should be a positive integer");

        // Verify that the game exists
        List<GameData> games = gameService.listGames(token);
        assertEquals(1, games.size(), "There should be one game in the list");
        assertEquals("MyChessGame", games.get(0).getGameName(), "Game name should match the request");
    }

    @Test
    @DisplayName("Join Game Successfully")
    public void joinGameSuccess() throws DataAccessException, IncorrectPasswordException, GameAlreadyTakenException, InvalidColorException {
        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("player1", "password789", "player1@email.com");
        UserService.RegisterResult result = userService.register(request);
        String token = result.authToken();

        // Create a game
        GameService.createGameRequest createRequest = new GameService.createGameRequest(token, "JoinableGame");
        GameService.createGameResult gameResult = gameService.createGame(createRequest);
        int gameID = gameResult.gameID();

        // Join as white player
        GameService.joinGameRequest joinRequest = new GameService.joinGameRequest(token, "WHITE", gameID);
        gameService.joinGame(joinRequest);

        // Verify that the user is set as the white player
        GameData game = memoryGameDAO.getGame(gameID);
        assertEquals("player1", game.getWhiteUsername(), "White player should be set correctly");
    }

    @Test
    @DisplayName("Successfully Clears Database")
    public void clearDatabaseSuccess() throws DataAccessException {
        gameService.clearDatabase();

        // Verify that all data has been removed
        assertTrue(memoryGameDAO.getAllGames().isEmpty(), "Games should be cleared");
        assertTrue(memoryUserDAO.getAllUsers().isEmpty(), "Users should be cleared");
        assertTrue(memoryAuthDAO.getAllAuth().isEmpty(), "Auth data should be cleared");
    }

    // Negative

    @Test
    @DisplayName("List Games with Invalid Token (Fail)")
    public void listGamesInvalidToken() {
        assertThrows(DataAccessException.class, () -> {
            gameService.listGames("invalid-token");
        }, "Should throw DataAccessException for unauthorized access");
    }

    @Test
    @DisplayName("Create Game with Invalid Token (Fail)")
    public void createGameInvalidToken() {
        GameService.createGameRequest createRequest = new GameService.createGameRequest("invalid-token", "InvalidGame");

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(createRequest);
        }, "Should throw DataAccessException for unauthorized game creation");
    }

    @Test
    @DisplayName("Join Game with Invalid Token (Fail)")
    public void joinGameInvalidToken() {
        GameService.joinGameRequest joinRequest = new GameService.joinGameRequest("invalid-token", "white", 12345);

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(joinRequest);
        }, "Should throw DataAccessException for joining with an invalid token");
    }

    @Test
    @DisplayName("Join Non-Existent Game (Fail)")
    public void joinNonExistentGame() throws DataAccessException, IncorrectPasswordException {
        // Register a user
        UserService.RegisterRequest request = new UserService.RegisterRequest("player2", "password789", "player2@email.com");
        UserService.RegisterResult result = userService.register(request);
        String token = result.authToken();

        // Try to join a game that doesn't exist
        GameService.joinGameRequest joinRequest = new GameService.joinGameRequest(token, "black", 99999);

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(joinRequest);
        }, "Should throw DataAccessException for joining a non-existent game");
    }
}
