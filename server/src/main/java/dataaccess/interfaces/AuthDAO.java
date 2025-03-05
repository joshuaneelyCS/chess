package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.AuthData;

import java.util.List;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    void removeAuth(String token) throws DataAccessException;
    AuthData getAuth(String token) throws DataAccessException;
    void deleteAllAuth() throws DataAccessException;
    List<AuthData> getAllAuth() throws DataAccessException;
}
