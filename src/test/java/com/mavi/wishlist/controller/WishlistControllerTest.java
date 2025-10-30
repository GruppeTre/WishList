package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import com.mavi.wishlist.service.WishService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class WishlistControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WishService service;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MockHttpSession session;

    private Wish newWish;
    private User user;

    @BeforeEach
    void setUp() {
        newWish = new Wish();
        newWish.setId(1);
        newWish.setName("Test");
        newWish.setLink("http://link.test");

        user = new User();
        user.setMail("test@example.com");
        user.setPassword("12345678");
        user.setId(1);
        user.setFirstName("Peter");
        user.setLastName("Petersen");

        session.setAttribute("user", user);
    }

    /*
    ===========================================
    ==               GET TESTS               ==
    ===========================================
     */

    @Test
    void shouldShowNewWishPage() throws Exception {

        //Mocks the static isLoggedIn method in SessionUtils as true.
        try (MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class)) {
            mockedStatic.when(() -> SessionUtils.isLoggedIn(session))
                    .thenReturn(true);

            //Tests that you get the correct status, page and model.
            mvc.perform(get("/wishlist/add").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("wishPage"))
                    .andExpect(model().attribute("wish", instanceOf(Wish.class)));
        }
    }
    /*
    ===========================================
    ==              POST TESTS               ==
    ===========================================
     */

    @Test
    void shouldAddNewWish() throws Exception {

        Mockito.when(session.getAttribute("user")).thenReturn(user);

        //Mocks the static isLoggedIn method in SessionUtils as true.
        try (MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class)) {
            mockedStatic.when(() -> SessionUtils.isLoggedIn(session))
                    .thenReturn(true);

            Integer userId = user.getId();

            Mockito.when(service.isInvalid(newWish)).thenReturn(true);
            Mockito.when(service.addWish(newWish, userId)).thenReturn(newWish);

            mvc.perform(post("/wishlist/add").session(session))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/wishlist/view"))
                    .andExpect(flash().attributeCount(0));
        }
    }
}