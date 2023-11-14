package com.cdjmdev.regex;

import com.fnproject.fn.testing.*;
import com.google.gson.Gson;
import org.junit.*;

import static org.junit.Assert.*;

public class LoginControllerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    public void shouldReturnGreeting() {
        Gson gson = new Gson();

        LoginController.LoginRequest request = new LoginController.LoginRequest();
        request.id = "test@test.com";
        request.password = "1234567890";

        testing.givenEvent().withBody(gson.toJson(request)).enqueue();
        testing.thenRun(LoginController.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        System.out.println(result.getBodyAsString());
        assertNotNull(result.getBodyAsString());
    }

}