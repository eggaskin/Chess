package res;

/**
 * The response from the server after a join game request.
 */
public class JoinGameResponse  extends MessageResponse {

    /**
     * Creates a new JoinGameResponse object.
     * @param message the message to display
     */
    public JoinGameResponse(String message) {
        super(message);
    }

    /**
     * Creates a new JoinGameResponse object.
     */
    public JoinGameResponse() {}

}
