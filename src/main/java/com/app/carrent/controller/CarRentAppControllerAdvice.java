package com.app.carrent.controller;

import com.app.carrent.exception.UserNotFoundToActionInAdminPanel;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
public class CarRentAppControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception exception) {
        return new ModelAndView("/error-404");
    }

    @ExceptionHandler(value = UserNotFoundToActionInAdminPanel.class)
    public ModelAndView handleException(UserNotFoundToActionInAdminPanel exception) {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/user-list-admin");
        modelAndView.addObject("userListAdminError", exception.getMessage());
        return modelAndView;
    }


}
