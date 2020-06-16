package com.app.carrent.controller;

import com.app.carrent.controller.modelSaver.PageAndPageNumbersModelSaver;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
                                       @RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/user-list-admin");
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            actionOnUserInAdminPanel(action, userOptional);
        } else {
            modelAndView = new ModelAndView("forward:/admin/user-list-admin");
            modelAndView.addObject("userListAdminError", "Failed to find user with given id");
        }

        return modelAndView;
    }

    private void actionOnUserInAdminPanel(@PathVariable("action") String action, Optional<User> userOptional) {
        switch (action) {
            case "remove":
                userService.deleteUser(userOptional.get());
                break;
            case "changeLockedStatus":
                User u = userOptional.get();
                u.setNonLocked(!u.isNonLocked());
                userService.saveUser(u);
                break;
        }
    }

    @GetMapping("/admin/car-rent-list-admin")
    public ModelAndView carRentList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("car-rent-list-adm");

        displayingListOfRentals(pageNumber, modelAndView);

        return modelAndView;
    }

    private void displayingListOfRentals(@RequestParam Optional<Integer> pageNumber, ModelAndView modelAndView) throws NotFoundException {
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
                                          @RequestParam(value = "distance", required = false) Optional<String> distanceOptional) {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");

        Optional<CarRent> carRentOptional = carRentService.findById(id);
        if (carRentOptional.isPresent()) {
            ModelAndView model = actionOnRentItem(action, distanceOptional, modelAndView, carRentOptional);
            if (model != null) return model;
        } else {
            modelAndView.addObject("carRentListAdminError", "Failed to find user with given id");
        }

        return modelAndView;
    }

    private ModelAndView actionOnRentItem(@PathVariable("action") String action, @RequestParam(value = "distance", required = false) Optional<String> distanceOptional, ModelAndView modelAndView, Optional<CarRent> carRentOptional) {
        switch (action) {
            case "delete":
                carRentService.delete(carRentOptional.get());
                break;
            case "confirm":
                CarRent carRent = carRentOptional.get();
                if (carRent.isReturned()) {
                    modelAndView.addObject("carRentListAdminError", "The car has been returned");
                    return modelAndView;
                } else if (distanceIsIncorrect(distanceOptional)) {
                    modelAndView.addObject("carRentListAdminError", "Invalid distance value");
                    return modelAndView;
                } else {
                    setReturnDateIfIsEarlierThanCurrentDate(carRent);
                    calculationOfTotalAmountDue(distanceOptional, carRent);
                    carRentService.save(carRent);
                }
                break;
        }
        return null;
    }

    private void calculationOfTotalAmountDue(@RequestParam(value = "distance", required = false) Optional<String> distanceOptional, CarRent carRent) {
        double distance = getDoubleDistanceFromString(distanceOptional);
        double days = ChronoUnit.HOURS.between(carRent.getPickUpDate(), carRent.getDropOffDate()) / 24.0;
        double pricePerDay = carRent.getCar().getPricePerDay();
        double pricePerKM = carRent.getCar().getPricePerKm();

        BigDecimal totalPrice = BigDecimal.valueOf(days * pricePerDay + distance * pricePerKM).setScale(2, BigDecimal.ROUND_HALF_UP);
        carRent.setDistance(distance);
        carRent.setTotalPrice(totalPrice);
        carRent.setReturned(true);
    }

    private void setReturnDateIfIsEarlierThanCurrentDate(CarRent carRent) {
        if (LocalDateTime.now().isAfter(carRent.getDropOffDate())) {
            carRent.setDropOffDate(LocalDateTime.now());
        }
    }

    private boolean distanceIsIncorrect(@RequestParam(value = "distance", required = false) Optional<String> distanceOptional) {
        return !distanceOptional.isPresent() || getDoubleDistanceFromString(distanceOptional) == null;
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
            actionOnCarItem(action, carOptional);
        } else {
            modelAndView = new ModelAndView("forward:/admin/car-list-admin");
            modelAndView.addObject("carListAdminError", "Car with given id does not exist");
        }

        return modelAndView;
    }

    private void actionOnCarItem(@PathVariable("action") String action, Optional<Car> carOptional) {
        switch (action) {
            case "remove":
                Car car = carOptional.get();
                carService.delete(car);
                break;
        }
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

    private Double getDoubleDistanceFromString(Optional<String> distance) {
        try {
            return Double.parseDouble(distance.get());
        } catch (Exception e) {
            return null;
        }
    }
}
