package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

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

    public static void drawBoard(int gameID, String playerColor, ChessBoard board) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out, playerColor);

        drawChessBoard(out, playerColor, board);

        drawHeaders(out, playerColor);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, String playerColor) {

        setWhite(out);
        String[] headers = { "a", "b", "c", "d", "e", "f", "g", "h" };

        out.print(EMPTY.repeat(1));

        if ("BLACK".equals(playerColor)) {
            for (int boardCol = headers.length - 1; boardCol >= 0; --boardCol) {
                printHeaderText(out, headers[boardCol]);
            }
        } else {
            for (int boardCol = 0; boardCol < headers.length; ++boardCol) {
                printHeaderText(out, headers[boardCol]);
            }
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

    private static void drawChessBoard(PrintStream out, String playerColor, ChessBoard board) {

        String[] rows = "BLACK".equals(playerColor)
                ? new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }
                : new String[] { "8", "7", "6", "5", "4", "3", "2", "1" };

        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; ++i) {
            int boardRow = "WHITE".equals(playerColor)
                    ? BOARD_SIZE_IN_SQUARES - 1 - i  // Go from 7 to 0 for BLACK
                    : i;                             // Go from 0 to 7 for WHITE

            drawRowOfSquares(out, rows[i], board.getBoard()[boardRow], playerColor);
            setWhite(out);
        }
    }

    private static void drawRowOfSquares(PrintStream out, String rowNum, ChessPiece[] row, String playerColor) {
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

        if ("BLACK".equals(playerColor)) {
            for (int squareRow = BOARD_SIZE_IN_SQUARES - 1; squareRow >= 0; --squareRow) {
                tileColor = drawSquare(out, row, tileColor, squareRow);
            }
        } else {
            for (int squareRow = 0; squareRow < BOARD_SIZE_IN_SQUARES; ++squareRow) {
                tileColor = drawSquare(out, row, tileColor, squareRow);
            }
        }

        out.print(" ");
        out.print(rowNum);
        out.print(" \n");
    }

    private static String drawSquare(PrintStream out, ChessPiece[] row, String tileColor, int squareRow) {
        out.print(tileColor);

        ChessPiece piece = row[squareRow];
        char pieceSymbol = getPieceSymbol(piece, out);

        out.print(" ");
        out.print(pieceSymbol);
        out.print(" ");

        tileColor = toggleTileColor(tileColor);

        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_WHITE);
        return tileColor;
    }

    private static char getPieceSymbol(ChessPiece piece, PrintStream out) {
        if (piece == null) return ' ';

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            out.print(SET_TEXT_COLOR_RED);
        } else {
            out.print(SET_TEXT_COLOR_BLUE);
        }

        return switch (piece.getPieceType()) {
            case ROOK -> 'R';
            case KNIGHT -> 'K';
            case BISHOP -> 'B';
            case QUEEN -> 'Q';
            case KING -> 'K';
            case PAWN -> 'P';
        };
    }

    private static String toggleTileColor(String current) {
        return current.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
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
