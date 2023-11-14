package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.LoginController;

public class LoginService {

	private DAOFactory factory;

	public LoginService(DAOFactory factory) {
		this.factory = factory;
	}

	public LoginController.LoginResult login(LoginController.LoginRequest request) {
		if(request.google_cred != null)
			return googleLogin(request.google_cred);
		else
			return conventionalLogin(request.id, request.password);
	}

	private LoginController.LoginResult googleLogin(String credential) {
		return null;
	}

	private LoginController.LoginResult conventionalLogin(String id, String password) {

		User user = factory.getUserDAO().getByEmail(id);

		boolean match = user.password.compare(password);

		if(!match)
			throw new RuntimeException("Credentials Incorrect");

		Authtoken token;

		try {
			//if authtoken exists then just update the expiration time
			token = factory.getAuthtokenDAO().getByUserID(user.id);

			token.expires = Utilities.getFutureTimestamp();

			factory.getAuthtokenDAO().save(token);
		} catch(Exception e) {
			token = factory.getAuthtokenDAO().createNew(user);
		}

		LoginController.LoginResult result = new LoginController.LoginResult();
		result.authtoken = token.id;

		return result;
	}
}
