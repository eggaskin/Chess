package userCommands;

public class IdCommand extends UserGameCommand {
    private int gameID;

    public IdCommand(CommandType commandType, String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = commandType;
    }

    public int getGameID() {
        return gameID;
    }
}
