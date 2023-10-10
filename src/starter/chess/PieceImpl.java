package chess;

import java.util.Collection;

public abstract class PieceImpl implements ChessPiece {
    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;

    public PieceImpl(ChessGame.TeamColor teamColor, PieceType pieceType) {
        this.teamColor = teamColor;
        this.pieceType = pieceType;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return pieceType;
    }

    public boolean offBoard(ChessPosition pos) {
        int r = pos.getRow();
        int c = pos.getColumn();
        return (r < 1 || r > 8 || c < 1 || c > 8);
    }

    @Override
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    // FIXME: add isValidMove

    // can have interface inside class, that you implement when you make the object
    // in line essentially, etc new Speaker() { string sayhello() { return "hello"; } }
    // can also have interface inside method, that you implement when you call the method
}
