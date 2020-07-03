package com.app.carrent.dataLoader;

import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.model.ImageFile;
import com.app.carrent.model.User;
import com.app.carrent.repository.CarRentRepositoryInterface;
import com.app.carrent.repository.CarRepositoryInterface;
import com.app.carrent.repository.UserRepositoryInterface;
import com.app.carrent.service.CarRentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataLoader {
    private UserRepositoryInterface userRepository;
    private CarRepositoryInterface carRepository;
    private CarRentRepositoryInterface carRentRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepositoryInterface userRepository, CarRepositoryInterface carRepository, CarRentRepositoryInterface carRentRepository, PasswordEncoder passwordEncoder) throws IOException {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.carRentRepository = carRentRepository;
        this.passwordEncoder = passwordEncoder;
        loadDataIntoDatabase();
    }

    private void loadDataIntoDatabase() throws IOException {
        loadUsers();
        loadCars();
        loadCarRentals();
    }

    private void loadCars() throws IOException {
        carRepository.save(new Car("Audi", "A6", Car.CarType.KOMBI, 220, 0.55, "Fast kombi car", 2015, laodImage()));
        carRepository.save(new Car("Mercedes", "S", Car.CarType.SEDAN, 250, 0.85, "Luxury car", 2016, laodImage()));
        carRepository.save(new Car("Mazda", "6", Car.CarType.KOMBI, 230, 0.45, "Family car", 2016, laodImage()));
        carRepository.save(new Car("BMW", "5", Car.CarType.KOMBI, 180, 0.65, "Car with a powerful enginei", 2015, laodImage()));
        carRepository.save(new Car("Renault", "Clio", Car.CarType.SEDAN, 100, 0.85, "Small car", 2016, laodImage()));
        carRepository.save(new Car("Mazda", "3", Car.CarType.KOMBI, 150, 0.45, "Economical car", 2016, laodImage()));
    }

    private ImageFile laodImage() throws IOException {
        ImageFile imageFile = new ImageFile();
        BufferedImage bImage = ImageIO.read(new File("src/main/resources/static/assets/images/cars/1.jpg"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        byte [] data = bos.toByteArray();
        imageFile.setData(data);
        return imageFile;
    }

    private void loadUsers() {
        userRepository.save(getUser("Andrzej", "S", "andrzej@andrzej.pl", "andrzej", User.Role.USER));
        userRepository.save(getUser("Ania", "N", "ania@ania.pl", "ania", User.Role.USER));
    }

    private User getUser(String n, String sr, String em, String ps, User.Role role) {
        User user = new User();
        user.setName(n);
        user.setSurname(sr);
        user.setEmail(em);
        user.setPassword(passwordEncoder.encode(ps));
        user.setRole(role);
        user.setEnabled(true);
        user.setNonLocked(true);
        return user;
    }


    private void loadCarRentals() {
        CarRent carRentFirst = getCarRent(1, 1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3));
        CarRent carRentSecond = getCarRent(2, 2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(10));

        save(carRentFirst);
        save(carRentSecond);

    }

    private void save(CarRent carRent) {
        if (carRent != null) {
            carRentRepository.save(carRent);
        }
    }

    private CarRent getCarRent(long carId, long userId, LocalDateTime pickUp, LocalDateTime dropOff) {

        Optional<Car> carOptional = carRepository.findById(carId);
        Optional<User> userOptional = userRepository.findById(userId);

        CarRent carRent = new CarRent();
        if (userOptional.isPresent() && carOptional.isPresent()) {
            carRent.setUser(userOptional.get());
            carRent.setCar(carOptional.get());
            carRent.setPickUpDate(pickUp);
            carRent.setDropOffDate(dropOff);
            return carRent;
        }
        return null;
    }
}
