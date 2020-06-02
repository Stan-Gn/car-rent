package com.app.carrent.controller;

import com.app.carrent.model.Car;
import com.app.carrent.service.CarService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class CarRentController {

    private CarService carService;

    @Autowired
    public CarRentController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("cars", carService.findAll());
        return modelAndView;
    }

    @GetMapping("/cars")
    private ModelAndView cars(@RequestParam("page") Optional<Integer>page) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("car-list-one");

        int currentPage = page.orElse(1);
        Page<Car> carsPage = carService.findAll(PageRequest.of(currentPage -1,4));
        if(currentPage>carsPage.getTotalPages())
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
        modelAndView.addObject("cars", carService.findAll());
        return modelAndView;
    }

}
