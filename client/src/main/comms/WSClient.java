package comms;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import serverMessages.*;
import userCommands.*;

import javax.websocket.*;
import java.net.URI;

public class WSClient extends Endpoint {
    public Session session = null;
    private GameHandler gameHandler;
    private String loc = "ws://localhost:8080/connect";

    public WSClient(GameHandler handler) throws Exception {
        this.gameHandler = handler;
    }

    public void connect() throws Exception {
        URI uri = new URI(loc);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        System.out.println("Connected to server");
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println("Received message: " + message);
                // deserialize message to ServerMessage
                // pass to gameHandler
                var body = new Gson().fromJson(message, ServerMessage.class);
                switch (body.getServerMessageType()) {
                    case LOAD_GAME -> { gameHandler.updateGame(message); }
                    case ERROR -> { gameHandler.printMessage(new Gson().fromJson(message, ErrorMessage.class).getMessage()); }
                    case NOTIFICATION -> { gameHandler.printMessage(new Gson().fromJson(message, NotifMessage.class).getMessage()); }
                }
            }
        });
    }

    public void disconnect() {
        try {
            this.session.close();
            session = null;
            System.out.println("Disconnected");

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    public void joinGame(String auth, int gameID, String color) throws Exception {
        System.out.println("Joining game");

        ChessGame.TeamColor teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        JoinPlayerCommand command = new JoinPlayerCommand(auth, gameID, teamColor);
        String message = new Gson().toJson(command);
        this.send(message);
    }

    public void joinObserver(String auth, int gameID) throws Exception {
        IdCommand command = new IdCommand(UserGameCommand.CommandType.JOIN_OBSERVER, auth, gameID);
        String message = new Gson().toJson(command);
        this.send(message);
    }

    public void makeMove(String auth, ChessMove move,int id) throws Exception {
        MoveCommand command = new MoveCommand(auth, move, id);
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
        System.out.println("Sending message: " + msg);
        this.session.getAsyncRemote().sendText(msg);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }
    @OnError
    public void onError(Session session, Throwable throwable) {
    }
}
