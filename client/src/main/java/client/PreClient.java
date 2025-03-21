package client;
// import server.Server;
import java.util.Arrays;

public class PreClient implements client {

    private String visitorName = null;
    //private final Server server;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;

    public PreClient(String serverUrl) {
        //server = new Server(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public String help() {
        return """
                Options:
                Login as an existing user: "l", "login" <USERNAME> <PASSWORD> 
                Register a new user: "r", "register" <USERNAME> <PASSWORD> <EMAIL>
                Exit the program: "q", "quit"
                Print this message: "h", "help"
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
            // state = State.SIGNEDIN;
            visitorName = String.join("-", params);
            // ws = new WebSocketFacade(serverUrl, notificationHandler);
            // ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new Exception("Expected: <yourname>");
    }



}
