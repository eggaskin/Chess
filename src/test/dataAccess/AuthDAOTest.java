package dataAccess;

import models.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static services.Server.db;

class AuthDAOTest {

    @BeforeEach
    void clearToks() throws DataAccessException {
        Connection conn = db.getConnection();
        (new AuthDAO()).clear(conn);
        db.returnConnection(conn);
    }


    void addToken(Connection conn, String token) throws DataAccessException {
        AuthToken authToken = new AuthToken(token, "username");
        (new AuthDAO()).insertAuthToken(conn, authToken);
    }

    @Test
    void getAuthToken() throws DataAccessException {
        Connection conn = db.getConnection();
        addToken(conn, "authtoken");
        assertNotNull((new AuthDAO()).getAuthToken(conn, "authtoken"));
        db.returnConnection(conn);
    }

    @Test
    void badAuthToken() throws DataAccessException {
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new AuthDAO()).getAuthToken(conn, "authtoken");});
        db.returnConnection(conn);
    }

    @Test
    void insertAuthToken() throws DataAccessException {
        Connection conn = db.getConnection();

        try {
            addToken(conn, "authtoken");
            assertNotNull((new AuthDAO()).getAuthToken(conn, "authtoken"));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void insertDuplToken() throws DataAccessException {
        Connection conn = db.getConnection();
        try {
            addToken(conn, "authtoken");
            assertThrows(DataAccessException.class, () -> {addToken(conn, "authtoken");});
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void removeAuthToken() throws DataAccessException {
        Connection conn = db.getConnection();
        try {
            addToken(conn, "authtoken");
            assertDoesNotThrow(() -> {(new AuthDAO()).removeAuthToken(conn, "authtoken");});
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void removeBadToken() throws DataAccessException{
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new AuthDAO()).removeAuthToken(conn, "authtoken");});
        db.returnConnection(conn);
    }

    @Test
    void clear() throws DataAccessException {
        Connection conn = db.getConnection();
        try {
            addToken(conn, "authtoken");
            (new AuthDAO()).clear(conn);
            assertThrows(DataAccessException.class, () -> {(new AuthDAO()).getAuthToken(conn, "authtoken");});

        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }
}