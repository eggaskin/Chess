package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn extends PieceImpl {
    public Pawn(ChessGame.TeamColor color) {
        super(color,PieceType.PAWN);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int r = myPosition.getRow();
        int c = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition next = myPosition;
        ChessGame.TeamColor teamColor = super.getTeamColor();

        // pawn
        int firstrow = 7;
        int lastrow = 1;
        int direction = -1;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            firstrow = 2;
            lastrow = 8;
            direction = 1;
        }

        if (r == firstrow) {
            next = new PositionImpl(r + 2 * direction, c);
            if (!board.hasPiece(next) && !board.hasPiece(new PositionImpl(r + direction, c))) {
                moves.add(new MoveImpl(myPosition, next,  null));
            }
        }

        // TODO: could simplify
        // normal movement
        next = new PositionImpl(r + direction, c);
        if (r == lastrow- direction) {
            if (!board.hasPiece(next)) {
                moves.add(new MoveImpl(myPosition, next,  PieceType.QUEEN));
                moves.add(new MoveImpl(myPosition, next,  PieceType.KNIGHT));
                moves.add(new MoveImpl(myPosition, next,  PieceType.BISHOP));
                moves.add(new MoveImpl(myPosition, next,  PieceType.ROOK));
            }
        } else {
            if (!offBoard(next) && !board.hasPiece(next)) {
                moves.add(new MoveImpl(myPosition, next, null));
            }
        }

        // capturing diagonally
        for (int i = -1;i<2;i+=2) {
            next = new PositionImpl(r + direction, c + i);
            if (r == lastrow-direction && board.hasPiece(next)) {
                if (board.getPiece(next).getTeamColor() != teamColor) {
                    moves.add(new MoveImpl(myPosition, next, PieceType.QUEEN));
                    moves.add(new MoveImpl(myPosition, next, PieceType.KNIGHT));
                    moves.add(new MoveImpl(myPosition, next, PieceType.BISHOP));
                    moves.add(new MoveImpl(myPosition, next, PieceType.ROOK));
                }
            }
            else {
                if (!offBoard(next) && board.hasPiece(next)) {
                    if (board.getPiece(next).getTeamColor() != teamColor) {
                        moves.add(new MoveImpl(myPosition, next, null));
                    }
                }
            }
        }

        return moves;
    }

}
