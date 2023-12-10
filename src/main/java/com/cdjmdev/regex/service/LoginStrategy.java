package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.exception.CredentialsIncorrectException;
import com.cdjmdev.regex.LoginController;

public interface LoginStrategy {
    public LoginController.LoginResult login() throws CredentialsIncorrectException;
}
