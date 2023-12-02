package comms;

import com.google.gson.Gson;
import req.CreateGameRequest;
import req.JoinGameRequest;
import req.LoginRequest;
import req.RegisterRequest;
import res.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;

public class ServerFacade {
    private static boolean loggedIn = false;
    private static final String serverUrl = "http://localhost:8080";

    private static String currUser = "";
    private static String authtok = "";

    // map of ids and numberings
    private static HashMap<Integer, Integer> idMap = new HashMap<>();


    public static void logout() throws Exception {
        if (!loggedIn) {
            throw new Exception("Error: not logged in.");
        }
        loggedIn = false;
        currUser = "";
        authtok = "";
        System.out.println("Logged out. Returning to pre-login.");
    }

    public static void register(String serverUrl, String username, String password, String email) throws Exception {
        // Specify the desired endpoint
        URI uri = new URI(serverUrl + "/user");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String body = new Gson().toJson(registerRequest);
        writeRequestBody(body, http);
        http.connect();
        System.out.println("connected");
        Object responseBody = getResponse(http, RegisterResponse.class);

        // Output the response body
        System.out.println("Welcome, " + username + "!");
        authtok = ((RegisterResponse) responseBody).getAuthToken();
        loggedIn = true;
        currUser = username;
    }

    public static void login(String serverUrl, String username, String password) throws Exception {
        // Specify the desired endpoint
        URI uri = new URI(serverUrl+"/session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        // make login request object and convert, pass to writeRequestBody
        LoginRequest loginRequest = new LoginRequest(username, password);
        String body = new Gson().toJson(loginRequest);
        writeRequestBody(body, http);
        // Make the request
        http.connect();
        Object responseBody = getResponse(http, LoginResponse.class);

        // Output the response body
        System.out.println("Welcome, " + username + "!");
        authtok = ((LoginResponse) responseBody).getAuthToken(); //(String) response.get("authToken");
        loggedIn = true;
        currUser = username;
    }

    public static int createGame(String serverUrl, String name) throws Exception {
        // Specify the desired endpoint
        URI uri = new URI(serverUrl+"/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        http.addRequestProperty("Authorization", authtok);

        // make login request object and convert, pass to writeRequestBody
        CreateGameRequest createRequest = new CreateGameRequest(name);
        String body = new Gson().toJson(createRequest);
        writeRequestBody(body, http);
        // Make the request
        http.connect();
        Object responseBody = getResponse(http, CreateGameResponse.class);

        int id = ((CreateGameResponse) responseBody).getGameID();
        // Output the response body
        System.out.println("Game created with ID: " + id);
        return id;
    }

    public static void listGames(String serverUrl) throws Exception {
        // Specify the desired endpoint
        URI uri = new URI(serverUrl+"/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");
        // set header as auth token
        http.addRequestProperty("Authorization", authtok);

        // Make the request
        http.connect();
        Object responseBody = getResponse(http, ListGamesResponse.class);

        // Output the response body
        System.out.println("Games:");
        ListGamesResponse.GameObj[] games = ((ListGamesResponse) responseBody).getGames();
        idMap = new HashMap<>();

        for (int i = 0; i < games.length; i++) {
            // make sure to assign different numberings for each id, and update
            System.out.print((i+1) + ". "+games[i].gameName()+ " : Black: " + games[i].blackUsername() + " White: " + games[i].whiteUsername());
            idMap.put(i+1, games[i].gameID());
            System.out.println();
        }
    }

    public static void joinGame(String serverUrl,String color, int id) throws Exception {
        System.out.println("id: " + id);
        System.out.println("idMap: " + idMap);
        id = idMap.get(id);
        // Specify the desired endpoint
        URI uri = new URI(serverUrl+"/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("PUT");
        // set header as auth token
        http.addRequestProperty("Authorization", authtok);

        JoinGameRequest joinRequest = new JoinGameRequest(color, id);
        String body = new Gson().toJson(joinRequest);
        writeRequestBody(body, http);

        // Make the request
        http.connect();
        Object responseBody = getResponse(http, JoinGameResponse.class);

        // Output the response body
        System.out.println("Joined game!");
    }


    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static Object getResponse(HttpURLConnection http,Class<?> cls) throws IOException {
        int statCode = http.getResponseCode();
        var statusMessage = http.getResponseMessage();
        if (statCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error: " + statCode + " " + statusMessage);
        }

        Object responseBody = "";
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = new Gson().fromJson(inputStreamReader, cls);
        }
        return responseBody;
    }
}
