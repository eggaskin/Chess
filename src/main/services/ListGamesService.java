package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import handlers.ListGamesHandler;
import models.AuthToken;
import res.ListGamesResponse;
import java.sql.Connection;
import static services.Server.db;

/**
 * A class to handle the ListGames request.
 */
public class ListGamesService {

    /**
     * Returns a list of games.
     * @return A list of games.
     */
    public ListGamesResponse listGames(String tok) {
        Connection conn = null;
        // validate auth token
        try {
            conn = db.getConnection();
            (new AuthDAO()).getAuthToken(conn,tok);
        } catch (DataAccessException e) {
            return new ListGamesResponse(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }

        // get list of games
        try {
            conn = db.getConnection();
            return new ListGamesResponse((new GameDAO()).findAllGames(conn));
        } catch (DataAccessException e) {
            return new ListGamesResponse(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

}
