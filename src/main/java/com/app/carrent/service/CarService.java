package com.app.carrent.service;


import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.repository.CarRentRepositoryInterface;
import com.app.carrent.repository.CarRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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

    public Optional<Car> findCarById(long id) {
        return carRepository.findById(id);
    }

    public void delete(Car car) {
        carRepository.delete(car);
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }
}
