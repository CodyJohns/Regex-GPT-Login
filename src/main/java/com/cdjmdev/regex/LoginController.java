package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.OracleDAOFactory;
import com.cdjmdev.regex.service.LoginService;

public class LoginController {

    public static class LoginRequest {
        public String google_cred;
        public String id;
        public String password;
    }

    public static class LoginResult {
        public String authtoken;
    }

    private DAOFactory factory;
    private LoginService service;

    public LoginController() {
        factory = new OracleDAOFactory();
        service = new LoginService(factory);
    }

    public LoginResult handleRequest(LoginRequest request) {

        LoginResult result = service.login(request);

        return result;
    }

}