package com.app.carrent.service;

import com.app.carrent.exception.IOImageFileTransferException;
import com.app.carrent.exception.ImageFileNotFoundException;
import com.app.carrent.exception.InvalidImageFileExtensionException;
import com.app.carrent.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

@Service
public class CarImageServiceImpl implements CarImageServiceInterface {

    private final ServletContext context;

    @Autowired
    public CarImageServiceImpl(ServletContext context) {
        this.context = context;
    }

    public void save(MultipartFile file, Car car) throws ImageFileNotFoundException, InvalidImageFileExtensionException, IOImageFileTransferException {
        String path = context.getRealPath("/") + "upload\\" + car.getMark() + car.getModel() + car.getId();

        if (file == null || file.isEmpty())
            throw new ImageFileNotFoundException("File not loaded");

        if (!(file.getContentType().toLowerCase().contains("jpg") || file.getContentType().toLowerCase().contains("jpeg")))
            throw new InvalidImageFileExtensionException("Invalid file extension");

        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            throw new IOImageFileTransferException("Failed to save the image. " + e.getMessage());
        }

    }
}
