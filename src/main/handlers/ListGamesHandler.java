package handlers;
import res.MessageResponse;
import services.ListGamesService;
import spark.*;
import java.util.*;
import com.google.gson.*;

/**
 * A class to handle the ListGames request.
 */
public class ListGamesHandler extends Handler {

    /**
     * Handle the listgames request, call the service, and return the response.
     * @param req the request object.
     * @param res the response object.
     * @return the response body.
     */
    public Object handle(Request req, Response res) {
        // check if request is valid
        if (!Objects.equals(req.requestMethod(), "GET")) {
            res.status(400);
            return "";
        }
        res.type("application/json");

        // check auth token
        var authToken = req.headers("Authorization");
        if (!checkAuth(authToken)) {
            res.status(401);
            return new Gson().toJson(new MessageResponse("Error: unauthorized"));
        }

        // list games
        var response = (new ListGamesService()).listGames(authToken);

        // check for errors
        res.status(200);
        if (response != null && response.getMessage() != null) {
            res.status(errorCode(response.getMessage()));
        }

        return new Gson().toJson(response);
    }
}
