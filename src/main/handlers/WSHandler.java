package handlers;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import req.CreateGameRequest;
import spark.Spark;
import userCommands.*;

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

    private void removeSessionFromGame(int gameID, String username) {
        if (sessionMap.containsKey(gameID)) {
            sessionMap.get(gameID).remove(username);
        }
    }

    private void removeSessionFromGame(Session session) {
        for (Map<String, Session> map : sessionMap.values()) {
            if (map.containsValue(session)) {
                map.remove(session);
            }
        }
    }

    private Map<String,Session> getSessionsFromGame(int gameID) {
        return sessionMap.get(gameID);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        // deserialize message to UserGameCommand
        var body = new Gson().fromJson(message, UserGameCommand.class);
        switch (body.getCommandType()) {
            case JOIN_PLAYER -> { joinPlayer(new Gson().fromJson(message, JoinPlayerCommand.class)); }
            case JOIN_OBSERVER -> { joinObserver(new Gson().fromJson(message, IdCommand.class)); }
            case MAKE_MOVE -> { makeMove(new Gson().fromJson(message, MoveCommand.class)); }
            case LEAVE -> { leave(new Gson().fromJson(message, IdCommand.class)); }
            case RESIGN -> { resign(new Gson().fromJson(message, IdCommand.class)); }
        }

        // check auth token exists
        // check auth token is valid
        String auth = body.getAuthString();
        if (auth == null) {
            session.getRemote().sendString("Error: no auth token.");
            return;
        }

        // check fields
        // call appropriate handler
        // create server command and serialize
        // send back to client
        System.out.printf("Received: %s", message);
        session.getRemote().sendString("WebSocket response: " + message);
    }

    private void joinPlayer(JoinPlayerCommand command) {
        // check if game exists
        // check if game is full
        // check if user is already in game
        // add user to game
        // send message
        // broadcast message
    }

    private void joinObserver(IdCommand command) {
        // check if game exists
        // check if user is already in game
        // add user to game
        // send message
        // broadcast message
    }

    private void makeMove(MoveCommand command) {
        // check if game exists
        // check if user is in game
        // check if move is valid
        // make move
        // send message
        // broadcast message
    }

    private void leave(IdCommand command) {
        // check if game exists
        // check if user is in game
        // remove user from game
        // send message
        // broadcast message
    }

    private void resign(IdCommand command) {
        // check if game exists
        // check if user is in game
        // resign user
        // send message
        // broadcast message
    }


    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("Connected");
    }

    @OnWebSocketClose
    public void onClose(Session session) {
        System.out.println("Closed");
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        System.out.println("Error");
    }


    private void broadcastMessage(int gameID, String message, String userExcluded) {
        for (Map.Entry<String,Session> sesh : getSessionsFromGame(gameID).entrySet()) {
            if (!sesh.getKey().equals(userExcluded)) {
                try {
                    sesh.getValue().getRemote().sendString(message);
                } catch (Exception e) {
                    System.out.println("Error: " + e.toString());
                }
            }
        }
    }

    private void sendMessage(int gameID, String message, String username) {
        try {
            getSessionsFromGame(gameID).get(username).getRemote().sendString(message);
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }


}
