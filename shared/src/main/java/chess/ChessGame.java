package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "ChessGame{" +
                "team_turn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    private void changeTeamTurn() {
        if (teamTurn == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Ensure there is a piece at the given position
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        // Iterate over each move and test if it prevents check
        for (ChessMove move : moves) {
            ChessGame gameCopy = this.clone(); // Deep copy game state
            gameCopy.testMove(move, gameCopy.getBoard());

            // Ensure king is not left in check after this move
            if (!gameCopy.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        // I need to check if king is in a castle space
        // If so make sure castling will not lead him through or into check

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

        // Get the piece that is trying to move
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("Piece is null!");
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn!");
        }


        var validMovesList = validMoves(move.getStartPosition());

        // Checks to see if the move is valid
        if (!validMovesList.contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }

        // If it is a pawn promotion
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        // This piece can no longer castle
        // piece.setCastle(false);

        // Add that piece to where it wants to go
        board.addPiece(move.getEndPosition(), piece);

        // remove the piece from its old square
        board.removePiece(move.getStartPosition());

        changeTeamTurn();
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

                if (extracted(teamColor, piece, r, c)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean extracted(TeamColor teamColor, ChessPiece piece, int r, int c) {
        Collection<ChessMove> moves;
        // gets the moves of all the pieces that are not on the same team
        if (piece != null && piece.getTeamColor() != teamColor) {
            // Gets the moves of opposing piece
            moves = piece.pieceMoves(board, new ChessPosition(r, c));
            // For each move, can it attack the king?
            for (ChessMove move : moves) {
                ChessPosition landingSquare = move.getEndPosition();
                ChessPiece opposingPiece = board.getPiece(landingSquare);
                if (opposingPiece != null && opposingPiece.getTeamColor() == teamColor &&
                        opposingPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    // if a move can attack the king, they are in check
                    return true;
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
        if(isInCheck(teamColor)) {
            return checkTeamMoves(teamColor);
        }
        return false;
    }

    private boolean checkTeamMoves(TeamColor teamColor) {
        ChessPiece piece;
        Collection<ChessMove> moves;
        // He's in check, are there any valid moves?
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                // Check the valid moves it has
                moves = validMoves(new ChessPosition(r,c));
                piece = board.getPiece(new ChessPosition(r,c));
                // If there is a piece, and it has valid moves, and it is on the same team,
                // Then he is not in checkmate
                if (piece != null) {
                    if ((moves.size() != 0) && piece.getTeamColor() == teamColor) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            return checkTeamMoves(teamColor);
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @paramboard the new board to use
     */
    public void setBoard(ChessBoard myBoard) {
        this.board = myBoard;
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
