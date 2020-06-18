package com.app.carrent.service;

import com.app.carrent.exception.CarIsReturnedInAdminPanelActionException;
import com.app.carrent.exception.InvalidDistanceValueInPanelAdminException;
import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.repository.CarRentRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public void actionOnRentItemInAdminPanel(String action, Optional<String> distanceOptional, CarRent carRent) throws CarIsReturnedInAdminPanelActionException, InvalidDistanceValueInPanelAdminException {
        switch (action) {
            case "delete":
                carRentRepository.delete(carRent);
                break;
            case "confirm":
                if (carRent.isReturned()) {
                    throw new CarIsReturnedInAdminPanelActionException("The car has been returned");
                } else if (distanceIsIncorrect(distanceOptional)) {
                    throw new InvalidDistanceValueInPanelAdminException("Invalid distance value");
                } else {
                    setReturnDateIfIsEarlierThanCurrentDate(carRent);
                    calculationOfTotalAmountDue(distanceOptional, carRent);
                    carRentRepository.save(carRent);
                }
                break;
        }
    }

    private boolean distanceIsIncorrect(Optional<String> distanceOptional) {
        return !distanceOptional.isPresent() || getDoubleDistanceFromString(distanceOptional) == null;
    }

    private Double getDoubleDistanceFromString(Optional<String> distanceOptional) {
        try {
            return Double.parseDouble(distanceOptional.get());
        } catch (Exception e) {
            return null;
        }
    }
    private void setReturnDateIfIsEarlierThanCurrentDate(CarRent carRent) {
        if (LocalDateTime.now().isAfter(carRent.getDropOffDate())) {
            carRent.setDropOffDate(LocalDateTime.now());
        }
    }

    private void calculationOfTotalAmountDue(@RequestParam(value = "distance", required = false) Optional<String> distanceOptional, CarRent carRent) {
        double distance = getDoubleDistanceFromString(distanceOptional);
        double days = ChronoUnit.HOURS.between(carRent.getPickUpDate(), carRent.getDropOffDate()) / 24.0;
        double pricePerDay = carRent.getCar().getPricePerDay();
        double pricePerKM = carRent.getCar().getPricePerKm();

        BigDecimal totalPrice = BigDecimal.valueOf(days * pricePerDay + distance * pricePerKM).setScale(2, BigDecimal.ROUND_HALF_UP);
        carRent.setDistance(distance);
        carRent.setTotalPrice(totalPrice);
        carRent.setReturned(true);
    }
}
