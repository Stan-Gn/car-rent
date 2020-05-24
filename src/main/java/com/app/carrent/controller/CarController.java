package com.app.carrent.controller;


import com.app.carrent.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CarController {

    private CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars")
    public ModelAndView cars(){
        ModelAndView modelAndView = new ModelAndView("cars");
        modelAndView.addObject("cars", carService.getAllCars());
        return modelAndView;
    }

}
