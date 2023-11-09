package handlers;

import com.google.gson.Gson;
import req.RegisterRequest;
import res.RegisterResponse;
import services.RegisterService;
import spark.Request;
import spark.Response;

import java.util.Objects;

/**
 * Handles register requests
 */
public class RegisterHandler extends Handler {

    /**
     * Perform the register request
     * @param req the request object
     * @param res the response object
     * @return the response body
     */
    public Object handle(Request req, Response res) {
        // check for invalid request
        if (!Objects.equals(req.requestMethod(), "POST")) {
            res.status(400);
            return "";
        }
        res.type("application/json");

        // register
        var body = new Gson().fromJson(req.body(), RegisterRequest.class);
        var response = (new RegisterService()).register(body);

        // check for errors and set status
        res.status(200);
        if (response.getMessage() != null) {
            // if 'duplicate entry' is in the error message, set it to Error: already taken
            if (response.getMessage().contains("Duplicate entry")) {
                response = new RegisterResponse(null, null, "Error: already taken");
            }
            res.status(errorCode(response.getMessage()));
        }

        return new Gson().toJson(response);
    }
}
