package com.app.carrent.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;



@ControllerAdvice
public class CarRentControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception exception) {
        return new ModelAndView("/error-404");
    }


}
