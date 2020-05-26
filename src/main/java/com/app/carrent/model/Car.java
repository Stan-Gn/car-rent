package com.app.carrent.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
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
