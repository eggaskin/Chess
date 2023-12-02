package userCommands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
    private ChessMove move;

    public MoveCommand(String authToken, ChessMove move) {
        super(authToken);
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }
}
