package com.app.carrent.dataLoader;

import com.app.carrent.model.Car;
import com.app.carrent.repository.CarRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarDataLoader {
    private CarRepositoryInterface carRepository;

    @Autowired
    public CarDataLoader(CarRepositoryInterface carRepository) {
        this.carRepository = carRepository;
        fillDataBase();
    }

    private void fillDataBase() {
        Car car1 = getCar("Audi", "A6", Car.CarType.KOMBI, 220, 0.55, "Szybkie kombi", 2015);
        Car car2 = getCar("Mercedes", "S", Car.CarType.SEDAN, 250, 0.85, "Luksus", 2016);
        Car car3 = getCar("Mazda", "6" , Car.CarType.KOMBI, 230,0.45,"Rodzinne autko",2016);
        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);

    }

    private Car getCar(String mark, String model, Car.CarType type, double ppd, double ppkm, String desc, int year) {
        Car car = new Car();
        car.setMark(mark);
        car.setModel(model);
        car.setCarType(type);
        car.setPricePerDay(ppd);
        car.setPricePerKm(ppkm);
        car.setDescription(desc);
        car.setYear(year);
        return car;
    }


}
