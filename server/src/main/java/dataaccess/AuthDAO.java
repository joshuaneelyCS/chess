package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData auth) throws DataAccessException;
    void removeAuth(String token) throws DataAccessException;
    AuthData getAuth(String token) throws DataAccessException;
    void deleteAllAuth() throws DataAccessException;
}
