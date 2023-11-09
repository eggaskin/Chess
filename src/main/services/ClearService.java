package services;

import dataAccess.*;
import res.MessageResponse;

import java.sql.Connection;

import static services.Server.db;

/**
 * A class to handle the Clear request.
 */
public class ClearService {

    /**
     * Clears the database.
     * @return the response body (a possible error)
     */
    public MessageResponse clear() {
        Connection conn = null;
        try {
            conn = Server.db.getConnection();
            // try to clear all databases
            (new UserDAO()).clear(conn);
            (new AuthDAO()).clear(conn);
            (new GameDAO()).clear(conn);
        } catch (DataAccessException e) {
            return new MessageResponse(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }

        return new MessageResponse();
    }
}
