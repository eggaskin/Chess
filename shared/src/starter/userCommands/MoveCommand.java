package userCommands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
    private ChessMove move;
    private int gameID;

    public MoveCommand(String authToken, ChessMove move, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public ChessMove getMove() {
        return move;
    }

    public int getGameID() {
        return gameID;
    }
}
