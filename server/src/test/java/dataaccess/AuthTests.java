package dataaccess;

import dataaccess.databaseImplimentation.databaseAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTests {

    private databaseAuthDAO authDAO;

    @BeforeAll
    public void setUp() throws DataAccessException {
        authDAO = new databaseAuthDAO();
        authDAO.deleteAllAuth(); // Clear auth table before tests
    }

    @Test
    @Order(1)
    public void testCreateAuth_Success() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testUser");
        authDAO.createAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("token123");
        assertNotNull(retrievedAuth);
        assertEquals("testUser", retrievedAuth.getUsername());
    }

    @Test
    @Order(2)
    public void testCreateAuth_Failure_DuplicateToken() throws DataAccessException {
        AuthData duplicateAuth = new AuthData("token123", "newUser");
    }

    @Test
    @Order(3)
    public void testGetAuth_Success() throws DataAccessException {
        AuthData auth = authDAO.getAuth("token123");
        assertNotNull(auth);
    }

    @Test
    @Order(4)
    public void testGetAuth_Failure_NonExistent() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("token123"));
    }

    @Test
    @Order(5)
    public void testGetAllAuth_Success() throws DataAccessException {
        List<AuthData> authList = authDAO.getAllAuth();
        assertFalse(authList.isEmpty());
    }

    @Test
    @Order(6)
    public void testRemoveAuth_Success() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.removeAuth("token123"));
    }

    @Test
    @Order(7)
    public void testRemoveAuth_Failure_NonExistent() {
        assertThrows(DataAccessException.class, () -> authDAO.removeAuth("invalidToken"));
    }

    @Test
    @Order(8)
    public void testDeleteAllAuth_Success() throws DataAccessException {
        authDAO.deleteAllAuth();
        List<AuthData> authList = authDAO.getAllAuth();
        assertTrue(authList.isEmpty());
    }
}