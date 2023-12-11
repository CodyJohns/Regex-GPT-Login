package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.LoginController;
import com.cdjmdev.regex.verifier.Verifier;

public class AppleLogin extends ThirdPartyLogin {

    public AppleLogin(DAOFactory factory, LoginController.LoginRequest request, Verifier verifier) {
        super(factory, request, verifier);
    }

    @Override
    protected User getUser(String userID, String email) {
        User user;

        try{
            user = factory.getUserDAO().getByAppleID(userID);
        } catch(Exception e) {
            user = new User(userID, "", Utilities.generateCode(16), email);
            factory.getUserDAO().save(user);
        }

        return user;
    }
}
