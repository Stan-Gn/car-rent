package com.app.carrent.service;

import com.app.carrent.model.User;
import com.app.carrent.repository.UserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepositoryInterface userRepository;

    @Autowired
    public UserService(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(long id){
        //todo exception handling
        return userRepository.findById(id).get();
    }

    public User saveUser(User user){
        user.setEnable(true); //todo false -> when activation by e-mail
        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }
}
