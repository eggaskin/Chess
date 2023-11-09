package dataAccess;

import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static services.Server.db;

class UserDAOTest {

    @BeforeEach
    void clearUsers() throws DataAccessException {
        Connection conn = db.getConnection();
        (new UserDAO()).clear(conn);
        db.returnConnection(conn);
    }


    void addUser(Connection conn, String username) throws DataAccessException {
        User u = new User(username, "password");
        (new UserDAO()).createUser(conn, u);
    }

    @Test
    void createUser() throws DataAccessException {
        Connection conn = db.getConnection();

        try {
            addUser(conn, "username");
            assertNotNull((new UserDAO()).findUser(conn, "username"));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }


    @Test
    void createDuplUser() throws DataAccessException {
        Connection conn = db.getConnection();
        try {
            addUser(conn, "username");
            assertThrows(DataAccessException.class, () -> {addUser(conn, "username");});
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }


    @Test
    void findUser() throws DataAccessException {
        Connection conn = db.getConnection();
        addUser(conn, "username");
        assertNotNull((new UserDAO()).findUser(conn, "username"));
        db.returnConnection(conn);
    }


    @Test
    void badUser() throws DataAccessException {
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new UserDAO()).findUser(conn, "username");});
        db.returnConnection(conn);
    }

    @Test
    void clear() throws DataAccessException {
        Connection conn = db.getConnection();
        try {
            addUser(conn, "username");
            (new UserDAO()).clear(conn);
            assertThrows(DataAccessException.class, () -> {(new UserDAO()).findUser(conn, "username");});

        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }
}