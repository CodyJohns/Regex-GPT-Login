package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.regex.LoginController;
import com.cdjmdev.regex.verifier.Verifier;

public class AppleLogin extends ThirdPartyLogin {

    public AppleLogin(DAOFactory factory, LoginController.LoginRequest request, Verifier verifier) {
        super(factory, request, verifier);
    }

    @Override
    protected User getUser(String userID, String email) {
        return null;
    }
}
