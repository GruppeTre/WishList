package com.mavi.wishlist.controller;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private UserService service;

    public UserController (UserService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String getIndex(){
        return "index";
    }

    @GetMapping("/login")
    public String getLogin(@ModelAttribute Model model){
        User userLogin = new User();
        model.addAttribute("userLogin", userLogin);

        return "loginPage";
    }

    @PostMapping("/login")
    public String postLogin(RedirectAttributes redirectAttributes, @ModelAttribute User user){

        service.userLogin(user);

        return "redirect:/wishlist";
    }


}
