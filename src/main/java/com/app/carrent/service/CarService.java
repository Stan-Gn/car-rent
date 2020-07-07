package com.app.carrent.service;


import com.app.carrent.controller.parser.CarRentDateTimeParser;
import com.app.carrent.exception.DatesToFilterAreNotValidException;
import com.app.carrent.exception.ImageFileNotFoundException;
import com.app.carrent.exception.InvalidImageFileExtensionException;
import com.app.carrent.model.Car;
import com.app.carrent.model.ImageFile;
import com.app.carrent.repository.CarRentRepositoryInterface;
import com.app.carrent.repository.CarRepositoryInterface;
import com.app.carrent.validations.LocalDateTimeCarRentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepositoryInterface carRepository;
    private final CarRentRepositoryInterface carRentRepository;

    @Autowired
    public CarService(CarRepositoryInterface carRepository, CarRentRepositoryInterface carRentRepository) {
        this.carRepository = carRepository;
        this.carRentRepository = carRentRepository;
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public Page<Car> findAll(Pageable pageRequest) {
        return carRepository.findAll(pageRequest);
    }

    public Optional<Car> findCarById(long id) {
        return carRepository.findById(id);
    }

    public void delete(Car car) {
        carRepository.delete(car);
    }

    public Car save(Car car, MultipartFile file) throws IOException, InvalidImageFileExtensionException {

        saveImageFileToCar(car, file);

        return carRepository.save(car);
    }

    private void saveImageFileToCar(Car car, MultipartFile file) throws InvalidImageFileExtensionException, IOException {
        if (file == null || file.isEmpty())
            throw new ImageFileNotFoundException("File not loaded");

        if (!(file.getContentType().toLowerCase().contains("jpg") || file.getContentType().toLowerCase().contains("jpeg")))
            throw new InvalidImageFileExtensionException("Invalid file extension");

        ImageFile imageFile = new ImageFile(file.getBytes());
        car.setImageFile(imageFile);
    }

    public Page<Car> getCarsPage(int currentPageNumber,
                                 Sort sort,
                                 Optional<String> pickUpDate,
                                 Optional<String> pickUpTime,
                                 Optional<String> dropOffDate,
                                 Optional<String> dropOffTime) {
        Pageable pageRequest = PageRequest.of(currentPageNumber - 1, 4, sort);

        if (isAllDatesArePresentAndNotEmptyToFilterUnreservedCars(pickUpDate, pickUpTime, dropOffDate, dropOffTime)) {
            LocalDateTime pickUp = CarRentDateTimeParser.parseLocalDateTime(pickUpDate.get(), pickUpTime.get());
            LocalDateTime dropOff = CarRentDateTimeParser.parseLocalDateTime(dropOffDate.get(), dropOffTime.get());
            LocalDateTimeCarRentValidator localDateTimeCarRentValidator = new LocalDateTimeCarRentValidator();
            if (localDateTimeCarRentValidator.pickUpDateOrDropOfDateIsNull(pickUp, dropOff)) {
                throw new DatesToFilterAreNotValidException("Dates to filter are not valid");
            } else if (localDateTimeCarRentValidator.checkingIfPickUpDateIsAfterDropOffDate(pickUp, dropOff)) {
                throw new DatesToFilterAreNotValidException("Dates to filter are not valid - Pick up date is after drop of date ");
            } else if (localDateTimeCarRentValidator.checkingIfPickUpDateIsBeforeNow(pickUp)){
                throw new DatesToFilterAreNotValidException("Dates to filter are not valid - Pick up date is before Today's date");
            }
                return carRentRepository.findCarsNotReserved(pickUp, dropOff, pageRequest);
        } else
            return findAll(pageRequest);
    }

    private boolean isAllDatesArePresentAndNotEmptyToFilterUnreservedCars(Optional<String> pickUpDate, Optional<String> pickUpTime, Optional<String> dropOffDate, Optional<String> dropOffTime) {
        return pickUpDate.isPresent() && !pickUpDate.get().isEmpty()
                && pickUpTime.isPresent() && !pickUpTime.get().isEmpty()
                && dropOffDate.isPresent() && !dropOffDate.get().isEmpty()
                && dropOffTime.isPresent() && !dropOffTime.get().isEmpty();
    }
}
