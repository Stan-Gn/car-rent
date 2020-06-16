package com.app.carrent.service;

import com.app.carrent.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;

@Service
public class CarImageServiceImpl implements CarImageServiceInterface {

    private ServletContext context;

    @Autowired
    public CarImageServiceImpl(ServletContext context) {
        this.context = context;
    }

    public boolean save(MultipartFile file, Car car) {
        String path = context.getRealPath("/") + "upload\\" + car.getMark() + car.getModel() + car.getId();
        boolean savedSuccessfully = false;

        if ((file != null || !file.isEmpty()) &&
                (file.getContentType().toLowerCase().contains("jpg") || file.getContentType().toLowerCase().contains("jpeg"))) {
            try {
                savedSuccessfully = true;
                file.transferTo(new File(path));
            } catch (Exception e) {
                savedSuccessfully = false;
            }
        }
        return savedSuccessfully;
    }
}
