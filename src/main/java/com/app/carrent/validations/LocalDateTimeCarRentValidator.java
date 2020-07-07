package com.app.carrent.validations;

import java.time.LocalDateTime;

public class LocalDateTimeCarRentValidator {
    public boolean pickUpDateOrDropOfDateIsNull(LocalDateTime pickUpDate, LocalDateTime dropOffDate) {
        return (pickUpDate == null || dropOffDate == null);
    }
    public boolean checkingIfPickUpDateIsAfterDropOffDate(LocalDateTime pickUpDate, LocalDateTime dropOffDate){
        return (pickUpDate.isAfter(dropOffDate));
    }
    public boolean checkingIfPickUpDateIsBeforeNow(LocalDateTime pickUp) {
        return pickUp.isBefore(LocalDateTime.now());
    }
}
