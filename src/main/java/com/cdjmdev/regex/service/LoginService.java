package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.oracle.util.Utilities;
import com.cdjmdev.regex.LoginController;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class LoginService {

	private DAOFactory factory;
	private final String CLIENT_ID = "1026110361137-1pjoqo75hg9a1eitiqsvffn73f731ojg.apps.googleusercontent.com";

	public LoginService(DAOFactory factory) {
		this.factory = factory;
	}

	public LoginController.LoginResult login(LoginController.LoginRequest request) throws CredentialsIncorrectException {
		if(request.google_cred != null)
			return googleLogin(request.google_cred);
		else
			return conventionalLogin(request.id, request.password);
	}

	//TODO: Refactor these two methods into their own classes

	private LoginController.LoginResult googleLogin(String credential) {

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
			.setAudience(Collections.singletonList(CLIENT_ID))
			.build();

		GoogleIdToken idToken;

		try {
			idToken = verifier.verify(credential);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		LoginController.LoginResult result = new LoginController.LoginResult();

		if(idToken != null) {
			String userID = idToken.getPayload().getSubject();
			String email = idToken.getPayload().getEmail();

			User user;

			try{
				user = factory.getUserDAO().getByGoogleID(userID);
			} catch(Exception e) {
				user = new User(userID, "", Utilities.generateCode(16), email);
			}

			Authtoken token;

			try {
				//if authtoken exists then just update the expiration time
				token = factory.getAuthtokenDAO().getByUserID(user.id);

				token.expires = Utilities.getFutureTimestamp();

				factory.getAuthtokenDAO().save(token);
			} catch(Exception e) {
				token = factory.getAuthtokenDAO().createNew(user);
			}

			result.authtoken = token.id;
		} else {
			throw new RuntimeException("Invalid google token.");
		}

		return result;
	}

	private LoginController.LoginResult conventionalLogin(String id, String password) throws CredentialsIncorrectException {

		User user = factory.getUserDAO().getByEmail(id);

		boolean match = user.password.compare(password);

		if(!match)
			throw new CredentialsIncorrectException("Credentials Incorrect");

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
