package com.app.carrent.service;

import com.app.carrent.model.Token;
import com.app.carrent.model.User;
import com.app.carrent.repository.TokenRepositoryInterface;
import com.app.carrent.repository.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepositoryInterface userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepositoryInterface tokenRepository;
    private final MailService mailService;
    private final HttpServletRequest request;

    @Autowired
    public UserService(UserRepositoryInterface userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenRepositoryInterface tokenRepository,
                       MailService mailService,HttpServletRequest request) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
        this.request = request;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) throws MessagingException {
        user.setEnabled(false);
        user.setRole(User.Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        String url = request.getRequestURL().toString();
        sendToken(user, url);
        return savedUser;
    }

    public Page<User> findAll(Pageable pageRequest) {
        return userRepository.findAll(pageRequest);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void adminActionOnUser(String action, User user) {
        if ("changeLockedStatus".equals(action)) {
            user.setNonLocked(!user.isNonLocked());
            userRepository.save(user);
        }
    }

    private void sendToken(User user, String url) throws MessagingException {
        String tokenValue = UUID.randomUUID().toString();
        Token token = new Token();
        token.setValue(tokenValue);
        token.setUser(user);
        tokenRepository.save(token);

        String tokenUrl = url+"/token?value=" + tokenValue;

        mailService.sendMail(user.getEmail(), "Activation", tokenUrl, false);


    }

}
