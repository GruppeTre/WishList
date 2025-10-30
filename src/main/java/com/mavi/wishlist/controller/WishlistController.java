package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.service.WishService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishService service;

    public WishlistController(WishService service) {
        this.service = service;
    }


    //this method is served on requests to /wishlist/view & /wishlist/view/{userId}
    @GetMapping(value = {"/view", "/view/{userId}"})
    public String getWishlist(@PathVariable(required = false) Integer userId, Model model, HttpSession session){
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //set userId to session owner's ID if no path variable is passed
        userId = (userId == null) ? ((User)session.getAttribute("user")).getId() : userId;

        System.out.println("user ID:" + userId);
        //Refactored to use pathvariable instead of session
        List<Wish> wishes = service.showWishlistByUser(userId);

        model.addAttribute("wishListId", userId);
        model.addAttribute("wishes", wishes);
        model.addAttribute("user", session.getAttribute("user"));

        return "wishlist";
    }

    @GetMapping("/add")
    public String getWishPage(Model model, HttpSession session) {

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        Wish newWish = new Wish();
        model.addAttribute("wish", newWish);

        return "wishPage";
    }

    @GetMapping("/edit/{id}")
    public String getEditWishPage(@PathVariable int id, Model model, HttpSession session) {
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        Wish wishToEdit = service.getWish(id);

        session.setAttribute("wish", wishToEdit);

        model.addAttribute(wishToEdit);

        return "editWishPage";
    }

    @PostMapping("/add")
    public String addWish(@ModelAttribute Wish newWish, RedirectAttributes redirectAttributes, HttpSession session){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        if (service.isInvalid(newWish)) {
            redirectAttributes.addFlashAttribute("showErrorMessage", true);
            redirectAttributes.addFlashAttribute("errorMessageText", "Fields cannot be blank");
            return "redirect:/wishlist/add";
        }

        Integer userId = ((User) session.getAttribute("user")).getId();

        service.addWish(newWish, userId);
        redirectAttributes.addAttribute("id", ((User) session.getAttribute("user")).getId());

        return "redirect:/wishlist/{id}";
    }

    @PostMapping("/edit")
    public String editWish(@ModelAttribute Wish editWish, RedirectAttributes redirectAttributes, HttpSession session){
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        editWish.setId(((Wish) session.getAttribute("wish")).getId());
        session.removeAttribute("wish");

        if (service.isInvalid(editWish)) {
            redirectAttributes.addFlashAttribute("showErrorMessage", true);
            redirectAttributes.addFlashAttribute("errorMessageText", "Fields cannot be blank");
            return "redirect:/wishlist/edit/" + editWish.getId();
        }

        service.editWish(editWish);
        redirectAttributes.addAttribute("id", ((User) session.getAttribute("user")).getId());

        return "redirect:/wishlist/{id}";
    }

    @PostMapping("/edit/delete")
    public String deleteWish(@ModelAttribute Wish wishToDelete, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        wishToDelete.setId(((Wish) session.getAttribute("wish")).getId());
        service.deleteWish(wishToDelete);
        redirectAttributes.addAttribute("id", ((User) session.getAttribute("user")).getId());

        return "redirect:/wishlist/{id}";
    }

}
