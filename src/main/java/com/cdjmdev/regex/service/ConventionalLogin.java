package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.LoginController;

public class ConventionalLogin implements LoginStrategy {

    private DAOFactory factory;
    private LoginController.LoginRequest request;

    public ConventionalLogin(DAOFactory factory, LoginController.LoginRequest request) {
        this.factory = factory;
        this.request = request;
    }

    @Override
    public LoginController.LoginResult login() throws CredentialsIncorrectException {
        User user = factory.getUserDAO().getByEmail(request.id);

        boolean match = user.password.compare(request.password);

        if(!match)
            throw new CredentialsIncorrectException("Credentials Incorrect");

        Authtoken token = getToken(user);

        LoginController.LoginResult result = new LoginController.LoginResult();
        result.authtoken = token.id;

        return result;
    }

    private Authtoken getToken(User user) {
        Authtoken token;

        try {
            //if authtoken exists then just update the expiration time
            token = factory.getAuthtokenDAO().getByUserID(user.id);

            token.expires = Utilities.getFutureTimestamp();

            factory.getAuthtokenDAO().save(token);
        } catch(Exception e) {
            token = factory.getAuthtokenDAO().createNew(user);
        }

        return token;
    }
}
