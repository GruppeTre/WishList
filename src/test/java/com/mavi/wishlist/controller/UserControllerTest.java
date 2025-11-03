package com.mavi.wishlist.controller;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import com.mavi.wishlist.service.WishService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WishService wishService;

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

    /*
    ===========================================
    ==               GET TESTS               ==
    ===========================================
     */
    @Test
    void shouldShowLoginPage() throws Exception {
        mvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"))
                .andExpect(model().attribute("userLogin", testLogin));
    }

    /*
    ===========================================
    ==              POST TESTS               ==
    ===========================================
     */

    @Test
    void shouldLoginUser() throws Exception {

        //Force mockito to return true for any User.class object as it does not have access to the Database
        Mockito.when(service.userLogin(Mockito.any(User.class))).thenReturn(true);

        //Returns the fully populated testUser when the controller calls getUserByMail() - necessary for the session test
        Mockito.when(service.getUserByMail(testUser.getMail())).thenReturn(testUser);

        mvc.perform(post("/user/login")
                //Sets the parameters of mail and passwordHash
                .param("mail", testUser.getMail())
                .param("passwordHash", testUser.getPassword()))
                //Checks that it redirects
                .andExpect(status().is3xxRedirection())
                //Checks that the redirected url is /wishlist
                .andExpect(redirectedUrl("/wishlist/view"))
                //Checks that no attributes are left behind
                .andExpect(flash().attributeCount(0))
                //Checks that it sets a new session
                .andExpect(request().sessionAttribute("user", testUser));
    }

    @Test
    void shouldRedirectUserToLoginPageOnWrongCredentials() throws Exception {
        String wrongPassword = "87654321";
        String wrongMail = "mail@example.com";
        //Force mockito to return true for any User.class object as it does not have access to the Database
        Mockito.when(service.userLogin(Mockito.any(User.class))).thenReturn(false);

        mvc.perform(post("/user/login")
                        //Sets the parameters of mail and passwordHash
                        .param("mail", wrongMail)
                        .param("passwordHash", wrongPassword))
                //Checks that it redirects
                .andExpect(status().is3xxRedirection())
                //Checks that the redirected url is /user/login
                .andExpect(redirectedUrl("/user/login"))
                //Checks that one attributes are left behind
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("error"));
    }
}