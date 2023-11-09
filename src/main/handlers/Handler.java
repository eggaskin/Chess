package handlers;

import services.Server;

import java.sql.Connection;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import static services.Server.db;

/**
 * Handler class that contains methods that are used by all handlers
 */
public class Handler {

    /**
     * Checks an error message and returns the appropriate error code
     * @param message the error message
     * @return the error code
     */
    public int errorCode(String message) {
        if (message.equals("Error: unauthorized")) {
            return 401;
        }
        if (message.equals("Error: bad request")) {
            return 400;
        }
        if (message.equals("Error: already taken")) {
            return 403;
        }
        return 500;
    }

    /**
     * Checks if the authToken is valid
     * @param authToken the authToken to check
     * @return true if the authToken is valid, false otherwise
     */
    protected boolean checkAuth(String authToken) {
        // check if authToken is valid
        Connection conn = null;
        try {
            conn = Server.db.getConnection();
            (new AuthDAO()).getAuthToken(conn, authToken);
        } catch (DataAccessException e) {
            return false;
        } finally {
            db.returnConnection(conn);
        }
        return true;
    }
}
