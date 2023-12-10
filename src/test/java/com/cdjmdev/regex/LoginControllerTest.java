package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.AuthtokenDAO;
import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.UserDAO;
import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.service.ConventionalLogin;
import com.cdjmdev.regex.service.LoginStrategy;
import com.cdjmdev.regex.service.ThirdPartyLogin;
import com.cdjmdev.regex.verifier.Verifier;
import com.fnproject.fn.testing.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.gson.Gson;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
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

    private static User user;
    private static AuthtokenDAO aDAO;
    private static UserDAO uDAO;
    private static DAOFactory dFactory;
    private static Verifier vMock;

    @BeforeAll
    static void setup() {
        user = new User("", "", "1234567890", "user@user.com");

        Authtoken token = new Authtoken();

        token.id = Utilities.generateAuthtoken(16);
        token.user_id = user.id;
        token.expires = Utilities.getFutureTimestamp();

        dFactory = mock(DAOFactory.class);

        aDAO = mock(AuthtokenDAO.class);
        uDAO = mock(UserDAO.class);

        Mockito.when(uDAO.getByEmail(Mockito.any())).thenReturn(user);
        Mockito.when(aDAO.getByUserID(Mockito.any())).thenReturn(token);
        Mockito.when(uDAO.getByGoogleID(Mockito.any())).thenReturn(user);

        Mockito.when(dFactory.getAuthtokenDAO()).thenReturn(aDAO);
        Mockito.when(dFactory.getUserDAO()).thenReturn(uDAO);

        vMock = mock(Verifier.class);
        GoogleIdToken tokenMock = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payloadMock = mock(GoogleIdToken.Payload.class);

        Mockito.when(payloadMock.getEmail()).thenReturn("user@user.com");
        Mockito.when(payloadMock.getSubject()).thenReturn(user.id);
        Mockito.when(tokenMock.getPayload()).thenReturn(payloadMock);

        Mockito.when(vMock.getToken("1234567890")).thenReturn(tokenMock);
        Mockito.when(vMock.getToken("0987654321")).thenReturn(null);
    }

    @Test
    @DisplayName("Conventional login with correct credentials")
    void testLogin() {

        LoginController.LoginRequest request = new LoginController.LoginRequest();

        request.id = "";
        request.password = "1234567890";

        LoginStrategy service = new ConventionalLogin(dFactory, request);

        assertDoesNotThrow(service::login);
    }

    @Test
    @DisplayName("Conventional login with wrong credentials")
    void testLoginWrongPassword() {

        LoginController.LoginRequest request = new LoginController.LoginRequest();

        request.id = "";
        request.password = "0987654321";

        LoginStrategy service = new ConventionalLogin(dFactory, request);

        assertThrows(CredentialsIncorrectException.class, service::login);
    }

    @Test
    @DisplayName("Google login with correct credential")
    void testGoogleLogin() {

        LoginController.LoginRequest request = new LoginController.LoginRequest();

        request.google_cred = "1234567890";

        LoginStrategy service = new ThirdPartyLogin(dFactory, request, vMock);

        assertDoesNotThrow(service::login);
    }

    @Test
    @DisplayName("Google login with wrong credential")
    void testGoogleLoginWrongCred() {
        LoginController.LoginRequest request = new LoginController.LoginRequest();

        request.google_cred = "0987654321";

        LoginStrategy service = new ThirdPartyLogin(dFactory, request, vMock);

        assertThrows(CredentialsIncorrectException.class, service::login);
    }
}