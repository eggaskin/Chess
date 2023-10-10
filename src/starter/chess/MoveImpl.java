package chess;

import java.util.Objects;

public class MoveImpl implements ChessMove {
    private ChessPosition from;
    private ChessPosition to;
    // private ChessPiece piece;

    private ChessPiece.PieceType promotionPiece;
    // private ChessPiece capPiece;

    public MoveImpl(ChessPosition from, ChessPosition to, ChessPiece.PieceType promotedPiece) {
        this.from = from;
        this.to = to;
        this.promotionPiece = promotedPiece;
        // this.capPiece = null;
    }

    @Override
    public ChessPosition getStartPosition() {
        return from;
    }

    @Override
    public ChessPosition getEndPosition() {
        return to;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }


//    @Override
//    public ChessPiece getPiece() {
//        return piece;
//    }

//    @Override
//    public ChessPiece getCapturedPiece() {
//        return capPiece;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveImpl move = (MoveImpl) o;
        return Objects.equals(from, move.from) && Objects.equals(to, move.to) && promotionPiece == move.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, promotionPiece);
    }

    public String toString() {
        return from + " to " + to;
    }

//    @Override
//    public String toString() {
//        return piece + " from " + from + " to " + to + ", captured " + capPiece;
//    }

}
