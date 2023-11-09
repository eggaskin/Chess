package req;

/**
 * The request body that is sent when a player attempts to join a game.
 */
public class JoinGameRequest {
    /**
     * The color of the player attempting to join the game.
     */
    private String playerColor;
    /**
     * The ID of the game the player is attempting to join.
     */
    private int gameID;

    /**
     * Creates a new JoinGameRequest.
     * @param playerColor the color of the player attempting to join the game.
     * @param gameID the ID of the game the player is attempting to join.
     */
    public JoinGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    /**
     * Gets the player color of the request.
     *
     * @return the player color of the request.
     */
    public String getPlayerColor() {
        return playerColor;
    }

    /**
     * Gets the game ID of the request.
     *
     * @return the game ID of the request.
     */
    public int getGameID() {
        return gameID;
    }

}
