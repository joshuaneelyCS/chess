package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final LoginClient loginClient;
    private final MainClient mainClient;
    private final GameClient gameClient;

    public Repl(String serverUrl)  {
        loginClient = new LoginClient(serverUrl);
        mainClient = new MainClient(serverUrl);
        gameClient = new GameClient(serverUrl, this);
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
            result = handleLoggedIn(scanner, result, token);

            loginClient.logout(token);
            mainClient.setToken(null);
            System.out.print(loginClient.help());
        }
    }

    private String handleLoggedIn(Scanner scanner, String result, String token) {
        while (!result.equals("logout")) {
            if (mainClient.getState() != State.IN_GAME) {
                result = runClient(scanner, mainClient, result);
                continue;
            }

            result = handleGameSession(scanner, result, token);
            mainClient.setState(State.OUT_GAME);
            System.out.print(mainClient.help());
        }
        return result;
    }

    private String handleGameSession(Scanner scanner, String result, String token) {
        int gameID = mainClient.getGameID();
        String playerColor = mainClient.getPlayerColor();

        if (gameID == 0) {
            System.out.println("Sorry. Game ID not found");
            return result;
        }

        gameClient.setGameInfo(gameID, playerColor);
        gameClient.setToken(token);
        gameClient.setIsObserver(mainClient.getIsObserver());

        try {
            gameClient.beginSession();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            result = "You left the game";
        }

        while (!result.equals("You left the game\n")) {
            result = runClient(scanner, gameClient, result);
        }

        return result;
    }

    private String runClient(Scanner scanner, Client client, String result) {
        if (!(client instanceof GameClient)) {
            printPrompt();
        }
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

    @Override
    public void notify(ServerMessage message) {
        try {
            switch (message.getServerMessageType()) {
                case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
                case ERROR -> displayError(((ErrorMessage) message).getMessage());
                case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loadGame(ChessGame game) {
        System.out.print("");
        gameClient.loadLocalGame(game);
        printPrompt();
    }

    private void displayError(String message) {
    }

    private void displayNotification(String message) {
        System.out.println(message);
        printPrompt();
    }
}
