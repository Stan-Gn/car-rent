package com.app.carrent.controller;


import com.app.carrent.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CarRentController {

    private CarService carService;

    @Autowired
    public CarRentController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("cars", carService.getAllCars());
        return modelAndView;
    }
    @GetMapping("/cars")
    private ModelAndView cars(){
        ModelAndView modelAndView = new ModelAndView("car-list-one");
        modelAndView.addObject("cars",carService.getAllCars());
        return modelAndView;
    }

    @GetMapping("/reservation")
    public ModelAndView reservation(@RequestParam long id){
        ModelAndView modelAndView = new ModelAndView("reservation");
        modelAndView.addObject("carToRent", carService.findCarById(id));
        modelAndView.addObject("cars",carService.getAllCars());
        return modelAndView;
    }

}
