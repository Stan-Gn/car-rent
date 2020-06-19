package com.app.carrent.service;

import com.app.carrent.controller.parser.CarRentDateTimeParser;
import com.app.carrent.exception.*;
import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.model.User;
import com.app.carrent.repository.CarRentRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CarRentService {

    private final CarRentRepositoryInterface carRentRepository;
    private final UserService userService;

    @Autowired
    public CarRentService(CarRentRepositoryInterface carRentRepository, UserService userService) {
        this.carRentRepository = carRentRepository;
        this.userService = userService;
    }

    public CarRent save(CarRent carRent) {
        return carRentRepository.save(carRent);
    }

    public List<CarRent> findDateConflict(LocalDateTime pickUpDate, LocalDateTime dropOffDate, Car car) {
        return carRentRepository.findDateConflict(pickUpDate, dropOffDate, car);
    }

    public Page<CarRent> findAll(Pageable pageRequest) {
        return carRentRepository.findAll(pageRequest);
    }

    public Optional<CarRent> findById(Long id) {
        return carRentRepository.findById(id);
    }

    public void delete(CarRent carRent) {
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


    public void saveReservation(String pickUpDate, String pickUpTime, String dropOffDate, String dropOffTime, Car carToRent, Authentication auth,long id) throws Exception {
        if (checkingIfDateTimeFieldsAreEmpty(pickUpDate, pickUpTime, dropOffDate, dropOffTime))
            throw new DatesToCarReservationAreNotValidException("Please enter a date",id);

        LocalDateTime pickUp = CarRentDateTimeParser.parseLocalDateTime(pickUpDate, pickUpTime);
        LocalDateTime dropOff = CarRentDateTimeParser.parseLocalDateTime(dropOffDate, dropOffTime);

        if (pickUp == null || dropOff == null) {
            throw new DatesToCarReservationAreNotValidException("Invalid date format", id);
        }

        if (checkingIfPickUpDateIsAfterDropOffDate(pickUp, dropOff))
            throw new DatesToCarReservationAreNotValidException("Pickup date is after drop off date",id);

        if (checkingIfPickUpDateIsBeforeNow(pickUp))
            throw new DatesToCarReservationAreNotValidException("Pickup date is before current date.",id);

        List<CarRent> dateConflictCarRentList = findDateConflict(pickUp, dropOff, carToRent);
        checkingDateConflicts(dateConflictCarRentList,id);



        if (auth == null)
            throw new Exception("Reservation portal error");

        Optional<User> user = userService.findUserByEmail(auth.getName());
        if (user.isPresent()) {
            saveCarRent(pickUp, dropOff, carToRent, user);
        } else {
            throw new Exception("Reservation portal error - user not found");
        }
    }

    private void saveCarRent(LocalDateTime pickUp, LocalDateTime dropOff, Car car, Optional<User> user) {
        CarRent carRent = new CarRent();
        carRent.setCar(car);
        carRent.setReturned(false);
        carRent.setPickUpDate(pickUp);
        carRent.setDropOffDate(dropOff);
        carRent.setUser(user.get());
        save(carRent);
    }

    private void checkingDateConflicts(List<CarRent> dateConflictCarRentList,long id) {
        if (!dateConflictCarRentList.isEmpty())
            throw new DatesToCarReservationConflictValidException("There is a conflict between booking dates", dateConflictCarRentList,id);
    }

    private boolean checkingIfPickUpDateIsBeforeNow(LocalDateTime pickUp) {
        return pickUp.isBefore(LocalDateTime.now());
    }

    private boolean checkingIfPickUpDateIsAfterDropOffDate(LocalDateTime pickUp, LocalDateTime dropOff) {
        return (pickUp.isAfter(dropOff));
    }

    private boolean checkingIfDateTimeFieldsAreEmpty(String pickUpDate, String pickUpTime, String dropOffDate, String dropOffTime) {
        return (pickUpDate.isEmpty() || pickUpTime.isEmpty() || dropOffDate.isEmpty() || dropOffTime.isEmpty());
    }


}
