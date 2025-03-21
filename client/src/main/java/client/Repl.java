package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final LoginClient loginClient;
    // private final PostClient postclient;

    public Repl(String serverUrl) {
        loginClient = new LoginClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Type help to get started. ♕");
        System.out.print(loginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = loginClient.eval(line);
                System.out.println(SET_TEXT_COLOR_BLUE + result);
            } catch (Exception e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
