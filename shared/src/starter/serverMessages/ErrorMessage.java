package serverMessages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String getMessage() {
        return this.errorMessage;
    }
}
