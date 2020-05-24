package com.app.carrent.repository;

import com.app.carrent.model.Car;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CarRepository {

    private List<Car> cars = new ArrayList<>();

    public CarRepository() {
        cars.add(new Car(1, "Opel", "Vectra", 2003, "Wygodne rodzinne auto", 200.00, 0.99));
        cars.add(new Car(2, "Porsche", "911", 2015, "Szybkie auto", 250.00, 1.55));
    }

    public Car findCarById(long id) {
        Optional <Car> foundCar = cars.stream()
                .filter(car -> car.getId() == id)
                .findFirst();

        if(foundCar.isPresent())
            return foundCar.get();
        throw new RuntimeException("Car not found");
    }

    public List<Car> getAllCars (){
        return cars;
    }
}
