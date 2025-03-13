package dataaccess.databaseimplementation;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    public class AuthDataHandler {

        // Convert a single row into an AuthData object
        public static AuthData resultSetToAuthData(ResultSet rs) throws SQLException {
            if (rs.next()) {// Moves cursor to the first row
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }
            throw new SQLException("Token could not be found"); // No matching data found
        }

        // Convert multiple rows into a List<AuthData>
        public static List<AuthData> resultSetToAuthDataList(ResultSet rs) throws SQLException {
            List<AuthData> authDataList = new ArrayList<>();
            while (rs.next()) { // Iterate through all rows
                authDataList.add(new AuthData(rs.getString("authToken"), rs.getString("username")));
            }
            return authDataList; // Return list of AuthData objects
        }
    }

    public class UserDataHandler {

        // Convert a single row into an AuthData object
        public static UserData resultSetToUserData(ResultSet rs) throws SQLException {
            if (rs.next()) {
                // Moves cursor to the first row
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
            return null; // No matching data found
        }

        // Convert multiple rows into a List<AuthData>
        public static List<UserData> resultSetToUserDataList(ResultSet rs) throws SQLException {
            List<UserData> userDataList = new ArrayList<>();
            while (rs.next()) { // Iterate through all rows
                userDataList.add(new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email")));
            }
            return userDataList; // Return list of AuthData objects
        }
    }

    public class GameDataHandler {

        private static final Gson GSON = new Gson();

        // Convert a JSON String to a ChessGame object
        public static ChessGame convertStringToGame(String data) {
            if (data == null || data.isEmpty()) {
                return new ChessGame(); // Return an empty ChessGame if data is null
            }
            return GSON.fromJson(data, ChessGame.class);
        }

        // Convert a JSON String to a ChessGame object
        public static String convertGameToString(ChessGame data) {
            return GSON.toJson(data);
        }


        // Convert a single row into an AuthData object
        public static GameData resultSetToGameData(ResultSet rs) throws SQLException {
            if (rs.next()) {

                var game = new GameData(
                        rs.getInt("game_id"),
                        rs.getString("game_name"),
                        convertStringToGame(rs.getString("chess_game")));

                game.setTeam("WHITE", rs.getString("white_username"));
                game.setTeam("BLACK", rs.getString("black_username"));
                // Moves cursor to the first row
                return game;
            }
            return null; // No matching data found
        }

        // Convert multiple rows into a List<AuthData>
        public static List<GameData> resultSetToGameDataList(ResultSet rs) throws SQLException {
            List<GameData> gameDataList = new ArrayList<>();

            while (rs.next()) {
                var game = new GameData(
                        rs.getInt("game_id"),
                        rs.getString("game_name"),
                        convertStringToGame(rs.getString("chess_game")));

                game.setTeam("WHITE", rs.getString("white_username"));
                game.setTeam("BLACK", rs.getString("black_username"));

                gameDataList.add(game);
            }
            return gameDataList; // Return list of AuthData objects
        }
    }
}
