package client;

import server.ServerFacade;

public class MainClient {

    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.OUT_GAME;

    public MainClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }


}
