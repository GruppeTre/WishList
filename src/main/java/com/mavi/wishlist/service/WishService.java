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

    //Adds a wish
    @Transactional
    public Wish addWish(Wish wish, Integer userId) {

        //new wishes are never reserved
        wish.setReserved(false);

        //Checks if there's invalid fields
        if (isInvalid(wish)) {
            throw new InvalidFieldsException("Invalid fields in Wish", "fields");
        }

        Wish insertedWish = repository.insertWish(wish);
        repository.insertToWishlistJunction(insertedWish.getId(), userId);
        return insertedWish;
    }

    //Get wishlist by user id
    public List<Wish> getWishlistByUser(int userId) {

        User user = this.userService.getUserById(userId);

        //Checks if user exists
        if(user == null){

            String error = "Well, this is awkward, the Wishlist with ID: " + userId + " doesn't seem to exist :(";

            throw new PageNotFoundException("Page not found", error);

        }
        return repository.getWishlistByUser(userId);

    }

    //Edits a wish
    public Wish editWish(Wish wish) {

        //Checks for invalid fields
        if(isInvalid(wish)){
            throw new InvalidFieldsException("Invalid fields in Wish", "fields");
        }

        return repository.editWish(wish);
    }

    //Toggles reservation for wishes
    public Wish toggleWishReservation(Wish wish, int userId) {

        //check if wish is valid
        if (isInvalid(wish)) {
            throw new InvalidFieldsException("Invalid fields in Wish", "fields");
        }

        //get all reservations by userID
        List<Integer> reservedByUser = this.getReservationListByUserId(userId);

        //if wish is reserved, check if current user owns reservation and unreserve it, otherwise return null
        if (wish.isReserved()) {
           return reservedByUser.contains(wish.getId()) ? this.unreserveWish(wish) : null;
        }

        //if wish is not already reserved, the current user reserves it
        return this.reserveWish(wish, userId);
    }

    //Gets reservation list by user id
    public List<Integer> getReservationListByUserId(int userId) {
        return this.repository.getReservationListByUserId(userId);
    }

    //Deletes wish
    public Wish deleteWish(Wish wishToDelete) {
        int rowsAffected = repository.deleteWish(wishToDelete);
        return wishToDelete;
    }

    //Unreserve a wish through toggle
    private Wish unreserveWish(Wish wish) {

        int rowsAffected = this.repository.deleteWishReservation(wish.getId());

        //Checks if no rows were affected
        if (rowsAffected == 0) {
            return null;
        //Checks if multiple rows were affected
        } else if (rowsAffected > 1) {
            throw new RuntimeException("Multiple lines in reservation junction were affected!");
        }

        wish.setReserved(false);
        return repository.editWish(wish);
    }

    //Reserves a wish through the toggle
    private Wish reserveWish(Wish wish, int userId) {

        int rowsAffected = this.repository.insertToReservationJunction(wish.getId(), userId);

        //Checks if no rows were affected
        if (rowsAffected == 0) {
            return null;
        //Checks if multiple rows were affected
        } else if (rowsAffected > 1) {
            throw new RuntimeException("Multiple lines in reservation junction were affected!");
        }

        wish.setReserved(true);
        return repository.editWish(wish);
    }

    //Invalid field logic
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

    //Check that links start with http(s)
    private boolean linkContainsHttp(Wish wish) {
        String regex = "http[s]?:\\/\\/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(wish.getLink());

        return matcher.find();
    }
}
