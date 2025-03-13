package dataaccess;

import dataaccess.databaseimplementation.DatabaseAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTests {

    private DatabaseAuthDAO authDAO;

    @BeforeAll
    public void setUp() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
        authDAO.deleteAllAuth(); // Clear auth table before tests
    }

    @Test
    @Order(1)
    public void testCreateAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "testUser");
        authDAO.createAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("token123");
        assertNotNull(retrievedAuth);
        assertEquals("testUser", retrievedAuth.getUsername());
    }

    @Test
    @Order(2)
    public void testCreateAuthFailureDuplicateToken() throws DataAccessException {
        AuthData duplicateAuth = new AuthData("token123", "newUser");
    }

    @Test
    @Order(3)
    public void testGetAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.getAuth("token123");
        assertNotNull(auth);
    }

    @Test
    @Order(4)
    public void testGetAuthFailureNonExistent() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("token123"));
    }

    @Test
    @Order(5)
    public void testGetAllAuthSuccess() throws DataAccessException {
        List<AuthData> authList = authDAO.getAllAuth();
        assertFalse(authList.isEmpty());
    }

    @Test
    @Order(6)
    public void testRemoveAuthSuccess() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.removeAuth("token123"));
    }

    @Test
    @Order(7)
    public void testRemoveAuthFailureNonExistent() {
        assertThrows(DataAccessException.class, () -> authDAO.removeAuth("invalidToken"));
    }

    @Test
    @Order(8)
    public void testDeleteAllAuthSuccess() throws DataAccessException {
        authDAO.deleteAllAuth();
        List<AuthData> authList = authDAO.getAllAuth();
        assertTrue(authList.isEmpty());
    }
}