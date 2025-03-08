package dataaccess.interfaces;

public interface DAO {
    AuthDAO getAuthDAO();
    UserDAO getUserDAO();
    GameDAO getGameDAO();
}
