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

    public boolean isInvalid(Wish wishToCheck) {

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

    public Wish getWish(Integer wishId){
        return repository.getWish(wishId);
    }
  
    public boolean linkContainsHttp(Wish wish) {
        String regex = "http[s]?:\\/\\/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(wish.getLink());

        return matcher.find();
    }

    @Transactional
    public Wish addWish(Wish wish, Integer userId) {

        //new wishes are never reserved
        wish.setReserved(false);

        if (isInvalid(wish)) {
            throw new InvalidFieldsException("Invalid fields in Wish");
        }

        Wish insertedWish = repository.insertWish(wish);
        repository.insertToWishlistJunction(insertedWish.getId(), userId);
        return insertedWish;
    }

    public List<Wish> showWishlistByUser(int userId) {

        User user = this.userService.getUserById(userId);

        if(user == null){

            String error = "Well, this is awkward, the Wishlist with ID: " + userId + " doesn't seem to exist :(";

            throw new PageNotFoundException("Page not found", error);

        }
        return repository.getWishlistByUser(userId);

    }

    public Wish editWish(Wish wish){
        if(isInvalid(wish)){
            throw new InvalidFieldsException("Invalid fields in Wish");
        }

        return repository.editWish(wish);
    }

    public Wish toggleWishReservation(Wish wish, int userId) {

        //check if wish is valid
        if (isInvalid(wish)) {
            throw new InvalidFieldsException("Invalid fields in Wish");
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

    public List<Integer> getReservationListByUserId(int userId) {
        return this.repository.getReservationListByUserId(userId);
    }

    public Wish deleteWish(Wish wishToDelete) {
        int rowsAffected = repository.deleteWish(wishToDelete);
        return wishToDelete;
    }

    private Wish unreserveWish(Wish wish) {

        int rowsAffected = this.repository.deleteWishReservation(wish.getId());

        if (rowsAffected == 0) {
            return null;
        } else if (rowsAffected > 1) {
            throw new RuntimeException("Multiple lines in reservation junction were affected!");
        }

        wish.setReserved(false);
        return repository.editWish(wish);
    }

    private Wish reserveWish(Wish wish, int userId) {

        int rowsAffected = this.repository.insertToReservationJunction(wish.getId(), userId);

        if (rowsAffected == 0) {
            return null;
        } else if (rowsAffected > 1) {
            throw new RuntimeException("Multiple lines in reservation junction were affected!");
        }

        wish.setReserved(true);
        return repository.editWish(wish);
    }
}
