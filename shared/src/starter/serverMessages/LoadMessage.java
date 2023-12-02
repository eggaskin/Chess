package serverMessages;

public class LoadMessage extends ServerMessage {
    private Object game;

    public LoadMessage(Object game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Object getGame() {
        return this.game;
    }
}
