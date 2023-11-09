package res;

/**
 * A response for a user's attempt to either login or register.
 */
public class UserResponse {
    /**
     * The authentication token of the response.
     */
    private String authToken;
    /**
     * The user's requested username.
     */
    private String username;
    /**
     * The message of the response, an error or success.
     */
    private String message;

    /**
     * Creates a new UserResponse object.
     * @param authToken the authentication token of the response.
     * @param username the user's requested username.
     * @param message the message of the response, an error or success.
     */
    public UserResponse(String authToken, String username, String message) {
        this.authToken = authToken;
        this.username = username;
        this.message = message;
    }

    /**
     * Gets the message of the response.
     * @return the message of the response.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the authentication token of the response.
     * @return the authentication token of the response.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Gets the username of the response.
     * @return the username of the response.
     */
    public String getUserName() {
        return username;
    }

}
