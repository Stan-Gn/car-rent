package com.app.carrent.configuration;

import com.app.carrent.model.Car;
import com.app.carrent.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Configuration
public class CarRentConfig {
    private CarService carService;

    @Autowired
    public CarRentConfig(CarService carService) {
        this.carService = carService;
    }

    @Bean
    public Page<Car> fiveCars() {
        return carService.findAll(PageRequest.of(0, 5));
    }
}
