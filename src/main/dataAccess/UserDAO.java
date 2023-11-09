package dataAccess;
import models.AuthToken;
import models.User;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * The UserDAO class.
 * Accesses User table in the database.
 */
public class UserDAO {

    /**
     * Creates a new UserDAO object.
     * @throws DataAccessException
     */
    public UserDAO() throws DataAccessException {}

    /**
     * Creates a new UserDAO object.
     * @param u the user to be stored in the database.
     * @throws DataAccessException
     */
    public void createUser(Connection conn, User u) throws DataAccessException {
        try {
            findUser(conn, u.getUsername());
            throw new DataAccessException("Error: already taken");
        } catch (DataAccessException e) {
            // do nothing, it's supposed to throw (we want it not to exist)
        }
        try (var preparedStatement = conn.prepareStatement("INSERT INTO users (username, password) VALUES(?, ?)")) {
            preparedStatement.setString(1, u.getUsername());
            preparedStatement.setString(2, u.getPassword());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Finds a user in the database.
     * @param username the username of the user to be found.
     * @return the user with the given username.
     * @throws DataAccessException
     */
    public User findUser(Connection conn, String username) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT username,password FROM users WHERE username=?")) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: unauthorized");
                }
                return new User(rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Clears all data from the database.
     * @throws DataAccessException
     */
    public void clear(Connection conn) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE users")) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }
}
