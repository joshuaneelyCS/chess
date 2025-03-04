package model;
import chess.ChessGame;

public class GameData {

    private int gameID;
    private String whiteUsername = null;
    private String blackUsername = null;
    private String gameName;
    private ChessGame game;

    public GameData(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName != null ? gameName : "";
    }

    public int getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setTeam(String playerColor, String username) throws GameAlreadyTakenException, InvalidColorException {
        if (playerColor == null) {
            throw new InvalidColorException("Error: Please enter a team color");
        }
        if (playerColor.equals("WHITE")) {
            if (whiteUsername == null) {
                this.whiteUsername = username;
            } else {
                throw new GameAlreadyTakenException("Error: Team Color already taken");
            }
        } else if (playerColor.equals("BLACK")) {
            if (blackUsername == null) {
                this.blackUsername = username;
            } else {
                throw new GameAlreadyTakenException("Error: Team Color already taken");
            }
        } else {
            throw new InvalidColorException("Error: Team Color invalid");
        }
    }
}
