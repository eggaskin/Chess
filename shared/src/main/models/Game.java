package models;
import chess.ChessGame;
import chess.GameImpl;

import java.util.ArrayList;

/**
 * Game class that represents a game.
 */
public class Game {
    /**
     * The gameID of the game.
     */
    private int gameID;
    /**
     * The gameName of the game.
     */
    private String gameName;
    /**
     * The username of the White player.
     */
    private String whiteUsername;
    /**
     * The username of the Black player.
     */
    private String blackUsername;
    /**
     * The ChessGame object (including board and pieces for the game).
     */
    private ChessGame game;

    /**
     * The observers of the game.
     */
    private ArrayList<String> observers;


    /**
     * Constructor for Game class.
     * @param gameID the gameID of the game.
     * @param gameName the gameName of the game.
     */
    public Game(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.game = new GameImpl();
        observers = new ArrayList<>();

    }

    /**
     * Constructor for Game class.
     * @param gameID the gameID of the game.
     * @param gameName the gameName of the game.
     * @param whiteUsername the username of the White player.
     * @param blackUsername the username of the Black player.
     * @param game the ChessGame object (including board and pieces for the game).
     */
    public Game(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;
        observers = new ArrayList<>();

    }

    /**
     * Gets the gameID of the game.
     * @return the gameID of the game.
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Gets the gameName of the game.
     * @return the gameName of the game.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Sets the gameName of the game.
     * @param gameName the gameName of the game.
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Gets the whiteUsername of the game.
     * @return the whiteUsername of the game.
     */
    public String getWhiteUsername() {
        return whiteUsername;
    }

    /**
     * Sets the whiteUsername of the game.
     * @param whiteUsername the whiteUsername of the game.
     */
    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    /**
     * Gets the blackUsername of the game.
     * @return the blackUsername of the game.
     */
    public String getBlackUsername() {
        return blackUsername;
    }

    /**
     * Sets the blackUsername of the game.
     * @param blackUsername the blackUsername of the game.
     */
    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    /**
     * Adds an observer to the game.
     * @param username of the player to observe the game.
     */

    public void addObserver(String username) {
        observers.add(username);
    }

    /**
     * Get list of observers from the game
     * @return an array of observers
     */
    public Object[] getObservers() {
        return observers.toArray();
    }

    /**
     * Gets the ChessGame object (including board and pieces for the game).
     * @return the chess game object.
     */
    public ChessGame getGame() {
        return game;
    }

}
