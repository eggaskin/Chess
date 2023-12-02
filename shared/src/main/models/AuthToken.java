package models;

/**
 * AuthToken class that represents an authtoken for a user.
 */
public class AuthToken {
    /**
     * The authtoken of the user.
     */
    private String authtoken;
    /**
     * The username of the user.
     */
    private String username;

    /**
     * Constructor for AuthToken class.
     * @param authtoken the authtoken of the user.
     * @param username the username of the user.
     */
    public AuthToken(String authtoken, String username) {
        this.authtoken = authtoken;
        this.username = username;
    }

    /**
     * Gets the authtoken of the user.
     * @return the authtoken of the user.
     */
    public String getAuthtoken() {
        return authtoken;
    }

    /**
     * Gets the username of the user.
     * @return the username of the user.
     */
    public String getUsername() {
        return username;
    }

}
