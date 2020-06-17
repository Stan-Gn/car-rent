package com.app.carrent.controller;

import com.app.carrent.exception.CarIsReturnedInAdminPanelActionException;
import com.app.carrent.exception.CarRentNotFoundToActionInAdminPanelException;
import com.app.carrent.exception.InvalidDistanceValueInPanelAdminException;
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

    @ExceptionHandler(value = CarRentNotFoundToActionInAdminPanelException.class)
    public ModelAndView handleException(CarRentNotFoundToActionInAdminPanelException exception){
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");
        modelAndView.addObject("carRentListAdminError", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler (CarIsReturnedInAdminPanelActionException.class)
    public ModelAndView handleException (CarIsReturnedInAdminPanelActionException exception){
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");
        modelAndView.addObject("carRentListAdminError", exception.getMessage());
        return modelAndView;
    }
    @ExceptionHandler (InvalidDistanceValueInPanelAdminException.class)
    public ModelAndView handleException(InvalidDistanceValueInPanelAdminException exception){
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");
        modelAndView.addObject("carRentListAdminError", exception.getMessage());
        return modelAndView;
    }

}
