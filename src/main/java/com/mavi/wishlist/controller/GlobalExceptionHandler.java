package com.mavi.wishlist.controller;

import com.mavi.wishlist.exceptions.DuplicateUserException;
import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.exceptions.UserNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public String handleDuplicateUser(){
        return "";
    }

    @ExceptionHandler(InvalidFieldsException.class)
    public String handleInvalidFields(){
        return "";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(){
        return "";
    }
}
