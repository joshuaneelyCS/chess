package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final LoginClient loginClient;
    private final MainClient mainClient;
    private final GameClient gameClient;

    public Repl(String serverUrl) {
        loginClient = new LoginClient(serverUrl);
        mainClient = new MainClient(serverUrl);
        gameClient = new GameClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Type help to get started. ♕");
        System.out.print(loginClient.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            if (loginClient.getState() != State.LOGGED_IN) {
                result = runClient(scanner, loginClient, result);
                continue;
            }

            String token = loginClient.getAuthToken();
            if (token == null) {
                System.out.println("Sorry something went wrong generating a token");
                continue;
            }

            mainClient.setToken(token);
            result = handleLoggedIn(scanner, result);

            loginClient.logout(token);
            mainClient.setToken(null);
            System.out.print(loginClient.help());
        }
    }

    private String handleLoggedIn(Scanner scanner, String result) {
        while (!result.equals("logout")) {
            if (mainClient.getState() != State.IN_GAME) {
                result = runClient(scanner, mainClient, result);
                continue;
            }

            result = handleGameSession(scanner, result);
            mainClient.setState(State.OUT_GAME);
            System.out.print(mainClient.help());
        }
        return result;
    }

    private String handleGameSession(Scanner scanner, String result) {
        int gameID = mainClient.getGameID();
        String playerColor = mainClient.getPlayerColor();

        if (gameID == 0) {
            System.out.println("Sorry. Something went wrong joining the game");
            return result;
        }

        gameClient.setGame(gameID, playerColor);

        while (!result.equals("quit") && !result.equals("logout")) {
            result = runClient(scanner, gameClient, result);
        }

        return result;
    }

    private String runClient(Scanner scanner, Client client, String result) {
        printPrompt();
        String line = scanner.nextLine();
        try {
            result = client.eval(line);
            System.out.println(SET_TEXT_COLOR_BLUE + result);
            return result;
        } catch (Exception e) {
            var msg = e.toString();
            System.out.print(msg);
            return null;
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
