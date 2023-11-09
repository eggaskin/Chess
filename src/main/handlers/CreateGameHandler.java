package handlers;
import req.CreateGameRequest;
import res.MessageResponse;
import services.CreateGameService;
import spark.*;
import java.util.*;
import com.google.gson.*;

/**
 * Handles requests for creating a game
 */
public class CreateGameHandler extends Handler {

    /**
     * Handles the request for creating a game
     * @param req the request object.
     * @param res the response object.
     * @return the response body.
     */
    public Object handle(Request req, Response res) {
        // check if request is valid
        if (!Objects.equals(req.requestMethod(), "POST")) {
            res.status(400);
            return null;
        }
        res.type("application/json");

        // check auth token
        var authToken = req.headers("Authorization");
        // check if authToken is valid
        if (!checkAuth(authToken)) {
            res.status(401);
            return new Gson().toJson(new MessageResponse("Error: unauthorized"));
        }

        // get body and list
        var body = new Gson().fromJson(req.body(), CreateGameRequest.class);
        var response = (new CreateGameService()).createGame(body);

        // check for errors and set status
        res.status(200);
        if (response.getMessage() != null) {
            res.status(errorCode(response.getMessage()));
        }

        return new Gson().toJson(response);
    }
}
