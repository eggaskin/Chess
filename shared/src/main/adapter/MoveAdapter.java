package adapter;

import chess.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class MoveAdapter implements JsonDeserializer<ChessMove> {
    public ChessMove deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        var builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPosition.class, new PosAdapter());

        //return builder.create().fromJson(jsonElement, BoardImpl.class);
        return builder.create().fromJson(jsonElement, MoveImpl.class);
    }
}
