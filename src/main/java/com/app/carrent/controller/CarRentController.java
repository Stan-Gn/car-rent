package com.app.carrent.controller;

import com.app.carrent.controller.modelSaver.PageAndPageNumbersModelSaver;
import com.app.carrent.exception.CarToRentNotFoundException;
import com.app.carrent.model.Car;
import com.app.carrent.service.CarRentService;
import com.app.carrent.service.CarService;
import com.app.carrent.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class CarRentController {

    private final CarService carService;
    private final CarRentService carRentService;

    @Autowired
    public CarRentController(CarService carService, CarRentService carRentService) {
        this.carService = carService;
        this.carRentService = carRentService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("cars", carService.findAll());
        return modelAndView;
    }

    @GetMapping("/cars")
    private ModelAndView cars(@RequestParam("page") Optional<Integer> pageNumber,
                              Sort sort,
                              @RequestParam Optional<String> pickUpDate,
                              @RequestParam Optional<String> pickUpTime,
                              @RequestParam Optional<String> dropOffDate,
                              @RequestParam Optional<String> dropOffTime, Model model) throws NotFoundException {

        ModelAndView modelAndView = new ModelAndView("car-list-one");
        int currentPageNumber = pageNumber.orElse(1);

        Page<Car> carsPage = carService.getCarsPage(currentPageNumber, sort, pickUpDate, pickUpTime, dropOffDate, dropOffTime);

        if (carsPage.getTotalPages() > 0) {
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "carsPage", carsPage, currentPageNumber);
        }

        return modelAndView;
    }

    @GetMapping("/reservation")
    public ModelAndView reservation(@RequestParam long id) throws CarToRentNotFoundException {
        ModelAndView modelAndView = new ModelAndView("reservation");
        Optional<Car> carOptional = carService.findCarById(id);
        if (!carOptional.isPresent())
            throw new CarToRentNotFoundException("Car with given id does not exist");
        modelAndView.addObject("carToRent", carOptional.get());
        return modelAndView;
    }

    @PostMapping("/reservation")
    public ModelAndView reservation(@RequestParam long id,
                                    @RequestParam String pickUpDate,
                                    @RequestParam String pickUpTime,
                                    @RequestParam String dropOffDate,
                                    @RequestParam String dropOffTime,
                                    Authentication auth) throws Exception {
        ModelAndView modelAndView = new ModelAndView("reservation");
        Optional<Car> carOptional = carService.findCarById(id);

        if (!carOptional.isPresent()) {
            throw new CarToRentNotFoundException("Car with given id does not exist");
        }
        Car carToRent = carOptional.get();
        modelAndView.addObject("carToRent", carToRent);

        carRentService.saveReservation(pickUpDate, pickUpTime, dropOffDate, dropOffTime,carToRent,auth,id);

        modelAndView.addObject("successReservation", "The car was successfully booked");

        return modelAndView;
    }

}
