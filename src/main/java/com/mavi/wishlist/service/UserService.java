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

        if (user == null) {
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

    public boolean mailIsTaken(String mail) {
        return getUserByMail(mail) != null;
    }

    public User registerUser(User user) {

        //trim mail for leading and trailing whitespaces
        user.setMail(user.getMail().trim());

        //check for validity (no empty fields)
        if (!isValidNewUser(user)) {
            return null;
        }

        return userRepository.addUser(user);
    }

    //collection of guard clauses to run before adding new user to database
    private boolean isValidNewUser(User user) {

        int passwordMinLength = 4;

        boolean mailIsBlank = user.getMail().isBlank();
        if (mailIsBlank) {
            return false;
        }

        boolean passwordIsBlank = user.getPassword().isBlank();
        if (passwordIsBlank) {
            return false;
        }

        boolean firstNameIsBlank = user.getFirstName().isBlank();
        if (firstNameIsBlank) {
            return false;
        }

        boolean lastNameIsBlank = user.getLastName().isBlank();
        if (lastNameIsBlank) {
            return false;
        }

        boolean passwordIsTooShort = user.getPassword().length() < passwordMinLength;
        if (passwordIsTooShort) {
            return false;
        }

        boolean mailIsTaken = mailIsTaken(user.getMail());
        if (mailIsTaken) {
            return false;
        }

        return true;
    }
}
