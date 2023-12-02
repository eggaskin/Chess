package serverMessages;

public class NotifMessage extends ServerMessage {
    private String message;

    public NotifMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
