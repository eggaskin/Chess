package res;

/**
 * A response for a user's register request.
 */
public class RegisterResponse extends UserResponse {


    /**
     * Creates a new RegisterResponse object.
     *
     * @param authToken the authentication token of the response.
     * @param username  the user's requested username.
     * @param message   the message of the response, an error or success.
     */
    public RegisterResponse(String authToken, String username, String message) {
        super(authToken, username, message);
    }
}
