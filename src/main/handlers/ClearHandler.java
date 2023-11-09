package handlers;
import spark.*;
import java.util.*;
import com.google.gson.*;
import services.ClearService;

/**
 * A class to handle the Clear request.
 */
public class ClearHandler {

    /**
     * Clears the database.
     * @param req the request object
     * @param res the response object
     * @return the response body
     */
    public Object handle(Request req, Response res) {
        // check for valid request
        if (!Objects.equals(req.requestMethod(), "DELETE")) {
            res.status(400);
            return "";
        }
        res.type("application/json");

        // clear the database
        var response = (new ClearService()).clear();

        // set response status code
        res.status(200);
        if (response.getMessage() != null) {
            res.status(500);
        }

        return new Gson().toJson(response);
    }
}
