package com.mavi.wishlist.controller;

import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

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


    @GetMapping("/login")
    public String getLogin(Model model){
        User userLogin = new User();
        model.addAttribute("userLogin", userLogin);

        return "loginPage";
    }

    @PostMapping("/login")
    public String postLogin(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute User user){

        if(!service.userLogin(user)) {
            redirectAttributes.addFlashAttribute("badCredentials", true);
            return "redirect:/user/login";
        }

        //update user object with all fields including ID
        user = service.getUserByMail(user.getMail());

        session.setAttribute("user", user);

        return "redirect:/wishlist";
    }


}
