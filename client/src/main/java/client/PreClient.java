package client;
import server.Server;
import java.util.Arrays;

public class PreClient implements client {

    private String visitorName = null;
    private final Server server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public PreClient(String serverUrl) {
        server = new Server(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public String help() {
        return """
                register <USERNAME> <PASSWORD> <VISITOR NAME> - to create an account 
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new Exception("Expected: <yourname>");
    }



}
