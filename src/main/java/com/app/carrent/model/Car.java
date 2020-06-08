package com.app.carrent.model;

import lombok.AllArgsConstructor;
import lombok.Data;


import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String mark;
    private String model;
    private int year;
    private CarType carType;
    private String description;
    private double pricePerDay;
    private double pricePerKm;


    public enum CarType {
        KOMBI, COUPE, SEDAN, SUV, HATCHBACK
    }

    public Car() {
    }
}
