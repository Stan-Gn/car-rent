package com.app.carrent.service;

import com.app.carrent.model.Car;
import org.springframework.web.multipart.MultipartFile;

public interface CarImageServiceInterface {
    boolean save(MultipartFile file, Car car);
}
