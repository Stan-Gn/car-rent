package com.app.carrent.controller;

import com.app.carrent.model.User;
import com.app.carrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView("registration");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/saveUser")
    public String saveUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors())
            return "registration";

        model.addAttribute("regSuccess", "registration completed successfully");
        userService.saveUser(user);
        return "registration";
    }
}
