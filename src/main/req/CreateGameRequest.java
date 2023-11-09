package req;

/**
 * A request to create a game.
 */
public class CreateGameRequest {
    /**
     * The name of the game.
     */
    private String gameName;

    /**
     * Creates a new CreateGameRequest.
     * @param gameName The name of the game.
     */
    public CreateGameRequest(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

}
