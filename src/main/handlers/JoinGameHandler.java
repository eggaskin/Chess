package handlers;

import req.JoinGameRequest;
import res.MessageResponse;
import services.JoinGameService;
import spark.*;
import com.google.gson.*;

import java.util.Objects;

/**
 * A class to handle the JoinGame request.
 */
public class JoinGameHandler extends Handler {

    /**
     * Joins the user to the game and returns response.
     * @param req the request object
     * @param res the response object
     * @return the response body
     */
    public Object handle(Request req, Response res) {
        // check if request is valid
        if (!Objects.equals(req.requestMethod(), "PUT")) {
            res.status(400);
            return "";
        }
        res.type("application/json");

        // check if authToken is valid
        var authToken = req.headers("Authorization");

        if (!checkAuth(authToken)) {
            res.status(401);
            return new Gson().toJson(new MessageResponse("Error: unauthorized"));
        }

        // join game
        var body = new Gson().fromJson(req.body(), JoinGameRequest.class);
        var response = (new JoinGameService()).joinGame(body, authToken);

        // check for errors and set status
        res.status(200);
        if (response.getMessage() != null) {
            res.status(errorCode(response.getMessage()));
        }

        return new Gson().toJson(response);
    }
}
