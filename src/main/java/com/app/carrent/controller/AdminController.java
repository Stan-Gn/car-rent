package com.app.carrent.controller;

import com.app.carrent.controller.modelSaver.PageAndPageNumbersModelSaver;
import com.app.carrent.dataLoader.CarRentLoader;
import com.app.carrent.model.CarRent;
import com.app.carrent.model.User;
import com.app.carrent.service.CarRentService;
import com.app.carrent.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
public class AdminController {

    private UserService userService;
    private CarRentService carRentService;

    @Autowired
    public AdminController(UserService userService, CarRentService carRentService) {
        this.userService = userService;
        this.carRentService = carRentService;
    }

    @GetMapping("/admin")
    public ModelAndView admin() {
        return new ModelAndView("admin");
    }

    @GetMapping("/admin/user-list-admin")
    public ModelAndView userList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("user-list-adm");

        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<User> usersPage = userService.findAll(pageRequest);

        if (usersPage.getTotalPages() > 0) {
            modelAndView.addObject("usersPage", usersPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "usersPage", usersPage, currentPage);
        }

        return modelAndView;
    }

    @GetMapping("/admin/user-list-admin/{action}")
    public ModelAndView userList(@PathVariable(value = "action") String action,
                           @RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/user-list-admin");
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
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
        }else{
            modelAndView = new ModelAndView("forward:/admin/user-list-admin");
            modelAndView.addObject("userListAdminError", "Failed to find user with given id");
        }

        return modelAndView;
    }

    @GetMapping("/admin/car-rent-list-admin")
    public ModelAndView carRentList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("car-rent-list-adm");

        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<CarRent> carRentPage = carRentService.findAll(pageRequest);
        if (carRentPage.getTotalPages() > 0) {
            modelAndView.addObject("carRentPage", carRentPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "carRentPage", carRentPage, currentPage);
        }
        return modelAndView;
    }

    @GetMapping("/admin/car-rent-list-admin/{action}")
    public ModelAndView carRentList(@PathVariable(value = "action") String action,
                                    @RequestParam(value = "id") Long id,
                                    @RequestParam(value = "distance", required = false) Optional<String> distanceOptional) {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");
        Optional<CarRent> carRentOptional = carRentService.findById(id);
        if (carRentOptional.isPresent()) {
            switch (action) {
                case "delete":
                    carRentService.delete(carRentOptional.get());
                    break;
                case "confirm":
                    CarRent carRent = carRentOptional.get();
                    if (carRent.isReturned()) {
                        modelAndView.addObject("carRentListAdminError", "The car has been returned");
                        return modelAndView;
                    } else if (!distanceOptional.isPresent() || getDoubleDistanceFromString(distanceOptional) == null) {
                        modelAndView.addObject("carRentListAdminError", "Invalid distance value");
                        return modelAndView;
                    } else {
                        if (LocalDateTime.now().isAfter(carRent.getDropOffDate())) {
                            carRent.setDropOffDate(LocalDateTime.now());
                        }
                        double distance = getDoubleDistanceFromString(distanceOptional);
                        double days = ChronoUnit.HOURS.between(carRent.getPickUpDate(), carRent.getDropOffDate()) / 24.0;
                        double pricePerDay = carRent.getCar().getPricePerDay();
                        double pricePerKM = carRent.getCar().getPricePerKm();

                        BigDecimal totalPrice = BigDecimal.valueOf(days * pricePerDay + distance * pricePerKM).setScale(2, BigDecimal.ROUND_HALF_UP);
                        carRent.setDistance(distance);
                        carRent.setTotalPrice(totalPrice);
                        carRent.setReturned(true);
                        carRentService.save(carRent);
                    }
                    break;
            }
        }else{
            modelAndView.addObject("carRentListAdminError","Failed to find user with given id");
        }

        return modelAndView;
    }

    @GetMapping("admin/car-list-admin")
    public ModelAndView carList() {
        ModelAndView modelAndView = new ModelAndView("car-list-adm");
        return modelAndView;
    }

    private Double getDoubleDistanceFromString(Optional<String> distance) {
        try {
            return Double.parseDouble(distance.get());
        } catch (Exception e) {
            return null;
        }
    }
}
