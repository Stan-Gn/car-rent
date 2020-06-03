package com.app.carrent.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class CarRent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDateTime pickUpDate;
    private LocalDateTime dropOffDate;
    private double totalPrice;
    private boolean isReturned;
    @ManyToOne
    private User user;
    @OneToOne
    private Car car;


}
