package dataAccess;

import chess.*;
import com.google.gson.*;
import models.Game;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Objects;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * GameDAO class.
 * Accesses the Game table in the database.
 */
public class GameDAO {

    class BoardAdapter implements JsonDeserializer<ChessBoard> {
        public ChessBoard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Gson().fromJson(jsonElement, BoardImpl.class);
        }
    }

    class PieceAdapter implements JsonDeserializer<ChessPiece> {
        public ChessPiece deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Gson().fromJson(jsonElement, PieceImpl.class);
        }
    }

    /**
     * Creates a new GameDAO object.
     * @throws DataAccessException
     */
    public GameDAO() throws DataAccessException{}

    /**
     * Creates a new GameDAO object.
     * @param g the game to be stored in the database.
     * @throws DataAccessException
     */
    public void createGame(Connection conn, Game g) throws DataAccessException {
        if (gameExists(conn,g.getGameID())) {
            throw new DataAccessException("Error: already taken");
        }
        // insert game into database
        try (var preparedStatement = conn.prepareStatement("INSERT INTO games (gameid, gamename, game) VALUES (?, ?,?)")) {
            preparedStatement.setInt(1, g.getGameID());
            preparedStatement.setString(2, g.getGameName());

            var json = new Gson().toJson(g.getGame());
            preparedStatement.setString(3, json);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Create Game: "+ ex.toString());
        }
    }

    /**
     * Finds a game in the database.
     * @param gameID the gameID of the game to be found.
     * @return the game with the given gameID.
     * @throws DataAccessException
     */
    public Game findGame(Connection conn, int gameID) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT gameid,gamename,white,black,game FROM games WHERE gameid=?")) {
            preparedStatement.setInt(1, gameID);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: game not found");
                }
                var gameid = rs.getInt("gameid");
                var gamename = rs.getString("gamename");
                var white = rs.getString("white");
                var black = rs.getString("black");

                var gamestr = rs.getString("game");
                var builder = new GsonBuilder();
                builder.registerTypeAdapter(ChessBoard.class, new BoardAdapter());
                builder.registerTypeAdapter(ChessPiece.class, new PieceAdapter());

                GameImpl gameobj = builder.create().fromJson(gamestr, GameImpl.class);

                return new Game(gameid, gamename, white, black, gameobj);

            }
        } catch (SQLException ex) {
            throw new DataAccessException("Find Game: " +ex.toString());
        }
    }

    /**
     * Checks if a game exists in the database.
     * @param conn
     * @param gameid
     * @return if the game exists.
     * @throws DataAccessException
     */
    private boolean gameExists(Connection conn, int gameid) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT gamename FROM games WHERE gameid=?")) {
            preparedStatement.setInt(1, gameid);
            try (var rs = preparedStatement.executeQuery()) {
                return rs.next();

            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Deletes a game from the database.
     * @param conn the connection to the database.
     * @param gameID
     * @throws DataAccessException
     */
    public void removeGame(Connection conn, int gameID) throws DataAccessException {
        if (!gameExists(conn, gameID)) {
            throw new DataAccessException("Error: game not found");
        }
        try (var preparedStatement = conn.prepareStatement("DELETE FROM games WHERE gameid=?")) {
            preparedStatement.setInt(1, gameID);
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }


    /**
     * Finds all games in the database.
     * @return an array of all games in the database.
     * @throws DataAccessException
     */
    public Object[] findAllGames(Connection conn) throws DataAccessException {
        LinkedList<Game> games = new LinkedList<>();
        try (var preparedStatement = conn.prepareStatement("SELECT gameid, white, black, gamename FROM games")) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    var gameid = rs.getInt("gameid");
                    var white = rs.getString("white");
                    var black = rs.getString("black");
                    var gamename = rs.getString("gamename");

                    games.add(new Game(gameid, gamename, white, black, new GameImpl()));
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
        return games.toArray();
    }

    /**
     * Updates a game in the database.
     * @param gameID the game to be updated.
     * @param chessGame the new chessGame string.
     * @throws DataAccessException
     */
    public void updateGame(Connection conn, int gameID, String chessGame) throws DataAccessException {
        if (!gameExists(conn, gameID)) {
            throw new DataAccessException("Error: game not found");
        }
        try (var preparedStatement = conn.prepareStatement("UPDATE games SET gamename=? WHERE gameid=?")) {
            preparedStatement.setString(1, chessGame);
            preparedStatement.setInt(2, gameID);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Clears all data from the database.
     * @throws DataAccessException
     */
    public void clear(Connection conn) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("TRUNCATE games")) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Claims a spot for a player in a game.
     * @param gameID the gameID of the game to be updated.
     * @param username the username of the player to be added to the game.
     * @param playerColor the color of the player to be added to the game.
     * @throws DataAccessException
     */
    public void claimSpot(Connection conn, int gameID, String username, String playerColor) throws DataAccessException {
        Game g = findGame(conn, gameID);
        if (g == null) {
            throw new DataAccessException("Error: game not found");
        }
        String white = g.getWhiteUsername();
        String black = g.getBlackUsername();
        // (in case there are already  users in game

        try (var preparedStatement = conn.prepareStatement("UPDATE games SET white=?,black=? WHERE gameid=?")) {
            if (Objects.equals(playerColor, "WHITE")) {
                if (g.getWhiteUsername() != null) {
                    throw new DataAccessException("Error: already taken");
                }
                white = username;
            }
            else {
                if (g.getBlackUsername() != null) {
                    throw new DataAccessException("Error: already taken");
                }
                black = username;

            }
            preparedStatement.setString(1, white);
            preparedStatement.setString(2, black);

            preparedStatement.setInt(3, gameID);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }

    /**
     * Add a user to the observers of a game.
     * @param gameID the gameID of the game to be observed.
     * @param username the username of the to-be observer.
     * @throws DataAccessException
     */
    public void observeGame(Connection conn, int gameID, String username) throws DataAccessException {
        if (!gameExists(conn, gameID)) {
            throw new DataAccessException("Error: game not found");
        }

        try (var preparedStatement = conn.prepareStatement("INSERT INTO observers (observer, gameid) VALUES (?,?)")) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.toString());
        }
    }
}
