package client;
import java.util.Arrays;
import server.Server;
import server.ServerFacade;

public class LoginClient implements client {

    private String visitorName = null;
    private final String serverUrl;
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;

    public LoginClient(String serverUrl) {
        // I don't really do anything with the url
        server = new ServerFacade(serverUrl);
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
        if (params.length == 3) {
            try {
                server.register(params[0], params[1], params[2]);
                state = State.LOGGED_IN;
                return String.format("Successfully registered. User is logged in");
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        }
        throw new Exception("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

}
