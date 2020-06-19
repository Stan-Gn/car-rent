package com.app.carrent.service;


import com.app.carrent.controller.parser.CarRentDateTimeParser;
import com.app.carrent.exception.DatesToFilterAreNotValidException;
import com.app.carrent.model.Car;
import com.app.carrent.repository.CarRentRepositoryInterface;
import com.app.carrent.repository.CarRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private CarRepositoryInterface carRepository;
    private CarRentRepositoryInterface carRentRepository;

    @Autowired
    public CarService(CarRepositoryInterface carRepository,CarRentRepositoryInterface carRentRepository) {
        this.carRepository = carRepository;
        this.carRentRepository = carRentRepository;
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

    public Page<Car> getCarsPage(int currentPageNumber,
                                 Sort sort,
                                 Optional<String> pickUpDate,
                                 Optional<String> pickUpTime,
                                 Optional<String> dropOffDate,
                                 Optional<String> dropOffTime) {
        Pageable pageRequest = PageRequest.of(currentPageNumber - 1, 4, sort);

        if (isAllDatesArePresentAndNotEmptyToFilterUnreservedCars(pickUpDate, pickUpTime, dropOffDate, dropOffTime)) {
            LocalDateTime pickUp = CarRentDateTimeParser.parseLocalDateTime(pickUpDate.get(), pickUpTime.get());
            LocalDateTime dropOff = CarRentDateTimeParser.parseLocalDateTime(dropOffDate.get(), dropOffTime.get());
            if (pickUp != null && dropOff != null) {
                return carRentRepository.findCarsNotReserved(pickUp, dropOff, pageRequest);
            }
            else throw new DatesToFilterAreNotValidException("Dates to filter are not valid");
        } else
            return findAll(pageRequest);
    }
    private boolean isAllDatesArePresentAndNotEmptyToFilterUnreservedCars(Optional<String> pickUpDate, Optional<String> pickUpTime, Optional<String> dropOffDate, Optional<String> dropOffTime) {
        return pickUpDate.isPresent() && !pickUpDate.get().isEmpty()
                && pickUpTime.isPresent() && !pickUpTime.get().isEmpty()
                && dropOffDate.isPresent() && !dropOffDate.get().isEmpty()
                && dropOffTime.isPresent() && !dropOffTime.get().isEmpty();
    }
}
