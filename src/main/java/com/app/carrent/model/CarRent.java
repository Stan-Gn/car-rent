package com.app.carrent.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class CarRent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime pickUpDate;
    private LocalDateTime dropOffDate;
    private BigDecimal totalPrice;
    private double distance;
    private boolean isReturned = false;
    @ManyToOne
    private User user;
    @OneToOne
    private Car car;


}
