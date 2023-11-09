package models;

/**
 * User class that represents a user.
 */
public class User  {
    /**
     * The username of the user.
     */
    private String username;
    /**
     * The password of the user.
     */
    private String password;
    /**
     * The email of the user.
     */
    private String email;

    /**
     * Constructor for creating a new user.
     */
    public User() {}

    /**
     * Constructor for creating a new user.
     * @param username the username of the user.
     * @param password the password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor for creating a new user.
     * @param username the username of the user.
     * @param password the password of the user.
     * @param email the email of the user.
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the username of the user.
     * @return the username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user.
     * @return the password of the user.
     */
    public String getPassword() {
        return password;
    }

}
