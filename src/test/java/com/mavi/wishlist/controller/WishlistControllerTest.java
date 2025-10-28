package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import com.mavi.wishlist.service.WishService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        newWish.setLink("link.test");

        user = new User();
        user.setMail("test@example.com");
        user.setPassword("12345678");
        user.setId(1);
        user.setFirstName("Peter");
        user.setFirstName("Petersen");

        session.setAttribute("user", user);
    }

    @Test
    void shouldShowNewWishPage() throws Exception {
        try (MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class)) {
            mockedStatic.when(() -> SessionUtils.isLoggedIn(session))
                    .thenReturn(true);


            mvc.perform(get("/wishlist/add").session(session))
                    .andExpect(status().isOk())
                    .andExpect(view().name("wishPage"))
                    .andExpect(model().attribute("wish", instanceOf(Wish.class)));
        }
    }
}