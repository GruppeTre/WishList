package com.mavi.wishlist.service;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.repository.UserRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public boolean userLogin(User userToCheck){

        User user = this.getUserByMail(userToCheck.getMail());

        if(user == null) {
            return false;
        }

        return user.getPassword().equals(this.userRepository.getPassword(user.getId()));
    }

    public User getUserByMail(String mail) {

        try{
            // returns the user
            return this.userRepository.getUser(mail);

        } catch (IncorrectResultSizeDataAccessException e) {
            throw new RuntimeException("Multiple users found with email: " + mail);
        }
    }
}
