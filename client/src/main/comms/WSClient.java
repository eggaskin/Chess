package comms;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import serverMessages.*;
import userCommands.*;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint implements MessageHandler.Whole<String> {
    public Session session;
    private GameHandler gameHandler;

    public WSClient() throws Exception {
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }

    private void connect() throws Exception {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    private void disconnect() {
        try {
            this.session.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    public void joinGame(String auth, int gameID, ChessGame.TeamColor color) throws Exception {
        JoinPlayerCommand command = new JoinPlayerCommand(auth, gameID, color);
        String message = new Gson().toJson(command);
        this.send(message);
    }

    public void joinObserver(String auth, int gameID) throws Exception {
        IdCommand command = new IdCommand(UserGameCommand.CommandType.JOIN_OBSERVER, auth, gameID);
        String message = new Gson().toJson(command);
        this.send(message);
    }

    public void makeMove(String auth, ChessMove move) throws Exception {
        MoveCommand command = new MoveCommand(auth, move);
        String message = new Gson().toJson(command);
        this.send(message);
    }

    public void leave(String auth, int id) throws Exception {
        IdCommand command = new IdCommand(UserGameCommand.CommandType.LEAVE,auth, id);
        String message = new Gson().toJson(command);
        this.send(message);
    }

    public void resign(String auth, int id) throws Exception {
        IdCommand command = new IdCommand(UserGameCommand.CommandType.RESIGN,auth, id);
        String message = new Gson().toJson(command);
        this.send(message);
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onMessage(String message) {
        // deserialize message to ServerMessage
        // pass to gameHandler
        var body = new Gson().fromJson(message, ServerMessage.class);
        switch (body.getServerMessageType()) {
            case LOAD_GAME -> { gameHandler.updateGame(message); }
            case ERROR -> { gameHandler.printMessage(new Gson().fromJson(message, ErrorMessage.class).getMessage()); }
            case NOTIFICATION -> { gameHandler.printMessage(new Gson().fromJson(message, NotifMessage.class).getMessage()); }
        }
    }

    @OnWebSocketClose
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    public void onClose(Session session, CloseReason closeReason) {
    }
    public void onError(Session session, Throwable throwable) {
    }
}
