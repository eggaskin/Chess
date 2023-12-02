package services;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.Game;
import req.CreateGameRequest;
import res.CreateGameResponse;

import java.sql.Connection;
import java.util.UUID;

import static services.Server.db;


/**
 * A class to handle the CreateGame request.
 */
public class CreateGameService {

    /**
     * The next game ID to be used.
     */
    private static int nextGameID = 2;

    /**
     * Creates a game.
     * @param req The request to create a game.
     * @return The response to the request.
     */
    public CreateGameResponse createGame(CreateGameRequest req) {
        Connection conn = null;
        // check request validity
        if (req.getGameName() == null) {
            return new CreateGameResponse("Error: bad request");
        }

        // create game
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        nextGameID = Integer.parseInt(str);

        Game game = new Game(nextGameID, req.getGameName());


        //nextGameID++;

        // try to add game to database
        try {
            conn = Server.db.getConnection();
            System.out.println(game.getGameID());
            (new GameDAO()).createGame(conn, game);
            return new CreateGameResponse(game.getGameID());
        } catch (DataAccessException e) {
            return new CreateGameResponse(e.getMessage());
        } finally {
            db.returnConnection(conn);
        }

    }

}
