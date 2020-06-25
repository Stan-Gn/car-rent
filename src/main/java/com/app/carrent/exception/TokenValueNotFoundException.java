package com.app.carrent.exception;

import javassist.NotFoundException;

public class TokenValueNotFoundException extends NotFoundException {
    public TokenValueNotFoundException(String msg) {
        super(msg);
    }
}
