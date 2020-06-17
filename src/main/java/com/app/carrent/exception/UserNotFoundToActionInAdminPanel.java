package com.app.carrent.exception;

import javassist.NotFoundException;


public class UserNotFoundToActionInAdminPanel extends NotFoundException  {
    public UserNotFoundToActionInAdminPanel(String msg) {
        super(msg);
    }
}
