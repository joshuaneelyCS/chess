package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final LoginClient loginClient;
    private final MainClient mainClient;

    public Repl(String serverUrl) {
        loginClient = new LoginClient(serverUrl);
        mainClient = new MainClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Type help to get started. ♕");
        System.out.print(loginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            if (loginClient.getState() == State.LOGGED_IN) {

                var token = loginClient.getAuthToken();

                if (token != null) {
                    mainClient.setToken(token);

                    while (!result.equals("logout")) {
                        result = runClient(scanner, mainClient, result);
                    }

                } else {
                    System.out.println("Sorry something went wrong generating a token");
                }

                mainClient.setToken(null);
                loginClient.setState(State.LOGGED_OUT);
            } else {
                result = runClient(scanner, loginClient, result);
            }
        }
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
