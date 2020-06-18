package com.app.carrent.exception;

import javassist.NotFoundException;

public class CarToRentNotFoundException extends NotFoundException {
    public CarToRentNotFoundException(String msg) {
        super(msg);
    }
}
