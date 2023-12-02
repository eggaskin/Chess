package req;

/**
 * The request that is sent when a user attempts to login.
 */
public class LoginRequest {
    /**
     * The username of the user attempting to login.
     */
    private String username;
    /**
     * The password input with the login attempt.
     */
    private String password;

    /**
     * Creates a new LoginRequest.
     * @param username the username of the user attempting to login.
     * @param password the password of the user attempting to login.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username of the user attempting to login.
     * @return the username of the user attempting to login.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user attempting to login.
     * @return the password of the user attempting to login.
     */
    public String getPassword() {
        return password;
    }

}
