package req;

/**
 * The request body that is sent when a user tries to register as a new user.
 */
public class RegisterRequest {
    /**
     * The username of the user attempting to register.
     */
    private String username;
    /**
     * The requested password of the user trying to register.
     */
    private String password;
    /**
     * The email of the user attempting to register.
     */
    private String email;

    /**
     * Creates a new RegisterRequest object.
     * @param username the username of the user attempting to register.
     * @param password the password of the user attempting to register.
     * @param email the email of the user attempting to register.
     */
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the username of the user attempting to register.
     *
     * @return the username of the user attempting to register.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user attempting to register.
     *
     * @return the password of the user attempting to register.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the email of the user attempting to register.
     * @return the email of the user attempting to register.
     */
    public String getEmail() {
        return email;
    }
}
