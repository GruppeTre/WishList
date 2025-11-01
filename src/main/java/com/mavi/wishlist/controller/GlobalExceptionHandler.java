package com.mavi.wishlist.controller;

import com.mavi.wishlist.exceptions.DuplicateUserException;
import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.http.HttpRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public String handleDuplicateUser(DuplicateUserException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("showErrorModal", true);
        redirectAttributes.addFlashAttribute("status", HttpStatus.CONFLICT.value());
        redirectAttributes.addFlashAttribute("error", "Invalid input");
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/user/register";
    }

    @ExceptionHandler(InvalidFieldsException.class)
    public String handleInvalidFields(InvalidFieldsException ex, Model model) {
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", "Invalid input");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(){
        return "";
    }
}
