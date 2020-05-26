package com.app.carrent.service;


import com.app.carrent.model.Car;
import com.app.carrent.repository.CarRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private CarRepositoryInterface carRepository;

    @Autowired
    public CarService(CarRepositoryInterface carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car findCarById(long id) {
        //todo exception handling
        Optional<Car> car = carRepository.findById(id);
        return car.get();
    }

}
