package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    private Integer getUserIdFromSession(HttpSession session) {
        return ((User)session.getAttribute("user")).getId();
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        User userToAdd = new User();
        model.addAttribute("newUser", userToAdd);

        return "registerPage";
    }

    @PostMapping("/register")
    public String addNewUser(HttpSession session, Model model, RedirectAttributes redirectAttributes, @ModelAttribute User newUser) {

        try{
            newUser = service.registerUser(newUser);
            session.setAttribute("user", newUser);
        } catch (InvalidFieldsException e) {
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getIncorrectField());
            model.addAttribute("newUser", newUser);
            return "registerPage";
        }

        return "redirect:/wishlist/view";
    }


    @GetMapping("/login")
    public String getLogin(Model model){
        User userLogin = new User();
        model.addAttribute("userLogin", userLogin);

        return "loginPage";
    }

    @PostMapping("/login")
    public String postLogin(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute User user){

        //if credentials are invalid, return to login-form
        if(!service.userLogin(user)) {
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/user/login";
        }

        //if credentials are valid, update user object with all fields including ID
        user = service.getUserByMail(user.getMail());

        //add user to session
        session.setAttribute("user", user);

        return "redirect:/wishlist/view";
    }

    @GetMapping("/profile")
    public String showProfile(@RequestParam(required = false, defaultValue = "view") String viewMode, Model model, HttpSession session){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("viewMode", viewMode);
        return "profilePage";
    }

    @PostMapping("/profile/update")
    public String updateUser(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute User updatedUser){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        updatedUser.setId(this.getUserIdFromSession(session));

        try{
            updatedUser = service.updateUser(updatedUser);
            session.setAttribute("user", updatedUser);
        } catch (InvalidFieldsException e) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("invalidField", e.getIncorrectField());
        }

        return "redirect:/user/profile";
    }

    @PostMapping("profile/delete")
    public String deleteUser(HttpSession session, @ModelAttribute User userToDelete) {

        userToDelete.setId(this.getUserIdFromSession(session));

        this.service.deleteUser(userToDelete);

        session.invalidate();

        return "redirect:/";
    }

    @PostMapping("profile/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }
}
