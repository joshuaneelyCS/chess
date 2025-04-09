package client;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ChessBoardUI;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class ChessBoardUITest {

    @Test
    void printWhiteBoard() {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        board.resetBoard();
        Collection<ChessMove> moves = game.validMoves(new ChessPosition(2, 2));
        ChessBoardUI.drawBoard(12345, "WHITE", board, moves);
    }

    @Test
    void printWhiteBoard2() {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        board.resetBoard();
        Collection<ChessMove> moves = game.validMoves(new ChessPosition(2, 3));
        ChessBoardUI.drawBoard(12345, "WHITE", board, moves);
    }

    @Test
    void printBlackBoard() {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        board.resetBoard();
        Collection<ChessMove> moves = game.validMoves(new ChessPosition(2, 2));
        ChessBoardUI.drawBoard(12345, "BLACK", board, moves);
    }
}
