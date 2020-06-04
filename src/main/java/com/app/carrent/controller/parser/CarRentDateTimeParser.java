package com.app.carrent.controller.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CarRentDateTimeParser {
    public static LocalDateTime parseLocalDateTime(String date, String time) {
        LocalDateTime localDateTime = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy h : m a");
            localDateTime = LocalDateTime.parse(date + " " + time, formatter);
        } catch (Exception e) {

        }
        return localDateTime;
    }

}
