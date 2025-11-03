package com.mavi.wishlist.service;

import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.exceptions.PageNotFoundException;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    private final Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @MockitoBean
    private UserRepository repository;

    private User logInUser;
    private User registerUser;
    private User dbUser;

    @BeforeEach
    void setUp() {
        String mail = "test@test.com";
        String password = "t@12345678";
        String firstName = "Erik";
        String lastName = "Eriksen";
        int id = 1;

        logInUser = new User();
        logInUser.setMail(mail);
        logInUser.setPassword(password);

        registerUser = new User();
        registerUser.setMail(mail);
        registerUser.setFirstName(firstName);
        registerUser.setLastName(lastName);
        registerUser.setPassword(password);

        dbUser = new User();
        dbUser.setMail(mail);
        dbUser.setFirstName(firstName);
        dbUser.setLastName(lastName);
        dbUser.setId(id);
        dbUser.setPassword(encoder.encode(password));
    }

    //Log In tests
    @Test
    void logInShouldReturnTrueOnCorrectPassword() {
        when(repository.getUser(logInUser.getMail())).thenReturn(dbUser);

        assertTrue(this.userService.userLogin(logInUser));
    }

    @Test
    void logInShouldReturnFalseOnIncorrectPassword() {
        when(repository.getUser(logInUser.getMail())).thenReturn(dbUser);

        logInUser.setPassword("wrongPass");

        assertFalse(this.userService.userLogin(logInUser));
    }

    @Test
    void logInShouldReturnFalseOnIncorrectMail() {
        when(repository.getUser(logInUser.getMail())).thenReturn(dbUser);

        logInUser.setMail("Wrong@mail.com");

        assertThrows(PageNotFoundException.class, () -> this.userService.userLogin(logInUser));
    }

    //Register tests
    @Test
    void registerUserShouldHashPassword() {
        when(repository.addUser(registerUser)).thenReturn(dbUser);

        String oldPassword = registerUser.getPassword();
        this.userService.registerUser(registerUser);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).addUser(captor.capture());

        assertTrue(encoder.matches(oldPassword, registerUser.getPassword()));
    }

    @Test
    void mailIsTakenShouldRejectTakenMail() {
        when(repository.getUser(registerUser.getMail())).thenReturn(dbUser);

        assertTrue(this.userService.mailIsTaken(registerUser.getMail()));
    }

    @Test
    void registerUserShouldRejectUserWithEmptyMail() {
        when(repository.addUser(registerUser)).thenReturn(dbUser);

        registerUser.setMail("");

        assertThrows(InvalidFieldsException.class, () -> this.userService.registerUser(registerUser));
    }

    @Test
    void registerUserShouldRejectUserWithEmptyFirstName() {
        when(repository.addUser(registerUser)).thenReturn(dbUser);

        registerUser.setFirstName("");

        assertThrows(InvalidFieldsException.class, () -> this.userService.registerUser(registerUser));
    }

    @Test
    void registerUserShouldRejectUserWithEmptyLastName() {
        when(repository.addUser(registerUser)).thenReturn(dbUser);

        registerUser.setLastName("");

        assertThrows(InvalidFieldsException.class, () -> this.userService.registerUser(registerUser));
    }

    @Test
    void registerUserShouldRejectUserWithEmptyPassword() {
        when(repository.addUser(registerUser)).thenReturn(dbUser);

        registerUser.setPassword("");

        assertThrows(InvalidFieldsException.class, () -> this.userService.registerUser(registerUser));
    }

    //update tests

    @Test
    void updateUserShouldReturnNullOnEmptyMail() {
        when(repository.updateUser(registerUser)).thenReturn(dbUser);

        registerUser.setMail(" ");

        assertThrows(InvalidFieldsException.class, () -> this.userService.updateUser(registerUser));
    }

    @Test
    void updateUserShouldReturnNullOnEmptyFirstName() {
        when(repository.updateUser(registerUser)).thenReturn(dbUser);

        registerUser.setFirstName(" ");

        assertThrows(InvalidFieldsException.class, () -> this.userService.updateUser(registerUser));
    }

    @Test
    void updateUserShouldReturnNullOnEmptyLastName() {
        when(repository.updateUser(registerUser)).thenReturn(dbUser);

        registerUser.setLastName(" ");

        assertThrows(InvalidFieldsException.class, () -> this.userService.updateUser(registerUser));
    }

}