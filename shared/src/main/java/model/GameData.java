package model;
import chess.ChessGame;

public class GameData {

    private int gameID;
    private String whiteUsername = "";
    private String blackUsername = "";
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

    public void setTeam(String playerColor, String username) throws GameAlreadyTakenException {
        if (playerColor.equals("WHITE") && whiteUsername == null) {
            this.whiteUsername = username;
        } else if (playerColor.equals("BLACK") && blackUsername == null) {
            this.blackUsername = username;
        } else {
            throw new GameAlreadyTakenException("Error: Team Color already taken");
        }
    }
}
