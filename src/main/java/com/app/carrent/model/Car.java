package com.app.carrent.model;

import lombok.AllArgsConstructor;
import lombok.Data;


import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Mark field cannot be empty")
    private String mark;

    @NotBlank(message = "Mark field cannot be empty")
    private String model;

    private int year;

    @NotNull(message = "Type field cannot be empty")
    private CarType carType;

    @NotBlank (message = "Description field cannot be empty")
    private String description;

    @Min(value = 0,message = "The value should be greater than zero")
    private double pricePerDay;

    @Min(value = 0,message = "The value should be greater than zero")
    private double pricePerKm;

    @OneToOne(cascade = {CascadeType.ALL})
    private ImageFile imageFile;

    public Car(String mark, String model, CarType type, double ppd, double ppkm, String desc, int year, ImageFile imageFile) {
        this.mark = mark;
        this.model = model;
        this.carType = type;
        this.pricePerDay = ppd;
        this.pricePerKm = ppkm;
        this.description = desc;
        this.year = year;
        this.imageFile = imageFile;
    }

    public enum CarType {
        KOMBI(1), COUPE(2), SEDAN(3), SUV(4), HATCHBACK(5);
        final int id;

        CarType(int id) {
            this.id = id;
        }
    }

    public Car() {
    }
}
