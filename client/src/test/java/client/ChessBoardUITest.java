package client;
import chess.ChessBoard;
import client.ChessBoardUI;
import org.junit.jupiter.api.Test;

public class ChessBoardUITest {

    @Test
    void test1() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardUI.drawBoard(12345, "WHITE", board);
    }
}
