package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.OracleDAOFactory;
import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.regex.service.LoginService;

public class LoginController {

    public static class LoginRequest {
        public String google_cred;
        public String id;
        public String password;
    }

    public static class LoginResult {
        public String authtoken;
        public int status = 200;
        public String message;
    }

    private DAOFactory factory;
    private LoginService service;

    public LoginController() {
        factory = new OracleDAOFactory();
        service = new LoginService(factory);
    }

    public LoginResult handleRequest(LoginRequest request) {

        LoginResult result;

        try {
            result = service.login(request);
        } catch (NullPointerException e) {
            result = new LoginResult();
            result.status = 404;
            result.message = e.getMessage();
        } catch(CredentialsIncorrectException e) {
            result = new LoginResult();
            result.status = 403;
            result.message = e.getMessage();
        }catch(Exception e) {
            result = new LoginResult();
            result.status = 500;
            result.message = e.getMessage();
        }

        return result;
    }

}