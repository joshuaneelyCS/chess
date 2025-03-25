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
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void initTest() throws Exception {
        String serverUrl = "http://localhost:0";
        facade = new ServerFacade(serverUrl);
        facade.clearApplication();
    }


}
