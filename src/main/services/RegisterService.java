package services;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import req.RegisterRequest;
import res.RegisterResponse;

import java.util.UUID;
import java.sql.Connection;
import static services.Server.db;

/**
 * A class to handle the Register request.
 */
public class RegisterService {

    /**
     * Registers the user and returns an auth token.
     * @param request the request body
     * @return the response body
     */
    public RegisterResponse register(RegisterRequest request) {
        Connection conn = null;
        // check all fields are there
        if (request.getUsername()== null || request.getPassword()==null|| request.getEmail()==null) {
            return new RegisterResponse(null, null, "Error: bad request");
        }

        // make new user
        User user = new User(request.getUsername(), request.getPassword(),request.getEmail());

        // try to add user to database
        try {
            conn = db.getConnection();
            (new UserDAO()).createUser(conn,user);

            // create auth token and add to database
            AuthToken token = new AuthToken(UUID.randomUUID().toString(),user.getUsername());
            (new AuthDAO()).insertAuthToken(conn,token);

            return new RegisterResponse(token.getAuthtoken(), user.getUsername(), null);
        } catch (DataAccessException e) {
            return new RegisterResponse(null, null, e.getMessage());
        } finally {
            db.returnConnection(conn);
        }

    }
}
