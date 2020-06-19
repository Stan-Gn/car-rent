package com.app.carrent.service;

import com.app.carrent.model.User;
import com.app.carrent.repository.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService  {

    private UserRepositoryInterface userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepositoryInterface userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user){
        user.setEnabled(true); //todo false -> when activation by e-mail
        user.setRole(User.Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public Page<User> findAll(Pageable pageRequest) {
        return userRepository.findAll(pageRequest);
    }
    public Optional<User>findById(Long id){
        return userRepository.findById(id);
    }
    public void adminActionOnUser(String action, User userOptional) {
        if ("changeLockedStatus".equals(action)) {
            User u = userOptional;
            u.setNonLocked(!u.isNonLocked());
            userRepository.save(u);
        }
    }
}
