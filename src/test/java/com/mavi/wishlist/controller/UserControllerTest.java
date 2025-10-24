package com.mavi.wishlist.controller;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService service;

    private User testUser;
    private User testLogin;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setMail("example@mail.com");
        testUser.setPassword("12345678");
        testUser.setFirstName("Peter");
        testUser.setLastName("Petersen");

        testLogin = new User();
    }

    @Test
    void shouldLogin() throws Exception {
        mvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"))
                .andExpect(model().attribute("userLogin", testLogin));
    }
}