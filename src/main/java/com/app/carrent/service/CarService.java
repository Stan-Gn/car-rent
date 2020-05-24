package com.app.carrent.service;


import com.app.carrent.model.Car;
import com.app.carrent.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car findCarById (long id){
        return carRepository.findCarById(id);
    }

    public List<Car> getAllCars (){
        return carRepository.getAllCars();
    }
}
