package handlers;
import res.MessageResponse;
import services.LogoutService;
import spark.*;
import java.util.*;
import com.google.gson.*;

/**
 * Handler for logout requests
 */
public class LogoutHandler extends Handler {

    /**
     * Handles logout request
     * @param req the request object
     * @param res the response object
     * @return the response body
     */
    public Object handle(Request req, Response res) {
        // check if request is valid
        if (!Objects.equals(req.requestMethod(), "DELETE")) {
            res.status(400);
            return null;
        }
        res.type("application/json");

        // check the auth token
        var authToken = req.headers("Authorization");
        if (!checkAuth(authToken)) {
            res.status(errorCode("Error: unauthorized"));
            return new Gson().toJson(new MessageResponse("Error: unauthorized"));
        }

        // logout
        var response = (new LogoutService()).logout(authToken);

        // check for errors and set status
        res.status(200);
        if (response != null && response.getMessage() != null) {
            res.status(errorCode(response.getMessage()));
        }

        return new Gson().toJson(response);
    }
}
