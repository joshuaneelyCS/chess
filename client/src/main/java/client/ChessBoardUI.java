package client;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import static ui.EscapeSequences.*;

public class ChessBoardUI {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";

    private static Random rand = new Random();

    public static void drawBoard(int gameID, String playerColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out, playerColor);

        drawChessBoard(out, playerColor);

        drawHeaders(out, playerColor);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, String playerColor) {

        setWhite(out);
        String[] headers = { "a", "b", "c", "d", "e", "f", "g", "h" };

        out.print(EMPTY.repeat(1));
        for (int boardCol = 0; boardCol < 8; ++boardCol) {
            printHeaderText(out, headers[boardCol]);
        }
        out.print(EMPTY.repeat(1));

        out.println();
    }

    private static void printHeaderText(PrintStream out, String column) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(" ");
        out.print(column);
        out.print(" ");

        setWhite(out);
    }

    private static void drawChessBoard(PrintStream out, String playerColor) {

        String[] column = { "8", "7", "6", "5", "4", "3", "2", "1" };

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, column[boardRow], playerColor);
            setWhite(out);
        }
    }

    private static void drawRowOfSquares(PrintStream out, String rowNum, String playerColor) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(" ");
        out.print(rowNum);
        out.print(" ");

        int startingColorIndex;
        if (playerColor.equals("WHITE")) {
            startingColorIndex = 0;
        } else {
            startingColorIndex = 1;
        }

        String tileColor;
        if (Integer.parseInt(rowNum) % 2 == startingColorIndex) {
            tileColor = SET_BG_COLOR_WHITE;
        } else {
            tileColor = SET_BG_COLOR_BLACK;
        }

        for (int squareRow = 0; squareRow < BOARD_SIZE_IN_SQUARES; ++squareRow) {
            out.print(tileColor);

            out.print(" ");
            out.print(" ");
            out.print(" ");

            if (tileColor.equals(SET_BG_COLOR_WHITE)) {
                tileColor = SET_BG_COLOR_BLACK;
            } else {
                tileColor = SET_BG_COLOR_WHITE;
            }

            out.print(SET_BG_COLOR_WHITE);
        }

        out.print(" ");
        out.print(rowNum);
        out.print(" \n");


    }

    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}
