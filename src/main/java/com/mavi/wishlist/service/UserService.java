package com.mavi.wishlist.service;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    public boolean userLogin(User userToCheck){

        User user = this.userRepository.getUser(userToCheck.getMail());



        return user;
    }
}
