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
        var currPosition = initPosition;
        var direction = 1;
        var startingRow = 2;

        if (pieceColor == ChessGame.TeamColor.BLACK) {
            direction = -1;
            startingRow = 7;
        }

        if (currPosition.getRow() == startingRow) {
            // if it's in the starting position, allow the double space forward
            currPosition = new ChessPosition(currPosition.getRow() + (direction), currPosition.getColumn());
            // if the square in front is not blocked
            if (board.getPiece(currPosition) == null) {
                currPosition = new ChessPosition(currPosition.getRow() + (direction), currPosition.getColumn());
                // if the next square is not blocked
                if (board.getPiece(currPosition) == null) {
                    listOfMoves.add(new ChessMove(initPosition, currPosition, null));
                }
            }

            currPosition = initPosition;
        }
        // Move one space forward
        currPosition = new ChessPosition(currPosition.getRow() + direction, currPosition.getColumn());
        listOfMoves = getChessMoves(board, currPosition, listOfMoves, initPosition);

        // Attack spaces on the diagonal
        currPosition = initPosition;
        currPosition = new ChessPosition(currPosition.getRow() + direction, currPosition.getColumn() + 1);
        listOfMoves = getChessMoves(board, initPosition, listOfMoves, currPosition);

        currPosition = initPosition;
        currPosition = new ChessPosition(currPosition.getRow() + direction, currPosition.getColumn() - 1);
        listOfMoves = getChessMoves(board, initPosition, listOfMoves, currPosition);

        return listOfMoves;
    }

    private ArrayList<ChessMove> getChessMoves(ChessBoard board, ChessPosition initPosition, ArrayList<ChessMove> listOfMoves, ChessPosition currPosition) {
        if (!outOfBounds(currPosition)) {
            if (board.getPiece(currPosition) != null) {
                if (board.getPiece(currPosition).getTeamColor() != pieceColor) {
                    if(currPosition.getRow() == 8 || currPosition.getRow() == 1) {
                        // if on promotion square
                        listOfMoves = addPromotions(initPosition, currPosition, listOfMoves);
                    } else {
                        listOfMoves.add(new ChessMove(initPosition, currPosition, null));
                    }
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
