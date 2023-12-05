package adapter;

import chess.ChessGame;
import chess.ChessPiece;
import chess.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;

public class PieceAdapter implements JsonDeserializer<ChessPiece> {
    public ChessPiece deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        // get pieceType and teamColor from jsonElement
        JsonObject obj = jsonElement.getAsJsonObject();
        String pieceType = obj.get("pieceType").getAsString();
        String teamColor = obj.get("teamColor").getAsString();

        switch (pieceType) {
            case "PAWN" -> {
                if (teamColor.equals("WHITE")) {
                    return new Pawn(ChessGame.TeamColor.WHITE);
                } else {
                    return new Pawn(ChessGame.TeamColor.BLACK);
                }
            }
            case "ROOK" -> {
                if (teamColor.equals("WHITE")) {
                    return new Rook(ChessGame.TeamColor.WHITE);
                } else {
                    return new Rook(ChessGame.TeamColor.BLACK);
                }
            }
            case "KNIGHT" -> {
                if (teamColor.equals("WHITE")) {
                    return new Knight(ChessGame.TeamColor.WHITE);
                } else {
                    return new Knight(ChessGame.TeamColor.BLACK);
                }
            }
            case "BISHOP" -> {
                if (teamColor.equals("WHITE")) {
                    return new Bishop(ChessGame.TeamColor.WHITE);
                } else {
                    return new Bishop(ChessGame.TeamColor.BLACK);
                }
            }
            case "QUEEN" -> {
                if (teamColor.equals("WHITE")) {
                    return new Queen(ChessGame.TeamColor.WHITE);
                } else {
                    return new Queen(ChessGame.TeamColor.BLACK);
                }
            }
            case "KING" -> {
                if (teamColor.equals("WHITE")) {
                    return new King(ChessGame.TeamColor.WHITE);
                } else {
                    return new King(ChessGame.TeamColor.BLACK);
                }
            }
        }
        return null;
    }
}

