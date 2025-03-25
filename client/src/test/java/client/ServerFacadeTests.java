package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import model.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" +port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void initTest() throws Exception {
        facade.clearApplication();
    }

    @Test
    @Order(1)
    void registerTestSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.getAuthToken().length() > 10);
        System.out.println(authData.getAuthToken());
        System.out.println(authData.getUsername());
    }

    @Test
    @Order(2)
    void registerTestFailure() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.register("player1", "password", "p1@email.com"));
    }

    @Test
    @Order(3)
    void loginTestSuccess() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData authData = facade.login("player1", "password");
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    @Order(4)
    void loginTestFailure() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData authData = facade.login("player1", "password");
        assertThrows(Exception.class, () -> facade.login("player1", "password2"));
    }

    @Test
    @Order(5)
    void logoutTestSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        facade.logout(authData.getAuthToken());
    }

    @Test
    @Order(6)
    void logoutTestFailure() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.logout(null));
    }

    @Test
    @Order(7)
    void createGameSuccess() throws Exception {
        AuthData user = facade.register("player1", "password", "p1@email.com");
        facade.createGame(user.getAuthToken(), "test_game");
    }

    @Test
    @Order(8)
    void createGameFailure() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.createGame(null, "test_game"));
    }

    @Test
    @Order(9)
    void listGameSuccess() throws Exception {
        AuthData user = facade.register("player1", "password", "p1@email.com");
        facade.createGame(user.getAuthToken(), "test_game1");
        facade.createGame(user.getAuthToken(), "test_game2");
        facade.createGame(user.getAuthToken(), "test_game3");
        GameData[] games = facade.listGames(user.getAuthToken());
        assertTrue(games.length == 3);
    }

    @Test
    @Order(10)
    void listGameFailure() {
        assertThrows(Exception.class, () -> facade.listGames(null));
    }

    @Test
    @Order(11)
    void joinGameSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(authData.getAuthToken(), "test_game1");
        assertDoesNotThrow(() -> facade.joinGame(authData.getAuthToken(), "WHITE", gameID));
    }

    @Test
    @Order(12)
    void joinGameFailure() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame(authData.getAuthToken(), "test_game1");
        assertThrows(Exception.class, () -> facade.joinGame(authData.getAuthToken(), "WHITE", 0));
    }

    @Test
    @Order(13)
    void clearApplicationSuccess() throws Exception {
        AuthData user = facade.register("player1", "password", "p1@email.com");
        facade.createGame(user.getAuthToken(), "test_game");

        // Clear the entire application state
        facade.clearApplication();

        // After clearing, login should fail
        assertThrows(Exception.class, () -> facade.login("player1", "password"));

        // And listing games should fail with invalid/old token
        assertThrows(Exception.class, () -> facade.listGames(user.getAuthToken()));
    }
}
