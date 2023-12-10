package com.cdjmdev.regex.verifier;

public interface Verifier<T> {
    T getToken(String credential);
}
