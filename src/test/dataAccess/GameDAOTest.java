package dataAccess;

import models.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static services.Server.db;

class GameDAOTest {
    private static int id = 2;

    @BeforeEach
    void clearGames() throws DataAccessException {
        Connection conn = db.getConnection();
        (new GameDAO()).clear(conn);
        db.returnConnection(conn);
    }

    Game makeGame(String name) {
        Game g = new Game(id, name);
        id++;
        return g;
    }

    @Test
    void createGame() throws DataAccessException {
        Game g = makeGame("game");
        Connection conn = db.getConnection();
        try {
            (new GameDAO()).createGame(conn,g);
            assertNotNull((new GameDAO()).findGame(conn, g.getGameID()));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void badCreateGame() throws DataAccessException {
        //try to create a game with an existing ID
        Game g = new Game(id, "game");
        Game g2 = makeGame("game2");
        Connection conn = db.getConnection();
        try {
            (new GameDAO()).createGame(conn,g);
            assertThrows(DataAccessException.class, () -> {(new GameDAO()).createGame(conn,g2);});
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void findGame() throws DataAccessException {
        Connection conn = db.getConnection();
        Game g = makeGame("game");
        try {
            (new GameDAO()).createGame(conn,g);
            assertNotNull((new GameDAO()).findGame(conn, g.getGameID()));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void findBadGame() throws DataAccessException {
        //try to find a game that doesn't exist
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new GameDAO()).findGame(conn, 1);});
        db.returnConnection(conn);
    }

    @Test
    void removeGame() throws DataAccessException {
        Connection conn = db.getConnection();
        Game g = makeGame("game");
        try {
            (new GameDAO()).createGame(conn,g);
            assertDoesNotThrow(() -> {(new GameDAO()).removeGame(conn, g.getGameID());});
            assertThrows(DataAccessException.class, () -> {(new GameDAO()).findGame(conn, g.getGameID());});
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void removeBadGame() throws DataAccessException {
        // try to remove a game that doesn't exist
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new GameDAO()).removeGame(conn, 1);});
        db.returnConnection(conn);
    }

    @Test
    void findAllGames()  throws DataAccessException {
        // for this test there's not really a negative test case so this is my only test.
        // there's no way to make a 'bad' list games request to the DAO directly
        Connection conn = db.getConnection();
        Game g = makeGame("game");
        try {
            (new GameDAO()).createGame(conn,g);
            assertNotNull((new GameDAO()).findAllGames(conn));
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void updateGame() throws DataAccessException {
        Connection conn = db.getConnection();
        Game g = makeGame("game");
        try {
            (new GameDAO()).createGame(conn,g);
            assertDoesNotThrow(() -> {(new GameDAO()).updateGame(conn, g.getGameID(), "name");});
            assertEquals("name", (new GameDAO()).findGame(conn, g.getGameID()).getGameName());
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void updateBadGame() throws DataAccessException {
        //update a game that doesn't exist
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new GameDAO()).updateGame(conn, 1, "name");});
        db.returnConnection(conn);
    }

    @Test
    void clear() throws DataAccessException {
        Connection conn = db.getConnection();
        try {
            Game g = makeGame("game");
            (new GameDAO()).createGame(conn,g);
            assertDoesNotThrow(() -> {(new GameDAO()).clear(conn);} );
            assertThrows(DataAccessException.class, () -> {(new GameDAO()).findGame(conn, g.getGameID());});
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }

    }

    @Test
    void claimSpot() throws DataAccessException {
        Connection conn = db.getConnection();
        Game g = makeGame("game");
        try {
            (new GameDAO()).createGame(conn,g);
            assertDoesNotThrow(() -> {(new GameDAO()).claimSpot(conn, g.getGameID(), "username", "WHITE");});
            assertEquals("username", (new GameDAO()).findGame(conn, g.getGameID()).getWhiteUsername());
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void claimBadSpot() throws DataAccessException {
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new GameDAO()).claimSpot(conn, 1, "username", "WHITE");});
        db.returnConnection(conn);
    }

    @Test
    void observeGame() throws DataAccessException {
        Connection conn = db.getConnection();
        Game g = makeGame("game");
        try {
            (new GameDAO()).createGame(conn,g);
            assertDoesNotThrow(() -> {(new GameDAO()).observeGame(conn, g.getGameID(), "username");});
        } catch (DataAccessException e) {
            fail(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    @Test
    void observeBadGame() throws DataAccessException {
        // observe a game that doesn't exist
        Connection conn = db.getConnection();
        assertThrows(DataAccessException.class, () -> {(new GameDAO()).observeGame(conn, 1, "username");});
        db.returnConnection(conn);
    }
}