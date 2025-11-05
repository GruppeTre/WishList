package com.mavi.wishlist.repository;

import com.mavi.wishlist.model.Wish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class WishRepositoryTest {

  @Autowired
    private WishRepository repository;

    private List<Wish> wishList;
    private Wish wish;
    private List<Integer> reservationList;
    private Wish getWish;

    @BeforeEach
    void setUp() {
        wishList = repository.getWishlistByUser(1);
        wish = new Wish();
        wish.setName("Fjernbetjening");
        wish.setLink("http://www.femgenerisklink.com");
        repository.insertWish(wish);
        getWish = repository.getWish(5);
        reservationList = repository.getReservationListByUserId(1);
    }

    /*
        ("Uldsokker 5stk", "http://www.etgenerisklink.com", 1),
        ("The Kopper", "http://www.togenerisklink.com", 0),
        ("Fjernsyn", "http://www.tregenerisklink.com", 0),
        ("Tegneblok", "http://www.firegenerisklink.com", 1);

        (user_id, wish_id) VALUES (2, 1), (1, 4);
     */
    @Test
    void shouldGetWish() throws Exception {
        Wish wish = repository.getWish(1);
        assertThat(wish).isNotNull();
        assertThat(wish.getName()).isEqualTo("Uldsokker 5stk");
        assertThat(wish.getLink()).isEqualTo("http://www.etgenerisklink.com");
        assertThat(wish.getId()).isEqualTo(1);
    }

    @Test
    void shouldInsertWish() throws Exception {
       assertThat(getWish.getId()).isEqualTo(5);
       assertThat(getWish.getName()).isEqualTo("Fjernbetjening");
       assertThat(getWish.getLink()).isEqualTo("http://www.femgenerisklink.com");
    }

    @Test
    void shouldInsertIntoWishListJunction() throws Exception {

        assertThat(wishList.size()).isEqualTo(2);
        assertThat(wishList).isNotNull();

        repository.insertToWishlistJunction(getWish.getId(), 1);

        List<Wish> newWishList = repository.getWishlistByUser(1);

        assertThat(newWishList.size()).isEqualTo(3);
        assertThat(newWishList.getFirst().getId()).isEqualTo(1);
        assertThat(newWishList.getFirst().getName()).isEqualTo("Uldsokker 5stk");
        assertThat(newWishList.getFirst().getLink()).isEqualTo("http://www.etgenerisklink.com");
        assertThat(newWishList.get(1).getId()).isEqualTo(2);
        assertThat(newWishList.get(1).getName()).isEqualTo("The Kopper");
        assertThat(newWishList.get(1).getLink()).isEqualTo("http://www.togenerisklink.com");
        assertThat(newWishList.getLast().getId()).isEqualTo(5);
        assertThat(newWishList.getLast().getName()).isEqualTo("Fjernbetjening");
        assertThat(newWishList.getLast().getLink()).isEqualTo("http://www.femgenerisklink.com");
    }

    @Test
    void shouldInsertIntoReservationJunction() throws Exception {
        assertThat(reservationList.size()).isEqualTo(1);
        assertThat(reservationList).isNotNull();

        int getIntWish = repository.getWish(5).getId();

        assertThat(getIntWish).isEqualTo(5);

        repository.insertToReservationJunction(getIntWish, 1);

        List<Integer> newReservationList = repository.getReservationListByUserId(1);

        assertThat(newReservationList.size()).isEqualTo(2);

    }

    @Test
    void shouldGetWishlistByUser() throws Exception {
        assertThat(wishList).isNotNull();
        assertThat(wishList.size()).isEqualTo(2);
        assertThat(wishList.getFirst().getId()).isEqualTo(1);
        assertThat(wishList.getFirst().getName()).isEqualTo("Uldsokker 5stk");
        assertThat(wishList.getFirst().getLink()).isEqualTo("http://www.etgenerisklink.com");
        assertThat(wishList.getLast().getId()).isEqualTo(2);
        assertThat(wishList.getLast().getName()).isEqualTo("The Kopper");
        assertThat(wishList.getLast().getLink()).isEqualTo("http://www.togenerisklink.com");
    }
    @Test
    void shouldGetReservationListByUser() throws Exception {
        assertThat(reservationList).isNotNull();
        assertThat(reservationList.size()).isEqualTo(1);
        assertThat(reservationList.getFirst()).isEqualTo(4);
    }

    @Test
    void shouldEditWish() throws Exception {
        assertThat(getWish.getName()).isEqualTo("Fjernbetjening");
        assertThat(getWish.getLink()).isEqualTo("http://www.femgenerisklink.com");

        getWish.setName("TV");
        getWish.setLink("http://www.seksgenerisklink.com");

        repository.editWish(getWish);
        Wish editedWish = repository.getWish(5);

        assertThat(editedWish.getName()).isEqualTo("TV");
        assertThat(editedWish.getLink()).isEqualTo("http://www.seksgenerisklink.com");
    }

    @Test
    void shouldDeleteWishReservation() throws Exception {
        assertThat(reservationList).isNotNull();
        assertThat(reservationList.size()).isEqualTo(1);

        repository.deleteWishReservation(4);

        List<Integer> newReservationList = repository.getReservationListByUserId(1);

        assertThat(newReservationList.isEmpty());

    }

    @Test
    void shouldDeleteWish() throws Exception {
        assertThat(getWish).isNotNull();

        repository.deleteWish(getWish);

        assertThrows(EmptyResultDataAccessException.class, () -> this.repository.getWish(5));
    }
}