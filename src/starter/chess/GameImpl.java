package chess;

import java.util.Collection;
import java.util.ArrayList;

public class GameImpl implements ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public GameImpl() {
        board = new BoardImpl();
        teamTurn = TeamColor.WHITE;
    }

    @Override
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        // check things
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : moves) {
            // make move
            ChessPosition end = move.getEndPosition();
            ChessPiece endPiece = board.getPiece(end);
            board.removePiece(startPosition);
            if (move.getPromotionPiece() != null) {
                // switch piece type...
                ChessPiece.PieceType type = move.getPromotionPiece();
                piece = promPiece(type, piece.getTeamColor());
            }
            board.addPiece(end, piece);
            // check if board is in check
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            // undo move
            board.removePiece(end);
            board.addPiece(startPosition, piece);
            if (endPiece != null) {
                board.addPiece(end, endPiece);
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (canCastle(piece.getTeamColor(),false)) {
                // kingside
                validMoves.add(new MoveImpl(startPosition, new PositionImpl(startPosition.getRow(), startPosition.getColumn() + 2),null));
            }
            if (canCastle(piece.getTeamColor(),true)) {
                // queenside
                validMoves.add(new MoveImpl(startPosition, new PositionImpl(startPosition.getRow(), startPosition.getColumn() - 2),null));
            }
        }

        return validMoves;
    }

    public boolean canCastle(TeamColor color, boolean queenside) {
        // check if king and rook have moved
        // check if pieces between king and rook
        // check if king is in check
        // check if king and rook will be safe after move
        ChessPosition kingPos = (color == TeamColor.WHITE) ? new PositionImpl(1,5) : new PositionImpl(8,5);
        ChessPosition rookPos = (color == TeamColor.WHITE) ? new PositionImpl(1,8) : new PositionImpl(8,8);
        if (queenside) {
            rookPos = (color == TeamColor.WHITE) ? new PositionImpl(1,1) : new PositionImpl(8,1);
        }

        ChessPiece king = board.getPiece(kingPos);
        ChessPiece rook = board.getPiece(rookPos);

        if (king == null || (rook == null)) {
            return false;
        }
        if (king.getPieceType() != ChessPiece.PieceType.KING || (rook.getPieceType() != ChessPiece.PieceType.ROOK)) {
            return false;
        }
        if (king.getTeamColor() != color || (rook.getTeamColor() != color)) {
            return false;
        }
        if (((King) king).hasCastled()) {
            return false;
        }
        if (((Rook) rook).hasCastled()) {
            return false;
        }

        int direction = (queenside) ? -1 : 1;
        ChessPosition pos = new PositionImpl(kingPos.getRow(), kingPos.getColumn() + 1*direction);
        while (pos.getColumn() != ((queenside) ? kingPos.getColumn()-3 : rookPos.getColumn())) {
            if (board.hasPiece(pos)) {
                return false;
            }
            pos = new PositionImpl(pos.getRow(), pos.getColumn() + direction);
        }
        if (isInCheck(color)) {
            return false;
        }

        // check if rook can be captured afterwards
        direction = (queenside) ? 3 : -2;
        ChessPosition rookEnd = new PositionImpl(rookPos.getRow(), rookPos.getColumn() + direction);
        Collection<ChessMove> moves = allMoves((color == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE, false);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(rookEnd)) {
                return false;
            }
        }

        // check if king is in check afterwards
        // make move
        direction = (queenside) ? -1 : 1;
        ChessPosition kingEnd = new PositionImpl(kingPos.getRow(), kingPos.getColumn() + 2 * direction);
        board.removePiece(kingPos);
        board.addPiece(kingEnd, new King(color));
        // check if board is in check
        if (isInCheck(color)) {
            return false;
        }
        // undo move
        board.removePiece(kingEnd);
        board.addPiece(kingPos, new King(color));
        return true;
    }

    public ChessPiece promPiece(ChessPiece.PieceType type, TeamColor color) {
        if (type == ChessPiece.PieceType.QUEEN) {
            return new Queen(color);
        } else if (type == ChessPiece.PieceType.ROOK) {
            return new Rook(color);
        } else if (type == ChessPiece.PieceType.BISHOP) {
            return new Bishop(color);
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            return new Knight(color);
        }
        return null;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        Collection<ChessMove> validMoves = validMoves(start);
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Out of turn");
        }

        board.removePiece(start);
        if (move.getPromotionPiece() != null) {
            // switch piece type...
            ChessPiece.PieceType type = move.getPromotionPiece();
            piece = promPiece(type, piece.getTeamColor());
        }
        // check if castle
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (Math.abs(start.getColumn() - end.getColumn()) == 2) {
                ((King) piece).castle();
                // rook movement
                // ALWAYS MOVING --> for kingside, other for queen
                if ((start.getColumn() - end.getColumn()) > 0) { // queenside
                    ChessPosition rookPos = (piece.getTeamColor() == TeamColor.WHITE) ? new PositionImpl(1,1) : new PositionImpl(8,1);
                    ChessPiece rook = board.getPiece(rookPos);
                    board.removePiece(rookPos);
                    board.addPiece(new PositionImpl(rookPos.getRow(), rookPos.getColumn() +3), rook);
                } else {  //  kingside
                    ChessPosition rookPos = (piece.getTeamColor() == TeamColor.WHITE) ? new PositionImpl(1,8) : new PositionImpl(8,8);
                    ChessPiece rook = board.getPiece(rookPos);
                    board.removePiece(rookPos);
                    board.addPiece(new PositionImpl(rookPos.getRow(), rookPos.getColumn() -2), rook);
                }
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            ((Rook) piece).castle();
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            ((King) piece).castle();
        }
        board.addPiece(end, piece);

        setTeamTurn((teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE);
    }

//    public class KingPieces<ChessPosition,Collection> {
//        public final ChessPosition kingPos;
//        public final Collection moves;
//
//        public KingPieces(ChessPosition a, Collection b) {
//            this.kingPos = a;
//            this.moves = b;
//        }
//    };

//    public Object[] kingPieces(TeamColor teamColor) {
//
//        return new Object[]{kingPos, moves};
//        // return new KingPieces(kingPos, moves);
//    }

    public Collection<ChessMove> allMoves(TeamColor teamColor, boolean valid) {
        Collection<ChessMove> moves = new ArrayList<>();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                if (board.hasPiece(new PositionImpl(r,c))) {
                    ChessPiece piece = board.getPiece(new PositionImpl(r,c));
                    if (piece.getTeamColor() == teamColor) {
                        if (valid)  {
                            moves.addAll(validMoves(new PositionImpl(r,c)));
                        } else {
                            moves.addAll(piece.pieceMoves(board, new PositionImpl(r,c)));
                        }
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        // check if any other team's pieces can capture king
        // get king position
        ChessPosition kingPos = null;
        Collection<ChessMove> moves = new ArrayList<>();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPiece piece = board.getPiece(new PositionImpl(r,c));
                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        kingPos = new PositionImpl(r,c);
                    }
                    else if (piece.getTeamColor() != teamColor) {
                        moves.addAll(piece.pieceMoves(board, new PositionImpl(r,c)));
                    }
                }
            }
        }

        // check if any of those pieces can capture king
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        // get team's king and piece positions on board
        ChessPosition kingPos = null;
        Collection<ChessPosition> pieces = new ArrayList<>();
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPiece piece = board.getPiece(new PositionImpl(r,c));
                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        kingPos = new PositionImpl(r,c);
                    }
                    else if (piece.getTeamColor() == teamColor) {
                        pieces.add(new PositionImpl(r,c));
                    }
                }
            }
        }
        if (!validMoves(kingPos).isEmpty()) {
            return false;
        }
        for (ChessPosition pos : pieces) {
            if (!validMoves(pos).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        // kingpieces on other team color
        Collection<ChessMove> moves = allMoves(teamColor, true);
        // if no moves, then stalemate
        return moves.isEmpty();
    }

    @Override
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }
}
