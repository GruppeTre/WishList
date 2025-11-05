package com.mavi.wishlist.controller;

import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.exceptions.PageNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    //Used for unhandled invalid fields.
    @ExceptionHandler(InvalidFieldsException.class)
    public String handleInvalidFields(InvalidFieldsException ex, Model model) {
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    //Used for pages that do not exist
    @ExceptionHandler(PageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePageNotFound(PageNotFoundException ex, Model model) {

        //Display the status, error which tells the origin and message of what went wrong.
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", ex.getError() );
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }
}
