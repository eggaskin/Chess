package comms;

import adapter.*;
import chess.*;

import java.util.Collection;
import java.util.Scanner;
import static ui.EscapeSequences.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import comms.GameHandler;
import comms.ServerFacade;
import comms.WSClient;
import serverMessages.*;

public class Client {
    public static boolean loggedIn = false;
    public static boolean inGame = false;
    private static boolean whiteOrient = true;
    public static GameImpl game;
    public static int gameID; //TODO: assign
    public static WSClient wsClient;

    public static void main(String[] args) throws Exception {
        // print welcome message and help
        System.out.println("Welcome to chess! Send 'help' to see possible commands.");

        // start websocket client
        try {
            GameHandler handler = new ActualGameHandler();
            wsClient = new WSClient(handler);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            // get user input
            System.out.printf("%s >>> ", loggedIn ? "WELCOME" : "LOGIN");
            Scanner scan = new Scanner(System.in);
            String line = scan.nextLine();
            String[] inputArr = line.split(" ");

            boolean status = false;

            try {
                if (loggedIn) {
                    if (inGame) {
                        status = handleInGame(inputArr);
                    } else {
                        status = handleGameReq(inputArr);
                    }
                } else {
                    status = handleReq(inputArr);
                }
                if (!status) {
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static class ActualGameHandler implements GameHandler {
        public void updateGame(String message) {
            LoadMessage loadMessage = new Gson().fromJson(message, LoadMessage.class);
            String gamestr = loadMessage.getGame();

            var builder = new GsonBuilder();
            builder.registerTypeAdapter(ChessBoard.class, new BoardAdapter());
            builder.registerTypeAdapter(ChessPiece.class, new PieceAdapter());

            game = builder.create().fromJson(gamestr, GameImpl.class);
            displayBoard(game.getBoard());
        }

        public void printMessage(String message) {
            System.out.println(message);
        }
    }

    private static boolean handleInGame(String[] inputArr) {
        String ingamehelp = """
                1. redraw to see game board 
                2. move <SQUARE1> <SQUARE2> to move 1 to 2 
                3. highlight to show legal moves 
                4. resign to forfeit/end game and not leave 
                5. leave to leave game 
                6. help to see possible commands \n""";

        // check if user wants help
        if (inputArr[0].equals("help")) {
            System.out.println(ingamehelp);
        }

        if (inputArr[0].equals("redraw")) {
            try {
                displayBoard(game.getBoard());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (inputArr[0].equals("move")) {
            if (inputArr.length != 3) {
                System.out.println("Invalid number of arguments. Try again.");
            } else {
                String pos1 = inputArr[1];
                String pos2 = inputArr[2];
                try {
                    ServerFacade.move(convertPos(pos1),convertPos(pos2),gameID);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (inputArr[0].equals("highlight")) {
            if (inputArr.length != 2) {
                System.out.println("Invalid number of arguments. Try again.");
            } else {
                try {
                    // get legal move
                    String pos = inputArr[1];
                    Collection<ChessMove> moves = game.validMoves(convertPos(pos));
                    highlightBoard(moves);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (inputArr[0].equals("resign")) {
            try {
                ServerFacade.resign(gameID);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (inputArr[0].equals("leave")) {
            try {
                ServerFacade.leave(gameID);
                inGame = false;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return true;
    }

    private static boolean handleGameReq(String[] inputArr) {
        String postloginhelp = """
                1. create <NAME> to create a game 
                2. list to see all available games 
                3. join <ID> <WHITE/BLACK> to join a game 
                4. observe <ID> to observe a game 
                5. logout to logout and return to prelogin 
                6. help to see possible commands \n""";

        // check if user wants help
        if (inputArr[0].equals("help")) {
            System.out.println(postloginhelp);
        }

        if (inputArr[0].equals("logout")) {
            try {
                ServerFacade.logout();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // check if user wants to create a game
        if (inputArr[0].equals("create")) {
            if (inputArr.length != 2) {
                System.out.println("Invalid number of arguments. Try again.");
            } else {
                String name = inputArr[1];
                try {
                    ServerFacade.createGame(name);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        // check if user wants to list games
        if (inputArr[0].equals("list")) {
            try {
                ServerFacade.listGames();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // check if user wants to join a game
        if (inputArr[0].equals("join")) {
            if (inputArr.length != 3) {
                System.out.println("Invalid number of arguments. Try again.");
            } else {
                String id = inputArr[1];
                // convert id string to int
                int idInt = Integer.parseInt(id);
                String color = inputArr[2];
                try {
                    ServerFacade.joinGame(color, idInt);
                    whiteOrient = color.equals("WHITE");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // check if user wants to observe a game
        if (inputArr[0].equals("observe")) {
            if (inputArr.length != 2) {
                System.out.println("Invalid number of arguments. Try again.");
            } else {
                String id = inputArr[1];
                // convert id string to int
                int idInt = Integer.parseInt(id);
                try {
                    ServerFacade.joinGame(null, idInt);
                } catch (Exception e) {
                    System.out.println(e.getMessage()+RESET_BG_COLOR);
                }
                ChessBoard board = new BoardImpl();
                board.resetBoard();
                displayBoard(board);
                System.out.println();
            }
        }

        return true;
    }

    private static boolean handleReq(String[] inputArr) {
        String preloginhelp = """
        1. register <USERNAME> <PASSWORD> <EMAIL> to make an account 
        2. login <USERNAME> <PASSWORD> to login and start playing 
        3. help to see possible commands 
        4. quit to exit the program \n""";

        // check if user wants to quit
        if (inputArr[0].equals("quit")) {
            System.out.println("Goodbye!");
            return false;
        }

        // check if user wants help
        if (inputArr[0].equals("help")) {
            System.out.println(preloginhelp);
        }

        // check if user wants to register
        if (inputArr[0].equals("register")) {
            if (inputArr.length != 4) {
                System.out.println("Invalid number of arguments. Try again.");
            } else {
                String username = inputArr[1];
                String password = inputArr[2];
                String email = inputArr[3];
                try {
                    ServerFacade.register(username, password, email);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // check if user wants to login
        if (inputArr[0].equals("login")) {
            if (inputArr.length != 3) {
                System.out.println("Invalid number of arguments. Try again.");
            } else {
                String username = inputArr[1];
                String password = inputArr[2];
                try {
                    ServerFacade.login(username, password);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        //check if input was invalid command and print help TODO:
        return true;
    }

    private static void highlightBoard(Collection<ChessMove> moves) {
        // get end positions of all possible moves
        String[][] boardArr = new String[8][8];
        ChessBoard board = game.getBoard();
        for (int i = 1; i<9; i++) {
            String[] row = new String[8];
            for (int j = 1; j<9; j++) {
                ChessPiece piece = board.getPiece(new PositionImpl(i,j));
                if (piece == null) {
                    row[j-1] = " ";
                } else {
                    row[j-1] = getPieceString(piece.getPieceType(),piece.getTeamColor());
                }
            }
            boardArr[i-1] = row;
        }
        for (ChessMove move : moves) {
            ChessPosition end = move.getEndPosition();
            int row = end.getRow();
            int col = end.getColumn();
            boardArr[row][col] = "X"+boardArr[row][col];
        }
        printBoard(boardArr, whiteOrient);
        System.out.println();
    }

    private static String rowtoString(String[] row, int rownum, boolean blackStart,boolean reverse) {
        String rowString = SET_BG_COLOR_DARK_GREEN + " " + rownum + " " + (blackStart ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY);
        boolean black = !blackStart;
        if (reverse) {
            for (int i = 7; i >= 0; i--) {
                String piece = row[i];
                if (piece.startsWith("X")) {
                    piece = piece.substring(1);
                    rowString += (piece.equals(" ") ? "   " :  piece );
                    rowString += black ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                    black = !black;

                } else {
                    rowString += (piece.equals(" ") ? "   " :  piece );
                    rowString += black ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;
                    black = !black;
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                String piece = row[i];
                // check if it begins with an X for highlighting, if so take it off
                if (piece.startsWith("X")) {
                    piece = piece.substring(1);
                    rowString += (piece.equals(" ") ? "   " :  piece );
                    rowString += black ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                    black = !black;

                } else {
                    rowString += (piece.equals(" ") ? "   " :  piece );
                    rowString += black ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY;
                    black = !black;
                }
            }
        }

        return rowString+SET_BG_COLOR_DARK_GREEN+ " " + rownum + " " + RESET_BG_COLOR;
    }

    private static void printBoard(String[][] board,boolean reverse) {
        String labels = "    a  b  c  d  e  f  g  h    ";
        String revlabels = "    h  g  f  e  d  c  b  a    ";
        if (reverse) {
            System.out.println(SET_BG_COLOR_DARK_GREEN+SET_TEXT_COLOR_WHITE+ revlabels+RESET_BG_COLOR);
            for (int i = 7; i>=0; i--) {
                System.out.println(rowtoString(board[i], i+1, i%2==0,true));
            }
            System.out.println(SET_BG_COLOR_DARK_GREEN+SET_TEXT_COLOR_WHITE+ revlabels+RESET_BG_COLOR);
        } else {
            System.out.println(SET_BG_COLOR_DARK_GREEN+SET_TEXT_COLOR_WHITE+ labels+RESET_BG_COLOR);
            for (int i = 0; i<8; i++) {
                System.out.println(rowtoString(board[i], 8-i, (8-i)%2==1,false));
            }
            System.out.println(SET_BG_COLOR_DARK_GREEN+SET_TEXT_COLOR_WHITE+ labels+RESET_BG_COLOR);
        }

    }

    private static String displayBoard(ChessBoard board) {
        String[][] boardArr = new String[8][8];
        for (int i = 1; i<9; i++) {
            String[] row = new String[8];
            for (int j = 1; j<9; j++) {
                ChessPiece piece = board.getPiece(new PositionImpl(i,j));
                if (piece == null) {
                    row[j-1] = " ";
                } else {
                    row[j-1] = getPieceString(piece.getPieceType(),piece.getTeamColor());
                }
            }
            boardArr[i-1] = row;
        }
        printBoard(boardArr, whiteOrient);
        System.out.println(RESET_BG_COLOR);
        return "";
    }

    private static ChessPosition convertPos(String pos) {
        int row = 8 - (pos.charAt(1) - '0');
        int col = pos.charAt(0) - 'a';
        return new PositionImpl(row, col);
        //TODO: test this
    }

    private static String getPieceString(ChessPiece.PieceType pieceType, ChessGame.TeamColor teamColor) {
        if (pieceType == ChessPiece.PieceType.KING) {
            return teamColor == ChessGame.TeamColor.WHITE ? " K " : " k ";
            //return teamColor == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
        } else if (pieceType == ChessPiece.PieceType.QUEEN) {
            return teamColor == ChessGame.TeamColor.WHITE ? " Q " : " q ";
            //return teamColor == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
        } else if (pieceType == ChessPiece.PieceType.BISHOP) {
            return teamColor == ChessGame.TeamColor.WHITE ? " B " : " b ";
            //return teamColor == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
        } else if (pieceType == ChessPiece.PieceType.KNIGHT) {
            return teamColor == ChessGame.TeamColor.WHITE ? " N " : " n ";
            //return teamColor == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
        } else if (pieceType == ChessPiece.PieceType.ROOK) {
            return teamColor == ChessGame.TeamColor.WHITE ? " R " : " r ";
            // return teamColor == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
        } else if (pieceType == ChessPiece.PieceType.PAWN) {
            return teamColor == ChessGame.TeamColor.WHITE ? " P " : " p ";
            //return teamColor == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        } else {
            return "   "; //EMPTY;
        }
    }
}
