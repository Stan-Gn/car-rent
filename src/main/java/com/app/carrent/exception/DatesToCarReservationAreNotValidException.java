package com.app.carrent.exception;

public class DatesToCarReservationAreNotValidException extends RuntimeException {
    private final long id;
    public DatesToCarReservationAreNotValidException(String mess,long id) {
        super(mess);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
