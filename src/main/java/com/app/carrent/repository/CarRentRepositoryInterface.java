package com.app.carrent.repository;

import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRentRepositoryInterface extends JpaRepository<CarRent, Long> {
    @Query("Select cr from CarRent cr WHERE " +
            "(:car) = cr.car " +
            "and ((:start) between cr.pickUpDate and cr.dropOffDate " +
            "or (:end) between cr.pickUpDate and cr.dropOffDate " +
            "or cr.pickUpDate >= (:start) and cr.pickUpDate<= (:end))")
    List<CarRent> findDateConflict(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("car") Car car);

    @Query(value = "SELECT c from Car c LEFT Join CarRent cr on c=cr.car where not " +
            "((:start)  between cr.pickUpDate and cr.dropOffDate " +
            " or (:end)  between cr.pickUpDate and cr.dropOffDate " +
            "or cr.pickUpDate >= (:start) and cr.pickUpDate<= (:end)) or cr.car is null ")
    Page<Car> findCarsNotReserved(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end, Pageable pageRequest);

}
