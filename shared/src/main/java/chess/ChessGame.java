package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable{

    @Override
    protected ChessGame clone() {
        try {
            ChessGame clonedGame = (ChessGame) super.clone();
            clonedGame.board = board.clone(); // Ensure deep copy of the board
            return clonedGame;
        } catch (CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }

    private TeamColor team_turn;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        this.team_turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team_turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        team_turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        Collection<ChessMove> validMoves = new ArrayList<>();
        // Copy's the board and gets piece and it's possible moves
        ChessGame gameCopy;
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        // Only allow moves that will get someone out of check
        for (ChessMove move : moves) {
            gameCopy = (ChessGame) clone();
            testMove(move, gameCopy.getBoard());
            if (!gameCopy.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    public ChessBoard testMove(ChessMove move, ChessBoard chessBoard) {

        // Get the piece that is trying to move
        ChessPiece piece = chessBoard.getPiece(move.getStartPosition());

        // Add that piece to where it wants to go
        chessBoard.addPiece(move.getEndPosition(), piece);

        // remove the piece from its old square
        chessBoard.removePiece(move.getStartPosition());

        return chessBoard;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        var validMovesList = validMoves(move.getStartPosition());

        // Checks to see if the move is valid
        if (!validMovesList.contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }

        // Get the piece that is trying to move
        ChessPiece piece = board.getPiece(move.getStartPosition());

        // If it is a pawn promotion
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        // Add that piece to where it wants to go
        board.addPiece(move.getEndPosition(), piece);

        // remove the piece from its old square
        board.removePiece(move.getStartPosition());
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        // Go through each square on the board and see if any of the moves can take the king
        ChessPiece piece;
        Collection<ChessMove> moves;

        for (int r = 1; r <= 8; r++){
            for (int c = 1; c <= 8; c++){
                piece = board.getPiece(new ChessPosition(r,c));

                // gets the moves of all the pieces that are not on the same team
                if (piece != null && piece.getTeamColor() != teamColor) {
                    moves = piece.pieceMoves(board, new ChessPosition(r, c));
                    for (ChessMove move : moves) {
                        ChessPosition landingSquare = move.getEndPosition();
                        ChessPiece OpposingPiece = board.getPiece(landingSquare);
                        if (OpposingPiece != null && OpposingPiece.getTeamColor() == teamColor) {
                            if (OpposingPiece.getPieceType() == ChessPiece.PieceType.KING ) {
                                return true;
                            }
                        }
                    }

                }


            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
