import org.junit.jupiter.api.*;
import comms.ServerFacade;

public class ClientTests {
    private static String serverUrl = "http://localhost:8080";
    private String name = "username";
    private String password = "password";
    private String email = "email";

    @Test
    @Order(1)
    @DisplayName("Invalid Logout User - Not Logged In")
    public void testInvalidLogoutUser() throws Exception {
        ServerFacade.logout();
        // check that you can't log out if you're not logged in
        Assertions.assertThrows(Exception.class, () -> {
            ServerFacade.logout();
        });
    }

    @Test
    @Order(2)
    @DisplayName("Register")
    public void testRegisterUser() throws Exception {
        //generate a random username
        String newname = "username" + (int)(Math.random() * 1000);
        Assertions.assertDoesNotThrow(() -> {
            ServerFacade.register(newname,password,email);});
    }

    @Test
    @Order(3)
    @DisplayName("Logout User")
    public void testLogoutUser() throws Exception {
        ServerFacade.login(name,password);
            // just logged in this user in the previous test
        Assertions.assertDoesNotThrow(() -> {
            ServerFacade.logout();
        });
    }

    @Test
    @Order(4)
    @DisplayName("Invalid Register User - Existing Username")
    public void testInvalidRegisterUser() throws Exception {
        // just registered this user in the previous test
        Assertions.assertThrows(Exception.class, () -> {
            ServerFacade.register( name, password, email);
        });
        //should throw an error message
    }

    @Test
    @DisplayName("Login User")
    @Order(5)
    public void testLoginUser() throws Exception {
        Assertions.assertDoesNotThrow(() -> {
            ServerFacade.login(name,password);
        });
    }

    @Test
    @Order(5)
    @DisplayName("Invalid Login User - Wrong Password")
    public void testInvalidLoginUser() throws Exception {
        ServerFacade.logout();
        Assertions.assertThrows(Exception.class, () -> {
            ServerFacade.login(name,"wrongPassword");
        });
    }

    @Test
    @Order(6)
    @DisplayName("Create Game")
    public void testCreateGame() throws Exception {
        ServerFacade.login(name,password);
        Assertions.assertDoesNotThrow(() -> {
           ServerFacade.createGame( "gameName");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Invalid Create Game - Missing Name")
    public void testInvalidCreateGame() {
        Assertions.assertThrows(Exception.class, () -> {
            ServerFacade.createGame( null);
        });
    }

    @Test
    @Order(8)
    @DisplayName("Join Game")
    public void testJoinGame() throws Exception {
        ServerFacade.login(name,password);
        int id = ServerFacade.createGame( "gameName");
        ServerFacade.listGames();
        Assertions.assertDoesNotThrow(() -> {
            ServerFacade.joinGame( "WHITE", 1);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Invalid Join Game - Game Doesn't Exist")
    public void testInvalidJoinGame() throws Exception {
        ServerFacade.login(name,password);
        Assertions.assertThrows(Exception.class, () -> {
            ServerFacade.joinGame( "WHITE", 0);
        });
    }

    @Test
    @Order(10)
    @DisplayName("List Games")
    public void testListGames() throws Exception {
        ServerFacade.login(name,password);
        Assertions.assertDoesNotThrow(() -> {
            ServerFacade.listGames();
        });
    }

    @Test
    @Order(11)
    @DisplayName("Invalid List Games - Not Logged In")
    public void testInvalidListGames() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            ServerFacade.listGames();
        });
    }
}
