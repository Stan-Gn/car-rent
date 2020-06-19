package com.app.carrent.exception;

import com.app.carrent.model.CarRent;

import java.util.List;

public class DatesToCarReservationConflictValidException extends DatesToCarReservationAreNotValidException {
    private final List<CarRent> dateConflictCarRentList;
    public DatesToCarReservationConflictValidException(String mess, List<CarRent> dateConflictCarRentList, long id) {
        super(mess,id);
        this.dateConflictCarRentList = dateConflictCarRentList;
    }

    public List<CarRent> getDateConflictCarRentList() {
        return dateConflictCarRentList;
    }
}
