package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.OracleDAOFactory;
import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.regex.service.ConventionalLogin;
import com.cdjmdev.regex.service.LoginStrategy;
import com.cdjmdev.regex.service.ThirdPartyLogin;
import com.cdjmdev.regex.verifier.AppleVerifier;
import com.cdjmdev.regex.verifier.GoogleVerifier;

public class LoginController {

    public static class LoginRequest {
        public String google_cred;
        public String apple_cred;
        public String id;
        public String password;
    }

    public static class LoginResult {
        public String authtoken;
        public int status = 200;
        public String message;
    }

    private DAOFactory factory;

    public LoginController() {
        factory = new OracleDAOFactory();
    }

    public LoginResult handleRequest(LoginRequest request) {

        LoginResult result;

        try {
            result = getLoginService(request).login();
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

    private LoginStrategy getLoginService(LoginRequest request) {
        if(request.apple_cred != null)
            return new ThirdPartyLogin(factory, request, new AppleVerifier());
        else if(request.google_cred != null)
            return new ThirdPartyLogin(factory, request, new GoogleVerifier());
        else
            return new ConventionalLogin(factory, request);
    }

}