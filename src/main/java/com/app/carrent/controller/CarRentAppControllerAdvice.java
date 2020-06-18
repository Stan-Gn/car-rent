package com.app.carrent.controller;

import com.app.carrent.exception.*;
import com.app.carrent.model.Car;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;



@ControllerAdvice
public class CarRentAppControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("exceptions");
        modelAndView.addObject("mess",exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(value = UserNotFoundToActionInAdminPanel.class)
    public ModelAndView handleException(UserNotFoundToActionInAdminPanel exception) {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/user-list-admin");
        modelAndView.addObject("userListAdminError", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(value = CarRentNotFoundToActionInAdminPanelException.class)
    public ModelAndView handleException(CarRentNotFoundToActionInAdminPanelException exception) {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");
        modelAndView.addObject("carRentListAdminError", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(CarIsReturnedInAdminPanelActionException.class)
    public ModelAndView handleException(CarIsReturnedInAdminPanelActionException exception) {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");
        modelAndView.addObject("carRentListAdminError", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(InvalidDistanceValueInPanelAdminException.class)
    public ModelAndView handleException(InvalidDistanceValueInPanelAdminException exception) {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");
        modelAndView.addObject("carRentListAdminError", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(ImageFileNotFoundException.class)
    public ModelAndView handleException(ImageFileNotFoundException exception) {
        ModelAndView modelAndView = new ModelAndView("addNewCar");
        modelAndView.addObject("car", new Car());
        modelAndView.addObject("addCarImageError", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(InvalidImageFileExtensionException.class)
    public ModelAndView handleException(InvalidImageFileExtensionException exception) {
        ModelAndView modelAndView = new ModelAndView("addNewCar");
        modelAndView.addObject("car", new Car());
        modelAndView.addObject("addCarImageError", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(IOImageFileTransferException.class)
    public ModelAndView handleException(IOImageFileTransferException exception) {
        ModelAndView modelAndView = new ModelAndView("addNewCar");
        modelAndView.addObject("car", new Car());
        modelAndView.addObject("addCarImageError", exception.getMessage());
        return modelAndView;
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleException(MaxUploadSizeExceededException exception) {
        ModelAndView modelAndView = new ModelAndView("addNewCar");
        modelAndView.addObject("car", new Car());
        modelAndView.addObject("addCarImageError", "File size too large");
        return modelAndView;
    }
}
