package com.app.carrent.service;

import com.app.carrent.exception.IOImageFileTransferException;
import com.app.carrent.exception.ImageFileNotFoundException;
import com.app.carrent.exception.InvalidImageFileExtensionException;
import com.app.carrent.model.Car;
import org.springframework.web.multipart.MultipartFile;

public interface CarImageServiceInterface {
    void save(MultipartFile file, Car car) throws ImageFileNotFoundException, InvalidImageFileExtensionException, IOImageFileTransferException;
}
