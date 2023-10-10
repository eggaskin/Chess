package chess;

import java.util.ArrayList;
import java.util.Collection;

public class King extends PieceImpl {
    private boolean hasCastled;
    public King(ChessGame.TeamColor color) {
        super(color,PieceType.KING);
        hasCastled = false;
    }

    public boolean hasCastled() {
        return hasCastled;
    }

    public void castle() {
        hasCastled = true;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition next = myPosition;
        ChessGame.TeamColor teamColor = super.getTeamColor();

        // king
        for (int i = -1;i<2;i++) {
            for (int j = -1;j<2;j++) {
                next = new PositionImpl(r + i, c + j);
                if (offBoard(next)) {
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

        // TODO: check if king is in check

        return moves;
    }
}
