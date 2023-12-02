package services;

import dataAccess.Database;
import handlers.*;
import spark.*;
import org.eclipse.jetty.websocket.api.annotations.*;

/**
 * The main class to run the Chess server.
 */
@WebSocket
public class Server {

    /**
     * The database from which to get connections.
     */
    public static Database db = new Database();

    /**
     * Runs the server.
     * @param args command line args, if any
     */
    public static void main(String[] args) {
        // start server
        new Server().run();
    }

    /**
     * Runs the server.
     */
    private void run() {
        Spark.port(8080);
        Spark.webSocket("/connect", WSHandler.class);
        Spark.externalStaticFileLocation("C:/Users/evely/Desktop/cs240/Chess/web/");

        // Setup handlers for endpoints
        Spark.post("/user", (req,res) -> (new RegisterHandler()).handle(req,res));
        Spark.post("/session", (req,res) -> (new LoginHandler()).handle(req,res));
        Spark.delete("/session", (req,res) -> (new LogoutHandler()).handle(req,res));
        Spark.delete("/db", (req,res) -> (new ClearHandler()).handle(req,res));
        Spark.get("/game", (req,res) -> (new ListGamesHandler()).handle(req,res));
        Spark.post("/game", (req,res) -> (new CreateGameHandler()).handle(req,res));
        Spark.put("/game",(req,res) -> (new JoinGameHandler()).handle(req,res));
    }


}
