package com.app.carrent.service;

import com.app.carrent.model.User;
import com.app.carrent.repository.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService  {

    private UserRepositoryInterface userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepositoryInterface userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUserById(long id){
        //todo exception handling
        return userRepository.findById(id).get();
    }

    public User saveUser(User user){
        user.setEnable(true); //todo false -> when activation by e-mail
        user.setRole(User.Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
