package com.mavi.wishlist.service;

import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WishServiceTest {

    @Autowired
    private WishService service;

    @MockitoBean
    private WishRepository repository;

    private Wish wish;
    private Wish wishWithId;
    private int userId;

    @BeforeEach
    void setUp() {
        String description = "Test ønske";
        String link = "testlink.com";
        int id = 1;

        userId = 1;

        wish = new Wish();
        wishWithId = new Wish();

        wish.setName(description);
        wish.setLink(link);

        wishWithId.setName(description);
        wishWithId.setLink(link);
        wishWithId.setId(id);
    }

    @Test
    void addWishShouldCallRepositoryAndReturnWishWithIdOnSuccess() {
        when(repository.insertWish(wish)).thenReturn(wishWithId);
        when(repository.insertToJunction(wishWithId.getId(), userId)).thenReturn(1);

        assertEquals(this.service.addWish(wish, userId), wishWithId);
    }

    @Test
    void addWishShouldRejectWishWithEmptyDescription() {

        wish.setName(" ");

        assertNull(this.service.addWish(wish, userId));
    }

    @Test
    void addWishShouldRejectWishWithEmptyLink() {

        wish.setLink(" ");

        assertNull(this.service.addWish(wish, userId));
    }
}