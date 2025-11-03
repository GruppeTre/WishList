package com.mavi.wishlist.service;

import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.repository.UserRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Argon2PasswordEncoder encoder;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
        this.encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    public boolean userLogin(User userToCheck){

        User user = this.getUserByMail(userToCheck.getMail());

        if (user == null) {
            return false;
        }

        return encoder.matches(userToCheck.getPassword(), user.getPassword());
    }

    public User getUserByMail(String mail) {

        try{
            // returns the user
            return this.userRepository.getUser(mail);

        } catch (IncorrectResultSizeDataAccessException e) {
            throw new RuntimeException("Multiple users found with email: " + mail);
        }
    }

    public User getUserById(int id){

        try {

            return this.userRepository.getUserById(id);

        } catch (IncorrectResultSizeDataAccessException e) {

            throw new RuntimeException("Multiple users found with id: " + id);

        }

    }

    public boolean mailIsTaken(String mail) {
        return getUserByMail(mail) != null;
    }

    public User registerUser(User user) {

        //trim fields for leading and trailing whitespaces
        trimFields(user);

        //check if mail already exists
        if (mailIsTaken(user.getMail())) {
            throw new InvalidFieldsException("User with that mail already exists", "mail");
        }

        //check for validity (no empty fields)
        if (userHasInvalidFields(user)) {
            throw new InvalidFieldsException("Invalid fields in User", "names");
        }

        String rawPassword = user.getPassword();

        user.setPassword(encoder.encode(rawPassword));

        return userRepository.addUser(user);
    }

    public User updateUser(User user){

        //trim fields for leading and trailing whitespaces
        trimFields(user);

        //check for validity (no empty fields)
        if (userHasInvalidFields(user)) {
            throw new InvalidFieldsException("Invalid fields in User", "name");
        }
        return userRepository.updateUser(user);
    }

    public User deleteUser(User userToDelete) {
        return this.userRepository.deleteUser(userToDelete);
    }

    //collection of guard clauses to run before adding new user to database
    private boolean userHasInvalidFields(User user) {

        int passwordMinLength = 8;

        boolean mailIsBlank = user.getMail().isBlank();
        if (mailIsBlank) {
            return true;
        }

        boolean passwordIsBlank = user.getPassword().isBlank();
        if (passwordIsBlank) {
            return true;
        }

        boolean firstNameIsBlank = user.getFirstName().isBlank();
        if (firstNameIsBlank) {
            return true;
        }

        boolean lastNameIsBlank = user.getLastName().isBlank();
        if (lastNameIsBlank) {
            return true;
        }

        boolean passwordIsTooShort = user.getPassword().length() < passwordMinLength;
        if (passwordIsTooShort) {
            return true;
        }

        return false;
    }

    private void trimFields(User user) {

        user.setMail(user.getMail().trim());
        user.setFirstName(user.getFirstName().trim());
        user.setLastName(user.getLastName().trim());
    }
}
