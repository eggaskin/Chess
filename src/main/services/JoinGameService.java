package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.AuthToken;
import req.JoinGameRequest;
import res.JoinGameResponse;
import java.sql.Connection;
import static services.Server.db;

/**
 * A class to handle the JoinGame request.
 */
public class JoinGameService {

    /**
     * Joins the user to the game and returns response.
     * @param request the request body
     * @param tok the auth token
     * @return the response body
     */
    public JoinGameResponse joinGame(JoinGameRequest request, String tok) {
        Connection conn = null;

        // check fields
        if (request == null || request.getGameID() == 0) {
            return new JoinGameResponse("Error: bad request");
        }

        try {
            conn = db.getConnection();
            // get user's username
            AuthToken token = (new AuthDAO()).getAuthToken(conn,tok);

            if (request.getPlayerColor() != null) {
                // try to join game
                (new GameDAO()).claimSpot(conn,request.getGameID(), token.getUsername(), request.getPlayerColor());
            }
            else {
                // otherwise, observe game
                (new GameDAO()).observeGame(conn,request.getGameID(), token.getUsername());
            }
        } catch (DataAccessException e) {
            return new JoinGameResponse(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }

        return new JoinGameResponse();
    }
}
