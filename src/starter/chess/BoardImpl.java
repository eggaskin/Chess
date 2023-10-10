package chess;

public class BoardImpl implements ChessBoard{
    private ChessPiece[][] board;

    public BoardImpl() {
        board = new ChessPiece[8][8];
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        // POSITIONS ARE 1-8, NOT 0-7
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
//        if (hasPiece(position)) {
//            return board[position.getRow()-1][position.getColumn()-1];
//        }
//        return null;
    }

    public void removePiece(ChessPosition position) {
        board[position.getRow()-1][position.getColumn()-1] = null;
    }

    public boolean hasPiece(ChessPosition pos) {
        return board[pos.getRow()-1][pos.getColumn()-1] != null;
    }

    @Override
    public void resetBoard() {
        for (int i = 0; i < board.length; i++) {
            board[1][i] = new Pawn(ChessGame.TeamColor.WHITE);
            board[6][i] = new Pawn(ChessGame.TeamColor.BLACK);
            board[2][i] = null;
            board[3][i] = null;
            board[4][i] = null;
            board[5][i] = null;
        }
        board[0][0] = new Rook(ChessGame.TeamColor.WHITE);
        board[0][7] = new Rook(ChessGame.TeamColor.WHITE);
        board[7][0] = new Rook(ChessGame.TeamColor.BLACK);
        board[7][7] = new Rook(ChessGame.TeamColor.BLACK);
        board[0][1] = new Knight(ChessGame.TeamColor.WHITE);
        board[0][6] = new Knight(ChessGame.TeamColor.WHITE);
        board[7][1] = new Knight(ChessGame.TeamColor.BLACK);
        board[7][6] = new Knight(ChessGame.TeamColor.BLACK);
        board[0][2] = new Bishop(ChessGame.TeamColor.WHITE);
        board[0][5] = new Bishop(ChessGame.TeamColor.WHITE);
        board[7][2] = new Bishop(ChessGame.TeamColor.BLACK);
        board[7][5] = new Bishop(ChessGame.TeamColor.BLACK);
        board[0][3] = new Queen(ChessGame.TeamColor.WHITE);
        board[7][3] = new Queen(ChessGame.TeamColor.BLACK);
        board[0][4] = new King(ChessGame.TeamColor.WHITE);
        board[7][4] = new King(ChessGame.TeamColor.BLACK);
    }
}
