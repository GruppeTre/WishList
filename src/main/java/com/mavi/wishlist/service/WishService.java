package com.mavi.wishlist.service;

import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.repository.WishRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishRepository repository;

    public WishService(WishRepository repository) {
        this.repository = repository;
    }

    public boolean isInvalid(Wish wishToCheck) {

        if(wishToCheck.getName().isBlank()) {
           return true;
        }

        if(wishToCheck.getLink().isBlank()) {
            return true;
        }

        return false;
    }

    @Transactional
    public Wish addWish(Wish wish, Integer userId) {

        if (isInvalid(wish)) {
            return null;
        }

        Wish insertedWish = repository.insertWish(wish);
        repository.insertToJunction(insertedWish.getId(), userId);
        return insertedWish;
    }

}
