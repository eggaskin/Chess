package userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    private int gameID;
    private ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

}
