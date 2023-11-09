package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import req.LoginRequest;
import res.LoginResponse;

import java.util.UUID;
import java.sql.Connection;
import static services.Server.db;

/**
 * A class to handle the Login request.
 */
public class LoginService {
    /**
     * Logs in the user and returns an auth token.
     * @param request the request body
     * @return the response body
     */
    public LoginResponse login(LoginRequest request) {
        Connection conn = null;
        // check all fields are there
        if (request.getUsername()== null || request.getPassword()==null) {
            return new LoginResponse(null, null, "Error: bad request");
        }

        // add new user
        User user = new User(request.getUsername(), request.getPassword());
        User compUser; // user to compare password with

        // check if user exists
        try {
            conn = db.getConnection();
            compUser = (new UserDAO()).findUser(conn,user.getUsername());
        } catch (DataAccessException e) {
            return new LoginResponse(null, null, e.getMessage());
        } finally {
            db.returnConnection(conn);
        }

        // if user exists, check if password is correct
        if (compUser.getPassword().equals(user.getPassword())) {
            // try to generate auth token
            AuthToken token = new AuthToken(UUID.randomUUID().toString(),user.getUsername());
            try {
                conn = db.getConnection();
                (new AuthDAO()).insertAuthToken(conn, token);
            } catch (DataAccessException e) {
                return new LoginResponse(null, null, e.getMessage());
            } finally {
                db.returnConnection(conn);
            }

            return new LoginResponse(token.getAuthtoken(), user.getUsername(), null);
        } else {
            // if password is incorrect, return error
            return new LoginResponse(null, null, "Error: unauthorized");
        }

    }
}
