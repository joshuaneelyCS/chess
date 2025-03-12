package dataaccess;

import dataaccess.databaseimplementation.DatabaseUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {

    private DatabaseUserDAO userDAO;

    @BeforeAll
    public void setUp() throws DataAccessException {
        userDAO = new DatabaseUserDAO();
        userDAO.deleteAllUsers(); // Clear database before tests
    }

    @Test
    @Order(1)
    public void testCreateUser_Success() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "test@example.com");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("testUser");
        assertNotNull(retrievedUser);
        assertEquals("testUser", retrievedUser.getUsername());
        assertEquals("test@example.com", retrievedUser.getEmail());
    }

    @Test
    @Order(2)
    public void testCreateUser_Failure_DuplicateUsername() throws DataAccessException {
        UserData duplicateUser = new UserData("testUser", "newpassword", "newemail@example.com");
        assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser));
    }

    @Test
    @Order(3)
    public void testGetUser_Success() throws DataAccessException {
        UserData user = userDAO.getUser("testUser");
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    @Order(4)
    public void testGetUser_Failure_NonExistent() throws DataAccessException {
        assertNull(userDAO.getUser("nonExistentUser"));
    }

    @Test
    @Order(5)
    public void testGetAllUsers_Success() throws DataAccessException {
        List<UserData> users = userDAO.getAllUsers();
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(6)
    public void testDeleteAllUsers_Success() throws DataAccessException {
        userDAO.deleteAllUsers();
        List<UserData> users = userDAO.getAllUsers();
        assertTrue(users.isEmpty());
    }
}
