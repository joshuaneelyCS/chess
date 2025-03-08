package dataaccess.memoryImplimentation;
import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryAuthDAO extends MemoryDAO implements AuthDAO {

    HashMap<String, AuthData> signedIn = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        signedIn.put(auth.getAuthToken(), auth);
    }

    @Override
    public void removeAuth(String token) throws DataAccessException {
        if (signedIn.containsKey(token)) {
            signedIn.remove(token);
        } else {
            throw new DataAccessException("Error: No token found!");
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        if (signedIn.containsKey(token)) {
            return signedIn.get(token);
        } else {
            throw new DataAccessException("Error: No token found!");
        }

    }

    @Override
    public void deleteAllAuth() {
        signedIn.clear();
    }

    @Override
    public List<AuthData> getAllAuth() {
        return new ArrayList<>(signedIn.values());
    }
}
