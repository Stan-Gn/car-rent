package com.app.carrent.exception;

import javassist.NotFoundException;

public class CarRentNotFoundToActionInAdminPanelException extends NotFoundException {
    public CarRentNotFoundToActionInAdminPanelException(String msg) {
        super(msg);
    }
}
