package com.app.carrent.repository;

import com.app.carrent.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CarRepositoryInterface extends JpaRepository<Car, Long> {

}
