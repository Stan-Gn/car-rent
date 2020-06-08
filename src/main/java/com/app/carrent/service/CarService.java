package com.app.carrent.service;


import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.repository.CarRentRepositoryInterface;
import com.app.carrent.repository.CarRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private CarRepositoryInterface carRepository;

    @Autowired
    public CarService(CarRepositoryInterface carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }
    public Page<Car> findAll(Pageable pageRequest) {
        return carRepository.findAll(pageRequest);
    }

    public Car findCarById(long id) {
        //todo exception handling
        Optional<Car> car = carRepository.findById(id);
        return car.get();
    }

}
