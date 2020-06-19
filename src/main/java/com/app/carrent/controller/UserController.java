package com.app.carrent.controller;

import com.app.carrent.model.User;
import com.app.carrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");
        return modelAndView;
    }

    @GetMapping("/registration")
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView("registration");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/registration")
    public String saveUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors())
            return "registration";

        if (userService.findUserByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailExists", "The user already exists with this email address");
            return "registration";
        }

        userService.saveUser(user);
        model.addAttribute("regSuccess", "registration completed successfully");
        return "registration";
    }

    @GetMapping("/contact")
    public ModelAndView contact() {
        ModelAndView modelAndView = new ModelAndView("contact");
        return modelAndView;
    }

    @GetMapping ("accessDenied")
    public ModelAndView accessDenied(){
        ModelAndView modelAndView = new ModelAndView("accessDenied");
        return modelAndView;
    }



}
