package com.app.carrent.controller;

import com.app.carrent.controller.modelSaver.PageAndPageNumbersModelSaver;
import com.app.carrent.exception.CarRentNotFoundToActionInAdminPanelException;
import com.app.carrent.exception.UserNotFoundToActionInAdminPanel;
import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.model.User;
import com.app.carrent.service.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;


@Controller
public class AdminController {

    private UserService userService;
    private CarRentService carRentService;
    private CarService carService;
    private CarImageServiceInterface imageService;


    @Autowired
    public AdminController(UserService userService, CarRentService carRentService, CarService carService,
                           CarImageServiceInterface imageService) {
        this.userService = userService;
        this.carRentService = carRentService;
        this.carService = carService;
        this.imageService = imageService;
    }

    @GetMapping("/admin")
    public ModelAndView admin() {
        return new ModelAndView("admin");
    }

    @GetMapping("/admin/user-list-admin")
    public ModelAndView userList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("user-list-adm");

        displayingListOfCarsInAdminPanel(pageNumber, modelAndView);

        return modelAndView;
    }

    private void displayingListOfCarsInAdminPanel(@RequestParam Optional<Integer> pageNumber, ModelAndView modelAndView) throws NotFoundException {
        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<User> usersPage = userService.findAll(pageRequest);

        if (usersPage.getTotalPages() > 0) {
            modelAndView.addObject("usersPage", usersPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "usersPage", usersPage, currentPage);
        }
    }

    @GetMapping("/admin/user-list-admin/{action}")
    public ModelAndView userListAction(@PathVariable(value = "action") String action,
                                       @RequestParam(value = "id") Long id) throws UserNotFoundToActionInAdminPanel {
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/user-list-admin");
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent())
            throw new UserNotFoundToActionInAdminPanel("Failed to find user with given id");
        userService.adminActionOnUser(action, userOptional.get());
        return modelAndView;
    }


    @GetMapping("/admin/car-rent-list-admin")
    public ModelAndView carRentList(@RequestParam Optional<Integer> pageNumber) throws Exception {
        ModelAndView modelAndView = new ModelAndView("car-rent-list-adm");

        displayingListOfRentals(pageNumber, modelAndView);

        return modelAndView;
    }

    private void displayingListOfRentals(@RequestParam Optional<Integer> pageNumber, ModelAndView modelAndView) throws Exception {
        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<CarRent> carRentPage = carRentService.findAll(pageRequest);
        if (carRentPage.getTotalPages() > 0) {
            modelAndView.addObject("carRentPage", carRentPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "carRentPage", carRentPage, currentPage);
        }
    }

    @GetMapping("/admin/car-rent-list-admin/{action}")
    public ModelAndView carRentListAction(@PathVariable(value = "action") String action,
                                          @RequestParam(value = "id") Long id,
                                          @RequestParam(value = "distance", required = false) Optional<String> distanceOptional) throws Exception {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");

        Optional<CarRent> carRentOptional = carRentService.findById(id);

        if (carRentOptional.isPresent()) {
            carRentService.actionOnRentItemInAdminPanel(action,distanceOptional,carRentOptional.get());
        } else {
           throw new CarRentNotFoundToActionInAdminPanelException("Failed to find user with given id");
        }

        return modelAndView;
    }

    @GetMapping("/admin/car-list-admin")
    public ModelAndView carList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("car-list-adm");

        displayCarItemsInPanelAdmin(pageNumber, modelAndView);

        return modelAndView;
    }

    private void displayCarItemsInPanelAdmin(@RequestParam Optional<Integer> pageNumber, ModelAndView modelAndView) throws NotFoundException {
        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<Car> carsPage = carService.findAll(pageRequest);

        if (carsPage.getTotalPages() > 0) {
            modelAndView.addObject("carsPage", carsPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "carsPage", carsPage, currentPage);
        }
    }

    @GetMapping("/admin/car-list-admin/{action}")
    public ModelAndView carList(@PathVariable(value = "action") String action,
                                @RequestParam(value = "id") long id) {
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/car-list-admin");

        Optional<Car> carOptional = carService.findCarById(id);
        if (carOptional.isPresent()) {
            modelAndView = actionOnCarItem(action, modelAndView, carOptional);
        } else {
            modelAndView = new ModelAndView("forward:/admin/car-list-admin");
            modelAndView.addObject("carListAdminError", "Car with given id does not exist");
        }

        return modelAndView;
    }

    private ModelAndView actionOnCarItem(@PathVariable("action") String action, ModelAndView modelAndView, Optional<Car> carOptional) {
        switch (action) {
            case "remove":
                Car car = carOptional.get();
                Optional<CarRent> cr = carRentService.findCarRentItemByGivenCarWhereNowIsBetweenPickUpDateAndDropOffDate(LocalDateTime.now(), car);
                if (cr.isPresent()) {
                    modelAndView = new ModelAndView("forward:/admin/car-list-admin");
                    modelAndView.addObject("carListAdminError", "You cannot delete the car because it is now rented");
                } else
                    carService.delete(car);
                break;
        }
        return modelAndView;
    }


    @GetMapping("/admin/car-list-admin/addCar")
    public ModelAndView addCar() {
        ModelAndView modelAndView = new ModelAndView("addNewCar");
        modelAndView.addObject("car", new Car());
        return modelAndView;
    }

    @PostMapping("/admin/car-list-admin/addCar")
    public ModelAndView addCar(@ModelAttribute(value = "car") @Valid Car car, Errors errors,
                               @RequestParam(value = "file") MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView("addNewCar");

        if (errors.hasErrors()) {
            modelAndView.addObject("addCarError", errors);
            return modelAndView;
        }

        carService.save(car);
        boolean savedCarImage = imageService.save(file, car);

        if (!savedCarImage) {
            modelAndView.addObject("addCarImageError", "Failed to save the image");
            return modelAndView;
        }

        modelAndView.addObject("success", "Correctly added car");

        return modelAndView;
    }

    @ModelAttribute("carTypeList")
    public void carTypeLis(Model model) {
        model.addAttribute(Arrays.asList(Car.CarType.values()));
    }
}
