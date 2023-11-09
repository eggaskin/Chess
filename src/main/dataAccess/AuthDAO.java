package dataAccess;

import com.google.gson.Gson;
import models.AuthToken;
import models.Game;

import java.util.HashMap;
import java.util.UUID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * AuthDAO class.
 * Accesses the AuthToken table in the database.
 */
public class AuthDAO {

    /**
     * Creates the AuthDAO instance.
     * @throws DataAccessException
     */
    public AuthDAO() throws DataAccessException {
    }

    /**
     * Get a specific authToken.
     * @param authTokenID the authTokenID
     * @return the authToken
     * @throws DataAccessException
     */
    public AuthToken getAuthToken(Connection conn, String authTokenID) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT authtoken,username FROM auths WHERE authtoken=?")) {
            preparedStatement.setString(1, authTokenID);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: unauthorized");
                }
                return new AuthToken(rs.getString("authtoken"), rs.getString("username"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Insert a new authToken.
     * @param authToken the authToken
     * @throws DataAccessException
     */
    public void insertAuthToken(Connection conn, AuthToken authToken) throws DataAccessException {
        try {
            getAuthToken(conn, authToken.getAuthtoken());
            throw new DataAccessException("Error: already taken");
        } catch (DataAccessException e) {
            // do nothing, it's supposed to throw (we want it not to exist)
        }
        try (var preparedStatement = conn.prepareStatement("INSERT INTO auths (authtoken, username) VALUES(?, ?)")) {
            preparedStatement.setString(1, authToken.getAuthtoken());
            preparedStatement.setString(2, authToken.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Remove an authToken.
     * @param authTokenID the authTokenID
     * @throws DataAccessException
     */
    public void removeAuthToken(Connection conn, String authTokenID) throws DataAccessException {
        try {
            getAuthToken(conn, authTokenID);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: unauthorized");
        }

        try (var preparedStatement = conn.prepareStatement("DELETE FROM auths WHERE authtoken=?")) {
            preparedStatement.setString(1, authTokenID);
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Clear all authTokens.
     * @throws DataAccessException
     */
    public void clear(Connection conn) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE auths")) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }
}
