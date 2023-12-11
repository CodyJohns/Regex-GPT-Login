package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.LoginController;
import com.cdjmdev.regex.verifier.Verifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public abstract class ThirdPartyLogin implements LoginStrategy {

    protected DAOFactory factory;
    private LoginController.LoginRequest request;
    private Verifier<GoogleIdToken> verifier;

    public ThirdPartyLogin(DAOFactory factory, LoginController.LoginRequest request, Verifier verifier) {
        this.factory = factory;
        this.request = request;
        this.verifier = verifier;
    }

    @Override
    public LoginController.LoginResult login() throws CredentialsIncorrectException {

        GoogleIdToken idToken = verifier.getToken(request.google_cred);

        LoginController.LoginResult result = new LoginController.LoginResult();

        if(idToken != null) {
            String userID = idToken.getPayload().getSubject();
            String email = idToken.getPayload().getEmail();

            User user = getUser(userID, email);

            Authtoken token = getToken(user);

            result.authtoken = token.id;
        } else {
            throw new CredentialsIncorrectException("Invalid token.");
        }

        return result;
    }

    protected abstract User getUser(String userID, String email);

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
