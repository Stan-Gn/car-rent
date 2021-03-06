package com.app.carrent.controller;

import com.app.carrent.controller.modelSaver.PageAndPageNumbersModelSaver;
import com.app.carrent.exception.*;
import com.app.carrent.model.Car;
import com.app.carrent.model.CarRent;
import com.app.carrent.model.User;
import com.app.carrent.service.*;
import javassist.NotFoundException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


@Controller
public class AdminController {

    private final UserService userService;
    private final CarRentService carRentService;
    private final CarService carService;


    @Autowired
    public AdminController(UserService userService, CarRentService carRentService, CarService carService) {
        this.userService = userService;
        this.carRentService = carRentService;
        this.carService = carService;
    }

    @GetMapping("/admin")
    public ModelAndView admin() {
        return new ModelAndView("admin");
    }

    @GetMapping("/admin/user-list-admin")
    public ModelAndView userList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("user-list-adm");

        displayingListOfCarsInAdminPanel(pageNumber, modelAndView);

        return modelAndView;
    }

    private void displayingListOfCarsInAdminPanel(@RequestParam Optional<Integer> pageNumber, ModelAndView modelAndView) throws NotFoundException {
        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<User> usersPage = userService.findAll(pageRequest);

        if (usersPage.getTotalPages() > 0) {
            modelAndView.addObject("usersPage", usersPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "usersPage", usersPage, currentPage);
        }
    }

    @GetMapping("/admin/user-list-admin/{action}")
    public ModelAndView userListAction(@PathVariable(value = "action") String action,
                                       @RequestParam(value = "id") Long id) throws UserNotFoundToActionInAdminPanel {
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/user-list-admin");
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent())
            throw new UserNotFoundToActionInAdminPanel("Failed to find user with given id");
        userService.adminActionOnUser(action, userOptional.get());
        return modelAndView;
    }


    @GetMapping("/admin/car-rent-list-admin")
    public ModelAndView carRentList(@RequestParam Optional<Integer> pageNumber) throws Exception {
        ModelAndView modelAndView = new ModelAndView("car-rent-list-adm");

        displayingListOfRentals(pageNumber, modelAndView);

        return modelAndView;
    }

    private void displayingListOfRentals(@RequestParam Optional<Integer> pageNumber, ModelAndView modelAndView) throws Exception {
        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<CarRent> carRentPage = carRentService.findAll(pageRequest);
        if (carRentPage.getTotalPages() > 0) {
            modelAndView.addObject("carRentPage", carRentPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "carRentPage", carRentPage, currentPage);
        }
    }

    @GetMapping("/admin/car-rent-list-admin/{action}")
    public ModelAndView carRentListAction(@PathVariable(value = "action") String action,
                                          @RequestParam(value = "id") Long id,
                                          @RequestParam(value = "distance", required = false) Optional<String> distanceOptional) throws Exception {
        ModelAndView modelAndView = new ModelAndView("forward:/admin/car-rent-list-admin");

        Optional<CarRent> carRentOptional = carRentService.findById(id);

        if (carRentOptional.isPresent()) {
            carRentService.actionOnRentItemInAdminPanel(action, distanceOptional, carRentOptional.get());
        } else {
            throw new CarRentNotFoundToActionInAdminPanelException("Failed to find user with given id");
        }

        return modelAndView;
    }

    @GetMapping("/admin/car-list-admin")
    public ModelAndView carList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("car-list-adm");

        displayCarItemsInPanelAdmin(pageNumber, modelAndView);

        return modelAndView;
    }

    private void displayCarItemsInPanelAdmin(@RequestParam Optional<Integer> pageNumber, ModelAndView modelAndView) throws NotFoundException {
        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<Car> carsPage = carService.findAll(pageRequest);

        if (carsPage.getTotalPages() > 0) {
            modelAndView.addObject("carsPage", carsPage);
            PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
            pageAndPageNumbersModelSaver.saveToModel(modelAndView, "carsPage", carsPage, currentPage);
        }
    }

    @GetMapping("/admin/car-list-admin/addCar")
    public ModelAndView addCar() {
        ModelAndView modelAndView = new ModelAndView("addNewCar");
        modelAndView.addObject("car", new Car());
        return modelAndView;
    }

    @PostMapping("/admin/car-list-admin/addCar")
    public ModelAndView addCar(@ModelAttribute(value = "car") @Valid Car car, Errors errors,
                               @RequestParam(value = "file") MultipartFile file) throws IOException, InvalidImageFileExtensionException {
        ModelAndView modelAndView = new ModelAndView("addNewCar");

        if (errors.hasErrors()) {
            modelAndView.addObject("addCarError", errors);
            return modelAndView;
        }

        carService.save(car,file);
        modelAndView.addObject("success", "Correctly added car");

        return modelAndView;
    }

    @ModelAttribute("carTypeList")
    public void carTypeLis(Model model) {
        model.addAttribute(Arrays.asList(Car.CarType.values()));
    }
}
