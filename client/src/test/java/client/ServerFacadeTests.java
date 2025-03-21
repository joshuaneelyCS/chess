package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import model.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void initTest() throws Exception {
        String serverUrl = "http://localhost:8080";
        facade = new ServerFacade(serverUrl);
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
    void LoginTestSuccess() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData authData = facade.login("player1", "password");
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    @Order(4)
    void loginTestFailure() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.register("player1", "password", "p1@email.com"));
    }


}
