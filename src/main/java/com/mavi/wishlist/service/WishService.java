package com.mavi.wishlist.service;

import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.exceptions.PageNotFoundException;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WishService {

    private final WishRepository repository;
    private final UserService userService;

    public WishService(WishRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public Wish getWish(Integer wishId) {
        return repository.getWish(wishId);
    }

    @Transactional
    public Wish addWish(Wish wish, Integer userId) {

        trimFields(wish);

        if (!linkContainsHttp(wish)) {
            throw new InvalidFieldsException("Invalid fields in Wish", "link");
        }

        if (isInvalid(wish)) {
            throw new InvalidFieldsException("Invalid fields in Wish", "description");
        }

        Wish insertedWish = repository.insertWish(wish);
        repository.insertToWishlistJunction(insertedWish.getId(), userId);
        return insertedWish;
    }

    public List<Wish> getWishlistByUser(int userId) {

        User user = this.userService.getUserById(userId);

        if(user == null){

            String error = "Well, this is awkward, the Wishlist with ID: " + userId + " doesn't seem to exist :(";

            throw new PageNotFoundException("Page not found", error);

        }
        return repository.getWishlistByUser(userId);

    }

    public Wish editWish(Wish wish) {

        trimFields(wish);

        if (!linkContainsHttp(wish)) {
            throw new InvalidFieldsException("Invalid fields in Wish", "link");
        }

        if(isInvalid(wish)){
            throw new InvalidFieldsException("Invalid fields in Wish", "description");
        }

        return repository.editWish(wish);
    }

    public List<Integer> getReservationListByUserId(int userId) {
        return this.repository.getReservationListByUserId(userId);
    }

    public List<Integer> getReservedWishes(int userId) {
        return this.repository.getReservedWishes(userId);
    }

    public Wish deleteWish(Wish wishToDelete) {
        int rowsAffected = repository.deleteWish(wishToDelete);
        return wishToDelete;
    }

    public Wish unreserveWish(Wish wish) {

        int rowsAffected = this.repository.deleteWishReservation(wish.getId());

        if (rowsAffected == 0) {
            return null;
        } else if (rowsAffected > 1) {
            throw new RuntimeException("Multiple lines in reservation junction were affected!");
        }
        return wish;
    }

    public Wish reserveWish(Wish wish, int userId) {

        int rowsAffected = this.repository.insertToReservationJunction(wish.getId(), userId);

        if (rowsAffected == 0) {
            return null;
        } else if (rowsAffected > 1) {
            throw new RuntimeException("Multiple lines in reservation junction were affected!");
        }
        return wish;
    }

    public boolean isReserved(Wish wish) {
        return this.repository.isReserved(wish);
    }

    private boolean isInvalid(Wish wishToCheck) {

        if(wishToCheck.getName().isBlank()) {
            return true;
        }

        if(wishToCheck.getLink().isBlank()) {
            return true;
        }

        if (!linkContainsHttp(wishToCheck)) {
            return true;
        }

        return false;
    }

    private boolean linkContainsHttp(Wish wish) {
        String regex = "http[s]?:\\/\\/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(wish.getLink());

        return matcher.find();
    }

    private void trimFields(Wish wish) {
        wish.setName(wish.getName().trim());
        wish.setLink(wish.getLink().trim());
    }
}
