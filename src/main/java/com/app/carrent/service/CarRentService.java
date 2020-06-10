package com.app.carrent.service;

import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.repository.CarRentRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarRentService {

    private CarRentRepositoryInterface carRentRepository;

    @Autowired
    public CarRentService(CarRentRepositoryInterface carRentRepository) {
        this.carRentRepository = carRentRepository;
    }

    public CarRent save(CarRent carRent){
        return carRentRepository.save(carRent);
    }

    public List<CarRent> findDateConflict(LocalDateTime pickUpDate, LocalDateTime dropOffDate, Car car){
        return carRentRepository.findDateConflict(pickUpDate,dropOffDate,car);
    }
    public Page<Car> findCarsNotReserved(LocalDateTime pickUp, LocalDateTime dropOff, Pageable pageRequest){
        return carRentRepository.findCarsNotReserved(pickUp,dropOff,pageRequest);
    }

    public Page<CarRent> findAll(Pageable pageRequest) {
        return carRentRepository.findAll(pageRequest);
    }

    public Optional<CarRent> findById(Long id) {
        return carRentRepository.findById(id);
    }

    public void delete(CarRent carRent){
        carRentRepository.delete(carRent);
    }
}
