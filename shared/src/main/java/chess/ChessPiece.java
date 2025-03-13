package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    @Override
    protected ChessPiece clone() {
    try {
        return (ChessPiece) super.clone();
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.BISHOP) {
            return getBishopMoves(board, myPosition);
        } else if (type == PieceType.KING) {
            return getKingMoves(board, myPosition);
        } else if (type == PieceType.KNIGHT) {
            return getKnightMoves(board, myPosition);
        } else if (type == PieceType.PAWN) {
            return getPawnMoves(board, myPosition);
        } else if (type == PieceType.QUEEN) {
            return getQueenMoves(board, myPosition);
        } else if (type == PieceType.ROOK) {
            return getRookMoves(board, myPosition);
        }

        return null;
    }

    private boolean outOfBounds(ChessPosition position) {
        return position.getRow() < 1 || position.getRow() > 8 || position.getColumn() < 1 || position.getColumn() > 8;
    }

    private ArrayList<ChessMove> addPromotions(ChessPosition startPosition, ChessPosition endPosition, ArrayList<ChessMove> listOfMoves) {
        listOfMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        listOfMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        listOfMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        listOfMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        return listOfMoves;
    }

    private ArrayList<ChessMove> getBishopMoves(ChessBoard board, ChessPosition initPosition) {

        var listOfMoves = new ArrayList<ChessMove>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return getChessMoves2(board, initPosition, listOfMoves, directions);
    }

    private ArrayList<ChessMove> getKingMoves(ChessBoard board, ChessPosition initPosition) {
        var listOfMoves = new ArrayList<ChessMove>();
        var directions = new int[][]{{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        return getChessMoves1(board, initPosition, listOfMoves, directions);
    }

    private ArrayList<ChessMove> getChessMoves1(ChessBoard board, ChessPosition initPosition, ArrayList<ChessMove> listOfMoves, int[][] directions) {
        var currPosition = initPosition;

        for (int i = 0; i < directions.length; i++) {
            currPosition = new ChessPosition(currPosition.getRow() + directions[i][0], currPosition.getColumn() + directions[i][1]);

            if (!outOfBounds(currPosition)) {
                if (board.getPiece(currPosition) != null) {
                    if (board.getPiece(currPosition).getTeamColor() != pieceColor) {
                        listOfMoves.add(new ChessMove(initPosition, currPosition, null));
                    }
                } else {
                    listOfMoves.add(new ChessMove(initPosition, currPosition, null));
                }
            }
            currPosition = initPosition;
        }
        return listOfMoves;
    }

    private ArrayList<ChessMove> getChessMoves2(ChessBoard board, ChessPosition initPosition, ArrayList<ChessMove> listOfMoves, int[][] directions) {
        var currPosition = initPosition;

        for (int i = 0; i < directions.length; i++) {
            while (true) {
                // set the current position to the next possible position
                currPosition = new ChessPosition(currPosition.getRow() + directions[i][0], currPosition.getColumn() + directions[i][1]);

                // if the position is out of bounds, break the loop
                if (outOfBounds(currPosition)) {
                    break;
                }
                if (board.getPiece(currPosition) != null) {
                    // if piece can be taken
                    if (board.getPiece(currPosition).getTeamColor() != pieceColor) {
                        listOfMoves.add(new ChessMove(initPosition, currPosition, null));
                    }
                    break;
                }

                listOfMoves.add(new ChessMove(initPosition, currPosition, null));
            }
            currPosition = initPosition;
        }

        return listOfMoves;
    }

    private ArrayList<ChessMove> getKnightMoves(ChessBoard board, ChessPosition initPosition) {
        var listOfMoves = new ArrayList<ChessMove>();
        var directions = new int[][]{{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
        return getChessMoves1(board, initPosition, listOfMoves, directions);
    }

    private ArrayList<ChessMove> getPawnMoves(ChessBoard board, ChessPosition initPosition) {
        var listOfMoves = new ArrayList<ChessMove>();
        int direction = (pieceColor == ChessGame.TeamColor.BLACK) ? -1 : 1;
        int startingRow = (pieceColor == ChessGame.TeamColor.BLACK) ? 7 : 2;
        int promotionRow = (pieceColor == ChessGame.TeamColor.BLACK) ? 1 : 8;

        // Move one square forward if not occupied
        ChessPosition oneStep = new ChessPosition(initPosition.getRow() + direction, initPosition.getColumn());
        if (!outOfBounds(oneStep) && board.getPiece(oneStep) == null) {
            if (oneStep.getRow() == promotionRow) {
                // If reaching the last rank, add all promotion options
                addPromotions(initPosition, oneStep, listOfMoves);
            } else {
                listOfMoves.add(new ChessMove(initPosition, oneStep, null));
            }

            // Move two squares forward from the starting position if the path is clear
            ChessPosition twoSteps = new ChessPosition(initPosition.getRow() + 2 * direction, initPosition.getColumn());
            if (initPosition.getRow() == startingRow && board.getPiece(twoSteps) == null && board.getPiece(oneStep) == null) {
                listOfMoves.add(new ChessMove(initPosition, twoSteps, null));
            }
        }

        // Diagonal captures (only if capturing an opponent piece)
        for (int dx : new int[]{-1, 1}) {
            ChessPosition diagonal = new ChessPosition(initPosition.getRow() + direction, initPosition.getColumn() + dx);
            if (!outOfBounds(diagonal) && board.getPiece(diagonal) != null &&
                    board.getPiece(diagonal).getTeamColor() != pieceColor) {

                if (diagonal.getRow() == promotionRow) {
                    // If capturing on the last rank, add promotion options
                    addPromotions(initPosition, diagonal, listOfMoves);
                } else {
                    listOfMoves.add(new ChessMove(initPosition, diagonal, null));
                }
            }
        }

        return listOfMoves;
    }

    private ArrayList<ChessMove> getQueenMoves(ChessBoard board, ChessPosition initPosition) {
        var listOfMoves = new ArrayList<ChessMove>();
        var directions = new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        return getChessMoves2(board, initPosition, listOfMoves, directions);
    }

    private ArrayList<ChessMove> getRookMoves(ChessBoard board, ChessPosition initPosition) {
        var listOfMoves = new ArrayList<ChessMove>();
        var directions = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
        return getChessMoves2(board, initPosition, listOfMoves, directions);
    }
}
