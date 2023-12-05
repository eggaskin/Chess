package serverMessages;

public class LoadMessage extends ServerMessage {
    private String game;

    public LoadMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String getGame() {
        return this.game;
    }
}
