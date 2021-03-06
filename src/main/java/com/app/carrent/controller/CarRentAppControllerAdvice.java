package com.app.carrent.controller;

import com.app.carrent.exception.*;
import com.app.carrent.model.Car;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;


@ControllerAdvice
public class CarRentAppControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("exceptions");
        modelAndView.addObject("mess", "An unexpected error occurred");
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
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleException(MaxUploadSizeExceededException exception) {
        ModelAndView modelAndView = new ModelAndView("addNewCar");
        modelAndView.addObject("car", new Car());
        modelAndView.addObject("addCarImageError", "File size too large");
        return modelAndView;
    }

    @ExceptionHandler(CarToRentNotFoundException.class)
    public ModelAndView handleException(CarToRentNotFoundException exception){
        ModelAndView modelAndView = new ModelAndView("exceptions");
        modelAndView.addObject("mess", exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(DatesToFilterAreNotValidException.class)
    public ModelAndView handleException(DatesToFilterAreNotValidException exc, RedirectAttributes redirectAttributes){
        ModelAndView modelAndView = new ModelAndView("redirect:/cars");
        redirectAttributes.addFlashAttribute("dateError",exc.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(DatesToCarReservationAreNotValidException.class)
    public String handleException(DatesToCarReservationAreNotValidException exception,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("reservationError", exception.getMessage());
        return "redirect:/reservation?id="+exception.getId();
    }
    @ExceptionHandler(DatesToCarReservationConflictValidException.class)
    public String handleException(DatesToCarReservationConflictValidException exception,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("reservationError", exception.getMessage());
        redirectAttributes.addFlashAttribute("dateConflictList", exception.getDateConflictCarRentList());
        return "redirect:/reservation?id="+exception.getId();
    }

    @ExceptionHandler(TokenValueNotFoundException.class)
    public String handleException(TokenValueNotFoundException exception,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("registrationError", exception.getMessage());
        return "redirect:/registration";
    }

    @ExceptionHandler(MailException.class)
    public ModelAndView handleException(MailException exception){
        ModelAndView modelAndView = new ModelAndView("exceptions");
        modelAndView.addObject("mess", "There was an error in sending the email, if you do not receive the activation token, contact the administrator");
        return modelAndView;
    }

}
