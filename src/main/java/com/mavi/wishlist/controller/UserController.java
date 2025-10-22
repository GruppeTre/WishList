package com.mavi.wishlist.controller;

import com.mavi.wishlist.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        User userToAdd = new User();
        model.addAttribute("newUser", userToAdd);

        return "registerPage";
    }

    @PostMapping("/register")
    public String addNewUser(@ModelAttribute User newUser) {
        //add user in backend (should salt and hash password before inserting into database

        return "loginPage";
    }
}
