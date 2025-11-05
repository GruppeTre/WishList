package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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

    /*
    ===================================
    ==          GET MAPPINGS         ==
    ===================================
     */

    //Gets user ID from session
    private Integer getUserIdFromSession(HttpSession session) {
        return ((User)session.getAttribute("user")).getId();
    }

    //Shows the register form
    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        User userToAdd = new User();
        model.addAttribute("newUser", userToAdd);

        return "registerPage";
    }

    //Shows login form
    @GetMapping("/login")
    public String getLogin(HttpSession session, Model model, @RequestParam(required = false, defaultValue = "") String ref){
        User userLogin = new User();
        model.addAttribute("userLogin", userLogin);

        //if ref value is given, temporarily store it in session
        if (!ref.isBlank()) {
            session.setAttribute("ref", ref);
        }

        return "loginPage";
    }

    //Shows the profile page
    @GetMapping("/profile")
    public String showProfile(@RequestParam(required = false, defaultValue = "view") String viewMode, Model model, HttpSession session){

        //If there is no session you get sent back to login
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("viewMode", viewMode);
        return "profilePage";
    }

    /*
    ===================================
    ==         POST MAPPINGS         ==
    ===================================
     */


    //Registers a new user
    @PostMapping("/register")
    public String addNewUser(HttpSession session, Model model, @ModelAttribute User newUser, HttpServletResponse response) {

        //Check to see if all fields are filled correctly
        try{
            newUser = service.registerUser(newUser);
            session.setAttribute("user", newUser);
        } catch (InvalidFieldsException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getIncorrectField());
            model.addAttribute("newUser", newUser);
            return "registerPage";
        }

        return "redirect:/wishlist/view";
    }

    //Log the user in
    @PostMapping("/login")
    public String postLogin(HttpSession session, Model model, @ModelAttribute User user, HttpServletResponse response){

        //if credentials are invalid, return to login-form
        if(!service.userLogin(user)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("userLogin", user);
            return "loginPage";
        }

        //if credentials are valid, update user object with all fields including ID
        user = service.getUserByMail(user.getMail());

        //add user to session
        session.setAttribute("user", user);

        //set up redirect String
        String redirect = "redirect:/wishlist/view";

        //get ref attribute from session (created in getLogin method)
        String ref = (String)session.getAttribute("ref");

        //if attribute contains a ref, concatenate it to redirect String
        if (ref != null) {
            redirect = redirect.concat("/" + ref);
        }

        //remove ref attribute as it is no longer needed
        session.removeAttribute("ref");

        return redirect;
    }

    //Updates a user profile
    @PostMapping("/profile/update")
    public String updateUser(HttpSession session, RedirectAttributes redirectAttributes, @ModelAttribute User updatedUser, HttpServletResponse response){

        //If there is no session, redirect to login form.
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        updatedUser.setId(this.getUserIdFromSession(session));

        //Check that all fields are valid
        try{
            updatedUser = service.updateUser(updatedUser);
            session.setAttribute("user", updatedUser);
        } catch (InvalidFieldsException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("invalidField", e.getIncorrectField());
        }

        return "redirect:/user/profile";
    }

    //Deletes the profile
    @PostMapping("profile/delete")
    public String deleteUser(HttpSession session, @ModelAttribute User userToDelete) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        userToDelete.setId(this.getUserIdFromSession(session));

        this.service.deleteUser(userToDelete);

        session.invalidate();

        return "redirect:/";
    }

    //Logout
    @PostMapping("profile/logout")
    public String logout(HttpSession session){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        session.invalidate();
        return "redirect:/";
    }
}
