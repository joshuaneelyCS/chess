package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData auth);
    void removeAuth(String token);
    AuthData getAuth(String token);
    void deleteAllAuth();
}
