package com.app.carrent.controller;

import com.app.carrent.controller.parser.CarRentDateTimeParser;
import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.model.User;
import com.app.carrent.service.CarRentService;
import com.app.carrent.service.CarService;
import com.app.carrent.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class CarRentController {

    private CarService carService;
    private CarRentService carRentService;
    private UserService userService;

    @Autowired
    public CarRentController(CarService carService, CarRentService carRentService, UserService userService) {
        this.carService = carService;
        this.carRentService = carRentService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("cars", carService.findAll());
        return modelAndView;
    }

    @GetMapping("/cars")
    private ModelAndView cars(@RequestParam("page") Optional<Integer> page,
                              Sort sort,
                              @RequestParam Optional<String> pickUpDate,
                              @RequestParam Optional<String> pickUpTime,
                              @RequestParam Optional<String> dropOffDate,
                              @RequestParam Optional<String> dropOffTime) throws NotFoundException {

        ModelAndView modelAndView = new ModelAndView("car-list-one");

        int currentPage = page.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 4, sort);
        Page<Car> carsPage = carService.findAll(pageRequest);

        if (pickUpDate.isPresent() && pickUpTime.isPresent() && dropOffDate.isPresent() && dropOffTime.isPresent()) {
            LocalDateTime pickUp = CarRentDateTimeParser.parseLocalDateTime(pickUpDate.get(), pickUpTime.get());
            LocalDateTime dropOff = CarRentDateTimeParser.parseLocalDateTime(dropOffDate.get(), dropOffTime.get());
            if (pickUp != null && dropOff != null) {
                carsPage = carRentService.findCarsNotReserved(pickUp, dropOff, pageRequest);
            }
        }

        if (currentPage > carsPage.getTotalPages())
            throw new NotFoundException("Page not found");

        modelAndView.addObject("carsPage", carsPage);

        int totalPages = carsPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        return modelAndView;
    }

    @GetMapping("/reservation")
    public ModelAndView reservation(@RequestParam long id) {
        ModelAndView modelAndView = new ModelAndView("reservation");
        modelAndView.addObject("carToRent", carService.findCarById(id));
        return modelAndView;
    }

    @PostMapping("/reservation")
    public ModelAndView reservation(@RequestParam long id,
                                    @RequestParam String pickUpDate,
                                    @RequestParam String pickUpTime,
                                    @RequestParam String dropOffDate,
                                    @RequestParam String dropOffTime,
                                    Authentication auth) {
        ModelAndView modelAndView = new ModelAndView("reservation");
        modelAndView.addObject("carToRent", carService.findCarById(id));

        if (checkingIfDateTimeFieldsAreEmpty(pickUpDate, pickUpTime, dropOffDate, dropOffTime, modelAndView))
            return modelAndView;

        LocalDateTime pickUp = CarRentDateTimeParser.parseLocalDateTime(pickUpDate, pickUpTime);
        LocalDateTime dropOff = CarRentDateTimeParser.parseLocalDateTime(dropOffDate, dropOffTime);

        if (pickUp == null || dropOff == null)
            return modelAndView.addObject("reservationError", "Invalid date format");

        if (checkingIfPickUpDateIsAfterDropOffDate(pickUp, dropOff, modelAndView))
            return modelAndView;

        if (checkingIfPickUpDateIsBeforeNow(pickUp, modelAndView))
            return modelAndView;

        Car car = carService.findCarById(id);
        List<CarRent> dateConflictCarRentList = carRentService.findDateConflict(pickUp, dropOff, car);

        if (checkingDateConflicts(dateConflictCarRentList, modelAndView))
            return modelAndView;

        if (auth == null) {
            modelAndView.addObject("reservationError", "Reservation portal error");
            return modelAndView;
        }

        Optional<User> user = userService.findUserByEmail(auth.getName());
        if (user.isPresent()) {
            saveCarRent(pickUp, dropOff, car, user);
        } else {
            modelAndView.addObject("reservationError", "Reservation portal error");
        }

        return modelAndView;
    }

    private void saveCarRent(LocalDateTime pickUp, LocalDateTime dropOff, Car car, Optional<User> user) {
        CarRent carRent = new CarRent();
        carRent.setCar(car);
        carRent.setReturned(false);
        carRent.setPickUpDate(pickUp);
        carRent.setDropOffDate(dropOff);
        carRent.setUser(user.get());
        carRentService.save(carRent);
    }

    private boolean checkingDateConflicts(List<CarRent> dateConflictCarRentList, ModelAndView modelAndView) {
        if (!dateConflictCarRentList.isEmpty()) {
            modelAndView.addObject("reservationError", "There is a conflict between booking dates");
            modelAndView.addObject("dateConflictList", dateConflictCarRentList);
            return true;
        }
        return false;
    }

    private boolean checkingIfPickUpDateIsBeforeNow(LocalDateTime pickUp, ModelAndView modelAndView) {
        if (pickUp.isBefore(LocalDateTime.now())) {
            modelAndView.addObject("reservationError", "Pickup date is before current date.");
            return true;
        }
        return false;
    }

    private boolean checkingIfPickUpDateIsAfterDropOffDate(LocalDateTime pickUp, LocalDateTime dropOff, ModelAndView modelAndView) {
        if (pickUp.isAfter(dropOff)) {
            modelAndView.addObject("reservationError", "Pickup date is after drop off date");
            return true;
        }
        return false;
    }

    private boolean checkingIfDateTimeFieldsAreEmpty(String pickUpDate,
                                                     String pickUpTime,
                                                     String dropOffDate,
                                                     String dropOffTime,
                                                     ModelAndView modelAndView) {
        if (pickUpDate.isEmpty() || pickUpTime.isEmpty() || dropOffDate.isEmpty() || dropOffTime.isEmpty()) {
            modelAndView.addObject("reservationError", "Please enter a date");
            return true;
        }
        return false;
    }

    private LocalDateTime parseLocalDateTime(@RequestParam String pickUpDate, @RequestParam String pickUpTime, ModelAndView modelAndView) {
        LocalDateTime localDateTime = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy h : m a");
            localDateTime = LocalDateTime.parse(pickUpDate + " " + pickUpTime, formatter);
        } catch (Exception e) {

        }
        return localDateTime;
    }

}
