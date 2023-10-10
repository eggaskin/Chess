package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook extends PieceImpl {
    private boolean hasCastled;

    public boolean hasCastled() {
        return hasCastled;
    }

    public void castle() {
        hasCastled = true;
    }
    public Rook(ChessGame.TeamColor color) {
        super(color,PieceType.ROOK);
        hasCastled = false;
    }

    // FIXME: if moved at ALL

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition next = myPosition;
        ChessGame.TeamColor teamColor = super.getTeamColor();

        // rook
        while (next.getRow() < 8) { // FIX THIS
            next = new PositionImpl(next.getRow() + 1, next.getColumn());
            if (board.hasPiece(next)) {
                if (board.getPiece(next).getTeamColor() != teamColor) {
                    moves.add(new MoveImpl(myPosition, next, null));
                }
                break;
            }
            moves.add(new MoveImpl(myPosition, next, null));
        }
        next = myPosition;
        while (next.getColumn() < 8) {
            next = new PositionImpl(next.getRow(), next.getColumn() + 1);
            if (board.hasPiece(next)) {
                if (board.getPiece(next).getTeamColor() != teamColor) {
                    moves.add(new MoveImpl(myPosition, next, null));
                }
                break;
            }
            moves.add(new MoveImpl(myPosition, next, null));
        }
        next = myPosition;
        while (next.getRow() > 1) {
            next = new PositionImpl(next.getRow() - 1, next.getColumn());
            if (board.hasPiece(next)) {
                if (board.getPiece(next).getTeamColor() != teamColor) {
                    moves.add(new MoveImpl(myPosition, next, null));
                }
                break;
            }
            moves.add(new MoveImpl(myPosition, next, null));
        }
        next = myPosition;
        while (next.getColumn()> 1) {
            next = new PositionImpl(next.getRow(), next.getColumn() - 1);
            if (board.hasPiece(next)) {
                if (board.getPiece(next).getTeamColor() != teamColor) {
                    moves.add(new MoveImpl(myPosition, next, null));
                }
                break;
            }
            moves.add(new MoveImpl(myPosition, next, null));
        }
        // go cols and rows back and forth until you hit a piece, etc.

        return moves;
    }
}
