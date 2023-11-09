package res;

/**
 * A response for a create game request.
 */
public class CreateGameResponse {
    /**
     * An error message if there is one.
     */
    private String message;
    /**
     *  The game ID of the game that was created.
     */
    private int gameID;

    /**
     * Creates a new CreateGameResponse object.
     * @param gameID The game ID of the game that was created.
     */
    public CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Creates a new CreateGameResponse object.
     * @param message The error message.
     */
    public CreateGameResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getGameID() {
        return gameID;
    }

}
