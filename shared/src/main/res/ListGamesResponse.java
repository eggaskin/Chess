package res;

import models.Game;

/**
 * A response for a list games request.
 */
public class ListGamesResponse {

    /**
     * A class that takes a Game and strips the ChessGame object from it
     * so that it can be sent to the client.
     */
    public record GameObj(int gameID, String gameName, String whiteUsername, String blackUsername) {
        /**
         * Constructor for GameObj class.
         * @param game the ChessGame object.
         */
        public GameObj(Object game) {
            this(((Game) game).getGameID(), ((Game)game).getGameName(), ((Game) game).getWhiteUsername(), ((Game) game).getBlackUsername());
        }
    }

    /**
     * An error message if there is one.
     */
    private String message;

    /**
     * The list of games.
     */
    private GameObj[] games;

    /**
     * Constructor for ListGamesResponse class.
     * @param games the list of game objects.
     */
    public ListGamesResponse(Object[] games) {
        this.games = new GameObj[games.length];
        for (int i = 0; i < games.length; i++) {
            this.games[i] = new GameObj(games[i]);
        }
    }

    /**
     * Constructor for ListGamesResponse class.
     * @param message an error message if there is one.
     */
    public ListGamesResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public GameObj[] getGames() {
        return games;
    }

}
