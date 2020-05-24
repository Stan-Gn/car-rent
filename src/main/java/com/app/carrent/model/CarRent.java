package com.app.carrent.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@Setter
public class CarRent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date since;
    private Date till;
    private double totalPrice;
    private boolean isReturned;
    @ManyToOne
    private User user;
    @OneToOne
    private Car car;


}
