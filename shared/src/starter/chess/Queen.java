package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen extends PieceImpl {
    public Queen(ChessGame.TeamColor color) {
        super(color,PieceType.QUEEN);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition next = myPosition;
        ChessGame.TeamColor teamColor = super.getTeamColor();

        // add bishop, rook moves
        // bishop
        for (int i = -1;i<2;i+=2) {
            for (int j = -1;j<2;j+=2) {
                next = myPosition;
                while (next.getColumn() <= 8 && next.getRow() <= 8 && next.getColumn() > 0 && next.getRow() > 0) {
                    next = new PositionImpl(next.getRow() + i, next.getColumn() + j);
                    if (offBoard(next)) {
                        // could consolidate this later.
                        break;
                    }
                    if (board.hasPiece(next)) {
                        if (board.getPiece(next).getTeamColor() != teamColor) {
                            moves.add(new MoveImpl(myPosition, next, null));
                        }
                        break;
                    }
                    moves.add(new MoveImpl(myPosition, next, null));
                }
            }
        }
        // rook
        next = myPosition;
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

        // rook TODO: make this a function


        return moves;
    }
}
