package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.AuthtokenDAO;
import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.UserDAO;
import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.service.LoginService;
import com.fnproject.fn.testing.*;
import com.google.gson.Gson;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class LoginControllerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    @Disabled
    public void shouldReturnGreeting() {
        Gson gson = new Gson();

        LoginController.LoginRequest request = new LoginController.LoginRequest();
        request.id = "test@test.com";
        request.password = "1234567890";

        testing.givenEvent().withBody(gson.toJson(request)).enqueue();
        testing.thenRun(LoginController.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        System.out.println(result.getBodyAsString());
        Assertions.assertNotNull(result.getBodyAsString());
    }

    private static AuthtokenDAO aDAO;
    private static UserDAO uDAO;
    private static DAOFactory dFactory;

    @BeforeAll
    static void setup() {
        User user = new User("", "", "1234567890", "user@user.com");

        Authtoken token = new Authtoken();

        token.id = Utilities.generateAuthtoken(16);
        token.user_id = user.id;
        token.expires = Utilities.getFutureTimestamp();

        dFactory = mock(DAOFactory.class);

        aDAO = mock(AuthtokenDAO.class);
        uDAO = mock(UserDAO.class);

        Mockito.when(uDAO.getByEmail(Mockito.any())).thenReturn(user);
        Mockito.when(aDAO.getByUserID(Mockito.any())).thenReturn(token);

        Mockito.when(dFactory.getAuthtokenDAO()).thenReturn(aDAO);
        Mockito.when(dFactory.getUserDAO()).thenReturn(uDAO);
    }

    @Test
    @DisplayName("Login with correct credentials")
    void testLogin() {
        LoginService service = new LoginService(dFactory);

        LoginController.LoginRequest request = new LoginController.LoginRequest();

        request.id = "";
        request.password = "1234567890";

        assertDoesNotThrow(() -> {
            LoginController.LoginResult result = service.login(request);

            System.out.println(result.authtoken);
        });
    }

    @Test
    @DisplayName("Login with wrong credentials")
    void testLoginWrongPassword() {
        LoginService service = new LoginService(dFactory);

        LoginController.LoginRequest request = new LoginController.LoginRequest();

        request.id = "";
        request.password = "0987654321";

        assertThrows(CredentialsIncorrectException.class, () -> {
            service.login(request);
        });
    }
}