package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.interfaces.IController;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController implements IController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Override
    public boolean isLoggedIn(HttpSession session){
        return session.getAttribute("user") != null;
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
    public String addNewUser(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute User newUser) {

        //validate if user with given email already exists
        if (service.mailIsTaken(newUser.getMail())) {
            redirectAttributes.addFlashAttribute("showErrorMessage", true);
            redirectAttributes.addFlashAttribute("errorMessageText", "That email is already in use");
            System.out.println("controller: failed mailIsTaken check");
            return "redirect:/user/register";
        }

        System.out.println("controller: passed mailIsTaken check");

        //If user was not added successfully, return value is null, and we redirect to register page with an error message
        if ((newUser = service.registerUser(newUser)) == null) {
            redirectAttributes.addFlashAttribute("showErrorMessage", true);
            redirectAttributes.addFlashAttribute("errorMessageText", "Failed to register user, please try again");
            System.out.println("controller: registerUser call returned null!");
            return "redirect:/user/register";
        }

        System.out.println("controller: passed all checks, redirecting to /wishlist with session attribute");

        session.setAttribute("user", newUser);

        return "redirect:/wishlist";
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

    @GetMapping("/profile")
    public String showProfile(@RequestParam(required = false, defaultValue = "view") String viewMode, Model model, HttpSession session){
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("viewMode", viewMode);
        return "profilePage";
    }

    @PostMapping("/profile/update")
    public String updateUser(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute User updatedUser){

        updatedUser.setId(this.getUserIdFromSession(session));

        if ((updatedUser = service.updateUser(updatedUser)) == null) {
            redirectAttributes.addFlashAttribute("showErrorMessage", true);
            redirectAttributes.addFlashAttribute("errorMessageText", "Failed to update user, please try again");
            System.out.println("controller: updateUser call returned null!");
        } else {
            session.setAttribute("user", updatedUser);
        }

        return "redirect:/user/profile";
    }

    @PostMapping("profile/delete")
    public String deleteUser(HttpSession session, @ModelAttribute User userToDelete) {

        userToDelete.setId(this.getUserIdFromSession(session));

        this.service.deleteUser(userToDelete);

        return "redirect:/";
    }

}
