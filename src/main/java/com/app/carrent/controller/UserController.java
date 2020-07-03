package com.app.carrent.controller;

import com.app.carrent.exception.TokenValueNotFoundException;
import com.app.carrent.model.Token;
import com.app.carrent.model.User;
import com.app.carrent.repository.TokenRepositoryInterface;
import com.app.carrent.repository.UserRepositoryInterface;
import com.app.carrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;


@Controller
public class UserController {

    private final UserService userService;
    private final UserRepositoryInterface userRepository;
    private final TokenRepositoryInterface tokenRepository;

    @Autowired
    public UserController(UserService userService, UserRepositoryInterface userRepository, TokenRepositoryInterface tokenRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
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
    public String saveUser(@Valid User user, BindingResult bindingResult, Model model , HttpServletRequest request) throws MessagingException {
        if (bindingResult.hasErrors())
            return "registration";

        if (userService.findUserByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("registrationError", "The user already exists with this email address");
            return "registration";
        }

        userService.saveUser(user);
        model.addAttribute("regSuccess", "activate account - click the link sent to the email address provided in the registration form");
        return "registration";
    }

    @GetMapping("/registration/token")
    public String registrationToken(@RequestParam String value, RedirectAttributes redirectAttributes) throws TokenValueNotFoundException {
        redirectAttributes.addFlashAttribute("regSuccess", "registration completed successfully");
        Optional<Token> tokenOptional = tokenRepository.findByValue(value);
        if (tokenOptional.isPresent()) {
            User user = tokenOptional.get().getUser();
            user.setEnabled(true);
            userRepository.save(user);
        } else throw new TokenValueNotFoundException("Token does not exist");

        return "redirect:/registration";
    }

    @GetMapping("/contact")
    public ModelAndView contact() {
        ModelAndView modelAndView = new ModelAndView("contact");
        return modelAndView;
    }

    @GetMapping("accessDenied")
    public ModelAndView accessDenied() {
        ModelAndView modelAndView = new ModelAndView("accessDenied");
        return modelAndView;
    }


}
