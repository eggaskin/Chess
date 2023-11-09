package handlers;
import req.LoginRequest;
import services.LoginService;
import spark.*;
import java.util.*;
import com.google.gson.*;

/**
 * A class to handle the Login request.
 */
public class LoginHandler extends Handler {
    /**
     * Handles the Login request.
     * @param req the request Object.
     * @param res the response Object.
     * @return the response body.
     */
    public Object handle(Request req, Response res) {
        // check if request is valid
        if (!Objects.equals(req.requestMethod(), "POST")) {
            res.status(400);
            return "";
        }
        res.type("application/json");

        // login
        LoginRequest body = new Gson().fromJson(req.body(), LoginRequest.class);
        var response = (new LoginService()).login(body);

        // check for errors and set status
        res.status(200);
        if (response.getMessage() != null) {
            res.status(errorCode(response.getMessage()));
        }

        return new Gson().toJson(response);
    }
}
