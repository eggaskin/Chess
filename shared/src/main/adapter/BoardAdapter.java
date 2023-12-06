package adapter;

import chess.BoardImpl;
import chess.ChessBoard;
import chess.ChessPiece;
import com.google.gson.*;

import java.lang.reflect.Type;

public class BoardAdapter implements JsonDeserializer<ChessBoard> {
    public ChessBoard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPiece.class, new PieceAdapter());

        //return builder.create().fromJson(jsonElement, BoardImpl.class);
        return ctx.deserialize(jsonElement, BoardImpl.class);
    }
}
