package handlers;

import adapter.*;
import chess.*;
import com.google.gson.*;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import models.AuthToken;
import models.Game;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import serverMessages.*;
import userCommands.*;
import static services.Server.db;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WSHandler {

    private Map<Integer, Map<String,Session>> sessionMap = new HashMap<>();

    private void addSessionToGame(int gameID, String username, Session session) {
        if (!sessionMap.containsKey(gameID)) {
            Map<String, Session> newMap = new HashMap<>();
            newMap.put(username, session);
            sessionMap.put(gameID, newMap);
        }
        sessionMap.get(gameID).put(username, session);
    }

    private void removeUserFromGame(int gameID, String username) {
        if (sessionMap.containsKey(gameID)) {
            sessionMap.get(gameID).remove(username);
        }
    }

    private String getUsernameFromSession(Session session) {
        for (Map<String, Session> map : sessionMap.values()) {
            for (Map.Entry<String, Session> sesh : map.entrySet()) {
                if (sesh.getValue().equals(session)) {
                    return sesh.getKey();
                }
            }
        }
        return null;
    }

    private void removeSession(Session session) {
        for (Map<String, Session> map : sessionMap.values()) {
            if (map.containsValue(session)) {
                map.remove(session); // FIXME
            }
        }
    }

    private Map<String,Session> getSessionsFromGame(int gameID) {
        return sessionMap.get(gameID);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        // deserialize message to UserGameCommand
        System.out.printf("Received: %s", message);
        System.out.println();

        var body = new Gson().fromJson(message, UserGameCommand.class);
        switch (body.getCommandType()) {
            case JOIN_PLAYER -> { joinPlayer(session,new Gson().fromJson(message, JoinPlayerCommand.class)); }
            case JOIN_OBSERVER -> { joinObserver(session,new Gson().fromJson(message, IdCommand.class)); }
            case MAKE_MOVE -> {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(ChessPosition.class, new PosAdapter());
                gsonBuilder.registerTypeAdapter(ChessMove.class, new MoveAdapter());
                Gson gson = gsonBuilder.create();
                makeMove(session,gson.fromJson(message, MoveCommand.class)); }
            case LEAVE -> { leave(session,new Gson().fromJson(message, IdCommand.class)); }
            case RESIGN -> { resign(session,new Gson().fromJson(message, IdCommand.class)); }
        }

        // check auth token exists
        // check auth token is valid
        //String auth = body.getAuthString();
        //if (auth == null) {
        //    session.getRemote().sendString("Error: no auth token.");
        //    return;
        //}
    }

    private void joinPlayer(Session session, JoinPlayerCommand command) {
        // check if game exists
        // check if game is full
        // check if user is already in game
        // add user to game
        // send message
        // broadcast message
        Connection conn = null;
        try {
            GameDAO gameDAO = new GameDAO();
            conn = db.getConnection();
            AuthToken token = (new AuthDAO()).getAuthToken(conn, command.getAuthString());
            String username = token.getUsername();

            // check if game exists
            Game game = gameDAO.findGame(conn, command.getGameID());
            if (game == null) {
                ErrorMessage error = new ErrorMessage("Error: game does not exist");
                sendSessMessage(session, new Gson().toJson(error));
                return;
            }

            System.out.println(game.getWhiteUsername() + ", black: " + game.getBlackUsername());

            // check if wanted color is full
            if (command.getPlayerColor() == ChessGame.TeamColor.WHITE) {
                if (game.getWhiteUsername() != null && !game.getWhiteUsername().equals(username)) {
                    ErrorMessage error = new ErrorMessage("Error: white player already exists");
                    sendSessMessage(session, new Gson().toJson(error));
                    return;
                }
                if (game.getWhiteUsername() == null) {
                    ErrorMessage error = new ErrorMessage("Error: white player does not exist");
                    sendSessMessage(session, new Gson().toJson(error));
                    return;
                }
            } else if (command.getPlayerColor() == ChessGame.TeamColor.BLACK) {
                if (game.getBlackUsername() != null && !game.getBlackUsername().equals(username)) {
                    ErrorMessage error = new ErrorMessage("Error: black player already exists");
                    sendSessMessage(session, new Gson().toJson(error));
                    return;
                }
                if (game.getBlackUsername() == null) {
                    ErrorMessage error = new ErrorMessage("Error: black player does not exist");
                    sendSessMessage(session, new Gson().toJson(error));
                    return;
                }
            }

            addSessionToGame(command.getGameID(), username, session);


            String gamestr = gameDAO.findGameStr(conn, command.getGameID());
            System.out.println(sessionMap.toString());


            LoadMessage load = new LoadMessage(gamestr);
            sendMessage(command.getGameID(), new Gson().toJson(load), getUsernameFromSession(session));

            NotifMessage notif = new NotifMessage(username + " joined game as "+command.getPlayerColor().toString()+".");
            broadcastMessage(command.getGameID(), new Gson().toJson(notif), getUsernameFromSession(session));

        } catch (DataAccessException e) {
            System.out.println("Error: " + e.getMessage());
            ErrorMessage error = new ErrorMessage(e.getMessage());
            sendSessMessage(session, new Gson().toJson(error));
        } finally {
            db.returnConnection(conn);
        }

    }

    private void joinObserver(Session session, IdCommand command) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            AuthToken token = (new AuthDAO()).getAuthToken(conn, command.getAuthString());
            String username = token.getUsername();

            String gamestr = (new GameDAO()).findGameStr(conn, command.getGameID());
            addSessionToGame(command.getGameID(), username, session);

            LoadMessage load = new LoadMessage(gamestr);
            sendSessMessage(session, new Gson().toJson(load));

            NotifMessage notif = new NotifMessage(username + " joined game as observer.");
            broadcastMessage(command.getGameID(), new Gson().toJson(notif), getUsernameFromSession(session));

        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage());
            sendSessMessage(session, new Gson().toJson(error));
        } finally {
            db.returnConnection(conn);
        }

    }

    private void makeMove(Session session, MoveCommand command) {
        // check if game exists
        // check if user is in game
        // check if move is valid
        // make move
        // send message
        // broadcast message
        Connection conn = null;
        try {
            conn = db.getConnection();
            Game game = (new GameDAO()).findGame(conn, command.getGameID());
            ChessGame gameobj = game.getGame();
            System.out.println("assuming user is " + getUsernameFromSession(session));
            // check if player's turn
            ChessGame.TeamColor turnColor = gameobj.getTeamTurn();
            if (turnColor == ChessGame.TeamColor.WHITE && !game.getWhiteUsername().equals(getUsernameFromSession(session))) {
                ErrorMessage error = new ErrorMessage("Error: not your turn");
                sendMessage(command.getGameID(), new Gson().toJson(error), getUsernameFromSession(session));
                return;
            } else if (turnColor == ChessGame.TeamColor.BLACK && !game.getBlackUsername().equals(getUsernameFromSession(session))) {
                ErrorMessage error = new ErrorMessage("Error: not your turn");
                sendMessage(command.getGameID(), new Gson().toJson(error), getUsernameFromSession(session));
                return;
            }

            gameobj.makeMove(command.getMove());

            // check if in check or checkmate
            if (gameobj.isInCheck(turnColor)) {
                if (gameobj.isInCheckmate(turnColor)) {
                    NotifMessage notif = new NotifMessage(getUsernameFromSession(session) + " is in checkmate.");
                    broadcastMessage(command.getGameID(), new Gson().toJson(notif), getUsernameFromSession(session));
                    // resign
                    //resign(session, new IdCommand(UserGameCommand.CommandType.RESIGN, command.getAuthString(), command.getGameID()));
                    //return;
                } else {
                    // check
                    NotifMessage notif = new NotifMessage(getUsernameFromSession(session) + " is in check.");
                    broadcastMessage(command.getGameID(), new Gson().toJson(notif), getUsernameFromSession(session));
                }
            }

            gameobj.setTeamTurn((gameobj.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);

            game.setGame(gameobj);
            String gamestr = new Gson().toJson(game);
            (new GameDAO()).updateGame(conn, command.getGameID(),gamestr);

            LoadMessage load = new LoadMessage(gamestr);
            broadcastMessage(command.getGameID(), new Gson().toJson(load), getUsernameFromSession(session));

            NotifMessage notif = new NotifMessage("Player made move " + command.getMove().toString() + ".");
            broadcastMessage(command.getGameID(), new Gson().toJson(notif), getUsernameFromSession(session));

        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage());
            sendMessage(command.getGameID(), new Gson().toJson(error), getUsernameFromSession(session));
        } catch (InvalidMoveException e) {
            ErrorMessage error = new ErrorMessage("Error: invalid move");
            sendMessage(command.getGameID(), new Gson().toJson(error), getUsernameFromSession(session));
        } finally {
            db.returnConnection(conn);
        }

    }

    private void leave(Session session, IdCommand command) {
        Connection conn = null;
        try {
            conn = db.getConnection();
            // get user's color in their game
            Game game = (new GameDAO()).findGame(conn, command.getGameID());
            String color = (game.getWhiteUsername().equals(getUsernameFromSession(session))) ? "WHITE" : "BLACK";
            (new GameDAO()).claimSpot(conn, command.getGameID(),null,color);

            NotifMessage notif = new NotifMessage(getUsernameFromSession(session) + " left game.");
            broadcastMessage(command.getGameID(), new Gson().toJson(notif), getUsernameFromSession(session));

            removeUserFromGame(command.getGameID(), getUsernameFromSession(session));
        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage());
            sendMessage(command.getGameID(), new Gson().toJson(error), getUsernameFromSession(session));
        }finally {
            db.returnConnection(conn);
        }
    }

    private void resign(Session session, IdCommand command) {
        // check if game exists
        // check if user is in game
        // resign user
        // send message
        // broadcast message
        Connection conn = null;
        try {
            conn = db.getConnection();
            Game game = (new GameDAO()).findGame(conn, command.getGameID());
            String color = (game.getWhiteUsername().equals(getUsernameFromSession(session))) ? "WHITE" : "BLACK";

            // remove both users from game
            (new GameDAO()).claimSpot(conn, command.getGameID(),null,"WHITE");
            (new GameDAO()).claimSpot(conn, command.getGameID(),null,"BLACK");

            // add players as observers
            (new GameDAO()).observeGame(conn, command.getGameID(), game.getWhiteUsername());
            (new GameDAO()).observeGame(conn, command.getGameID(), game.getBlackUsername());

            // send message
            NotifMessage notif = new NotifMessage(getUsernameFromSession(session)+" resigned.");
            broadcastMessage(command.getGameID(), new Gson().toJson(notif), getUsernameFromSession(session));

        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage());
            sendMessage(command.getGameID(), new Gson().toJson(error), getUsernameFromSession(session));
        }finally {
            db.returnConnection(conn);
        }

    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        // add session and auth..
        System.out.println("Connected");
    }

    @OnWebSocketClose
    public void onClose(Session session, int i, String str) {
        removeSession(session);
        System.out.println("Closed");
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        System.out.println("WS Error: " + error.getMessage());
        System.out.println(Arrays.toString(error.getStackTrace()));
    }

    private void broadcastMessage(int gameID, String message, String userExcluded) {
        System.out.println("Broadcasting: " + message);
        for (Map.Entry<String,Session> sesh : getSessionsFromGame(gameID).entrySet()) {
            if (!sesh.getKey().equals(userExcluded)) {
                try {
                    System.out.println("Sending to: " + sesh.getKey());
                    sesh.getValue().getRemote().sendString(message);
                } catch (Exception e) {
                    System.out.println("Error in broadcast: " + e.toString());
                }
            }
        }
    }

    private void sendMessage(int gameID, String message, String username) {
        System.out.println("Sending: " + message);
        try {
            System.out.println(sessionMap);
            getSessionsFromGame(gameID).get(username).getRemote().sendString(message);
        } catch (Exception e) {
            System.out.println("Error in sending: " + e.toString());
        }
    }

    private void sendSessMessage(Session session, String message) {
        System.out.println("Sending: " + message);
        try {
            System.out.println(sessionMap);
            session.getRemote().sendString(message);
        } catch (Exception e) {
            System.out.println("Error in sending sess message: " + e.toString());
        }
    }

}
