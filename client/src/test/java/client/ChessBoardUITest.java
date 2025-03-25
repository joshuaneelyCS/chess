package client;
import chess.ChessBoard;
import client.ChessBoardUI;
import org.junit.jupiter.api.Test;

public class ChessBoardUITest {

    @Test
    void printWhiteBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(12345, "WHITE", board);
    }

    @Test
    void printBlackBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(12345, "BLACK", board);
    }

    @Test
    void printWhiteBoard2() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(12345, "WHITE", board);
    }

    @Test
    void printBlackBoard2() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(12345, "BLACK", board);
    }
}
