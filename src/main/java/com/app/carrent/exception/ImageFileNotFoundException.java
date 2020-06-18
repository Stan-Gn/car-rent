package com.app.carrent.exception;

import java.io.FileNotFoundException;

public class ImageFileNotFoundException extends FileNotFoundException {
    public ImageFileNotFoundException(String mes) {
        super(mes);
    }
}
