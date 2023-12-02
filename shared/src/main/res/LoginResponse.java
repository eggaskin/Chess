package res;

/**
 * A response for a login request.
 */
public class LoginResponse extends UserResponse {

    /**
     * Creates a new LoginResponse object.
     *
     * @param authToken the authentication token of the response.
     * @param username  the user's requested username.
     * @param message   the message of the response, an error or success.
     */
    public LoginResponse(String authToken, String username, String message) {
        super(authToken, username, message);
    }
}
