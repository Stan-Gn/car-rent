package com.app.carrent.controller;

import com.app.carrent.controller.modelSaver.PageAndPageNumbersModelSaver;
import com.app.carrent.model.User;
import com.app.carrent.service.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class AdminController {

    private UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public ModelAndView admin() {
        ModelAndView modelAndView = new ModelAndView("admin");
        return modelAndView;
    }

    @GetMapping("/user-list-admin")
    public ModelAndView userList(@RequestParam Optional<Integer> pageNumber) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("user-list-adm");
        pageNumber.orElse(1);
        int currentPage = pageNumber.orElse(1);
        Pageable pageRequest = PageRequest.of(currentPage - 1, 5);
        Page<User> usersPage = userService.findAll(pageRequest);
        modelAndView.addObject("usersPage", usersPage);

        PageAndPageNumbersModelSaver pageAndPageNumbersModelSaver = new PageAndPageNumbersModelSaver();
        pageAndPageNumbersModelSaver.saveToModel(modelAndView,"usersPage",usersPage,currentPage);

        return modelAndView;
    }

    @GetMapping("/user-list-admin/{action}")
    public String userList(@PathVariable(value = "action") String action,
                           @RequestParam(value = "id") Long id,
                           @RequestParam(value = "isEnabled", required = false) boolean isEnabled) {
        Optional<User> userOptional = null;
        userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            switch (action) {
                case "remove":
                    userOptional.ifPresent(user -> userService.deleteUser(user));
                    break;
                case "changeLockedStatus":
                    User u = userOptional.get();
                    u.setNonLocked(!u.isNonLocked());
                    userService.saveUser(u);
                    break;
            }
        }

        return "redirect:/user-list-admin";
    }

    @GetMapping("/car-rent-list-admin")
    public ModelAndView carRentList() {
        ModelAndView modelAndView = new ModelAndView("car-rent-list-adm");
        return modelAndView;
    }

    @GetMapping("/car-list-admin")
    public ModelAndView carList() {
        ModelAndView modelAndView = new ModelAndView("car-list-adm");
        return modelAndView;
    }
}
