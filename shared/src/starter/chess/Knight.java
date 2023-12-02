package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight extends PieceImpl {
    public Knight(ChessGame.TeamColor color) {
        super(color,PieceType.KNIGHT);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition next = myPosition;
        ChessGame.TeamColor teamColor = super.getTeamColor();

        // knight
        // do -2, 1, 2, -1, iterate over
        for (int i = -2;i<=2;i++) {
            if (i == 0) {
                continue;
            }
            for (int j = -2;j<=2;j++) {
                if ((j == 0) || (Math.abs(i) == Math.abs(j))) {
                    continue;
                }
                next = new PositionImpl(r + i, c + j);
                if (this.offBoard(next)) {
                    continue;
                }
                if (board.hasPiece(next)) {
                    if (board.getPiece(next).getTeamColor() != teamColor) {
                        moves.add(new MoveImpl(myPosition, next, null));
                    }
                } else {
                    moves.add(new MoveImpl(myPosition, next, null));
                }

            }
        }

        return moves;
    }
}
