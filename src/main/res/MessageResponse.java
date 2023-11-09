package res;

/**
 * A generic response body for error messages.
 */
public class MessageResponse {
    /**
     * The message of the response, an error or success.
     */
    private String message;

    /**
     * Creates a new MessageResponse object.
     */
    public MessageResponse() {}

    /**
     * Creates a new MessageResponse object.
     * @param message the message of the response.
     */
    public MessageResponse(String message) {
        this.message = message;
    }

    /**
     * Gets the message of the response.
     * @return the message of the response.
     */
    public String getMessage() {
        return message;
    }

}
