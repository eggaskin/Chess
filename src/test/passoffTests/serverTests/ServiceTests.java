package passoffTests.serverTests;

import org.junit.jupiter.api.*;
import models.*;
import req.*;
import res.*;
import services.*;
import dataAccess.*;

import java.sql.Connection;

import static services.Server.db;

public class ServiceTests {
    private String myCurrAuth = null;

    @BeforeEach
    public void setUpEach() {
        // clear database
        (new ClearService()).clear();
    }

    @Test
    @Order(1)
    @DisplayName("Register User")
    public void testRegisterUser() throws DataAccessException {
        Connection conn = db.getConnection();
        // make user, register, and check that their username and auth token are in the databases
        RegisterRequest request = new RegisterRequest("username","password","email");
        RegisterResponse response = (new RegisterService()).register(request);

        Assertions.assertDoesNotThrow(() -> {
            (new UserDAO()).findUser(conn,"username");
        });
        this.myCurrAuth = response.getAuthToken();
        Assertions.assertEquals(myCurrAuth, (new AuthDAO()).getAuthToken(conn,myCurrAuth).getAuthtoken());
        db.returnConnection(conn);
    }

    public void addUser(String username) {
        User user = new User(username,"password","email");
        RegisterRequest request = new RegisterRequest(username,"password","email");
        RegisterResponse response = (new RegisterService()).register(request);
        this.myCurrAuth = response.getAuthToken();
    }

    @Test
    @Order(2)
    @DisplayName("Test Clear Service")
    public void testClearService() throws DataAccessException {
        Connection conn = db.getConnection();
        // add user to database
        addUser("username");
        (new ClearService()).clear();

        // make sure user and auth is cleared from database
        Assertions.assertThrows(DataAccessException.class, () -> {
            (new UserDAO()).findUser(conn,"username");
        });
        Assertions.assertThrows(DataAccessException.class, () -> {
            (new AuthDAO()).getAuthToken(conn,myCurrAuth);
        });
        db.returnConnection(conn);
    }

    @Test
    @Order(3)
    @DisplayName("Invalid Register User - Existing Username")
    public void testInvalidRegisterUser() {
        addUser("username");

        // you should not be able to register a user with the same username.
        RegisterRequest request = new RegisterRequest("username","password","email");
        RegisterResponse response = (new RegisterService()).register(request);

        //should throw an error message
        Assertions.assertNull(response.getAuthToken());
        Assertions.assertNull(response.getUserName());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    @DisplayName("Login User")
    @Order(4)
    public void testLoginUser() throws DataAccessException {
        addUser("username");
        Connection conn = db.getConnection();

        // then check auth token is in database
        LoginRequest request = new LoginRequest("username","password");
        var response = (new LoginService()).login(request);
        // check auth token in database
        Assertions.assertDoesNotThrow(() -> {
            (new AuthDAO()).getAuthToken(conn,response.getAuthToken());
        });
        Assertions.assertEquals(response.getAuthToken(),(new AuthDAO()).getAuthToken(conn,response.getAuthToken()).getAuthtoken());
        db.returnConnection(conn);
    }

    @Test
    @Order(5)
    @DisplayName("Invalid Login User - Wrong Password")
    public void testInvalidLoginUser() {
        addUser("username");
        // try to login with the wrong password
        LoginRequest request = new LoginRequest("username","ishallnotpass");
        var response = (new LoginService()).login(request);
        // check that response has no new auth/right error
        Assertions.assertNull(response.getAuthToken());
        Assertions.assertNull(response.getUserName());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals(response.getMessage(), "Error: unauthorized");
    }

    @Test
    @Order(6)
    @DisplayName("Logout User")
    public void testLogoutUser() throws DataAccessException {
        // check auth token is removed
        addUser("username");
        Connection conn = db.getConnection();
        var response = (new LogoutService()).logout(myCurrAuth);
        Assertions.assertThrows(DataAccessException.class, () -> {
            (new AuthDAO()).getAuthToken(conn,myCurrAuth);
        });
        db.returnConnection(conn);
    }

    @Test
    @Order(7)
    @DisplayName("Invalid Logout User - Not Logged In")
    public void testInvalidLogoutUser() {
        // check that you can't logout if you're not logged in
        var response = (new LogoutService()).logout(myCurrAuth);
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals("Error: unauthorized", response.getMessage());
    }


    @Test
    @Order(8)
    @DisplayName("Create Game")
    public void testCreateGame() throws DataAccessException {
        Connection conn = db.getConnection();
        // check that game is in database
        CreateGameRequest request = new CreateGameRequest("gameName");
        CreateGameResponse response = (new CreateGameService()).createGame(request);
        Assertions.assertDoesNotThrow(() -> {
            (new GameDAO()).findGame(conn, response.getGameID());
        });
        db.returnConnection(conn);
    }

    public int addGame(String gameName) {
        CreateGameRequest request = new CreateGameRequest(gameName);
        CreateGameResponse response = (new CreateGameService()).createGame(request);
        return response.getGameID();
    }

    @Test
    @Order(9)
    @DisplayName("Invalid Create Game - Missing Name")
    public void testInvalidCreateGame() {
        // check that we can't create a game with a null name
        CreateGameRequest request = new CreateGameRequest(null);
        CreateGameResponse response = (new CreateGameService()).createGame(request);
        Assertions.assertEquals(0,response.getGameID());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals("Error: bad request", response.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("Join Game")
    public void testJoinGame() throws DataAccessException {
        int gameId = addGame("gameName");
        addUser("username");
        Connection conn = db.getConnection();
        // check user is in game and game exists
        JoinGameRequest request = new JoinGameRequest("WHITE", gameId);
        JoinGameResponse response = (new JoinGameService()).joinGame(request, myCurrAuth);
        Assertions.assertDoesNotThrow(() -> {
            (new GameDAO()).findGame(conn, gameId);
        });
        Assertions.assertEquals("username", (new GameDAO()).findGame(conn, gameId).getWhiteUsername());
        db.returnConnection(conn);
    }

    @Test
    @Order(11)
    @DisplayName("Invalid Join Game - Game Doesn't Exist")
    public void testInvalidJoinGame() throws DataAccessException {
        addUser("username");
        Connection conn = db.getConnection();
        // check that you can't join a game that doesn't exist
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);
        JoinGameResponse response = (new JoinGameService()).joinGame(request, myCurrAuth);
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals("Error: game not found",response.getMessage());
        Assertions.assertThrows(DataAccessException.class, () -> {
            (new GameDAO()).findGame(conn,1);
        });
        db.returnConnection(conn);
    }

    @Test
    @Order(12)
    @DisplayName("List Games")
    public void testListGames() {
        addUser("username");
        addGame("gameName");
        // check that you can list games
        ListGamesResponse response = (new ListGamesService()).listGames(myCurrAuth);
        Assertions.assertNotNull(response.getGames());
    }

    @Test
    @Order(13)
    @DisplayName("Invalid List Games - No Games")
    public void testInvalidListGames() {
        addGame("gameName");
        // check that you can list games
        ListGamesResponse response = (new ListGamesService()).listGames(myCurrAuth);
        Assertions.assertNull(response.getGames());
        Assertions.assertEquals("Error: unauthorized", response.getMessage());
    }

}
