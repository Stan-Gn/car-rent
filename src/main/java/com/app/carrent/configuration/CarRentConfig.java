package com.app.carrent.configuration;

import com.app.carrent.model.Car;
import com.app.carrent.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class CarRentConfig {
    private final CarService carService;

    @Autowired
    public CarRentConfig(CarService carService) {
        this.carService = carService;
    }

    @Bean
    @Scope("prototype")
    public Page<Car> fiveCars() {
        return carService.findAll(PageRequest.of(0, 5));
    }

    @Bean
    public MultipartResolver multipartResolver() {
        int fileMaxSize = 2*1024*1024;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(fileMaxSize);
        return commonsMultipartResolver;
    }
}
