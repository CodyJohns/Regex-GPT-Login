package com.cdjmdev.regex.verifier;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleVerifier implements Verifier<GoogleIdToken> {

    private final String CLIENT_ID = "1026110361137-1pjoqo75hg9a1eitiqsvffn73f731ojg.apps.googleusercontent.com";

    private GoogleIdTokenVerifier verifier;

    public GoogleVerifier() {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    @Override
    public GoogleIdToken getToken(String credential) throws RuntimeException {
        try {
            return verifier.verify(credential);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
