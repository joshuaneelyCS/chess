package passoff.chess.game;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardUpdateTest {
    @Test
    @DisplayName("Full Game Checkmate")
    public void scholarsMate() throws InvalidMoveException {
        var game = new ChessGame();
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        /*
                |r|n|b|q|k|b|n|r|
                |p|p|p|p|p|p|p|p|
                | | | | | | | | |
                | | | | | | | | |
                | | | | |P| | | |
                | | | | | | | | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K|B|N|R|
         */
        game.makeMove(new ChessMove(new ChessPosition(7, 5), new ChessPosition(5, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));

        /*
                |r|n|b|q|k|b|n|r|
                |p|p|p|p| |p|p|p|
                | | | | | | | | |
                | | | | |p| | | |
                | | | | |P| | | |
                | | | | | | | | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K|B|N|R|
         */
        // Try to get the pawn on (2,5)
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        game.makeMove(new ChessMove(new ChessPosition(1, 6), new ChessPosition(4, 3), null));
        /*
                |r|n|b|q|k|b|n|r|
                |p|p|p|p| |p|p|p|
                | | | | | | | | |
                | | | | |p| | | |
                | | |B| |P| | | |
                | | | | | | | | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K| |N|R|
         */
        game.makeMove(new ChessMove(new ChessPosition(8, 7), new ChessPosition(6, 6), null));
        /*
                |r|n|b|q|k|b| |r|
                |p|p|p|p| |p|p|p|
                | | | | | |n| | |
                | | | | |p| | | |
                | | |B| |P| | | |
                | | | | | | | | |
                |P|P|P|P| |P|P|P|
                |R|N|B|Q|K| |N|R|
         */
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
        // game.makeMove(new ChessMove(new ChessPosition(1, 4), new ChessPosition(5, 8), null));
        /*
                |r|n|b|q|k|b| |r|
                |p|p|p|p| |p|p|p|
                | | | | | |n| | |
                | | | | |p| | |Q|
                | | |B| |P| | | |
                | | | | | | | | |
                |P|P|P|P| |P|P|P|
                |R|N|B| |K| |N|R|
         */
    }
}