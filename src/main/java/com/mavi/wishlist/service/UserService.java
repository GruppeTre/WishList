package com.mavi.wishlist.service;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    public User userLogin(User user){

        this.userRepository.userLogin(user);

        return user;
    }
}
