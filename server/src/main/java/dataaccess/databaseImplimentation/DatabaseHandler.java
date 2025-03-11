package dataaccess.databaseImplimentation;

import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    public class AuthDataHandler {

        // Convert a single row into an AuthData object
        public static AuthData resultSetToAuthData(ResultSet rs) throws SQLException {
            if (rs.next()) { // Moves cursor to the first row
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }
            return null; // No matching data found
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
            if (rs.next()) { // Moves cursor to the first row
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
}
