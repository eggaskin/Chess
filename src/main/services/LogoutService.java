package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import res.MessageResponse;
import java.sql.Connection;
import static services.Server.db;
/**
 * A class to handle the Logout request.
 */
public class LogoutService {

    /**
     * Logs out the user.
     * @return the response body (a possible error)
     * @param tok the auth token of the user
     */
    public MessageResponse logout(String tok) {
        Connection conn = null;
        // delete the auth token from the database
        try {
            conn = db.getConnection();
            (new AuthDAO()).removeAuthToken(conn,tok);
        } catch (DataAccessException e) {
            return new MessageResponse(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
        return new MessageResponse();
    }
}
