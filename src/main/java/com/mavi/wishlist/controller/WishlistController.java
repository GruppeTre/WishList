package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.model.Wish;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class WishlistController {

    @GetMapping("/wishlist")
    public String getWishlist(Model model, HttpSession session, @ModelAttribute User user){
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        model.addAttribute("user", session.getAttribute("user"));

        return "wishlist";
    }

    @GetMapping("/addWish")
    public String addWish(Model model, HttpSession session){
        if(!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        Wish newWish = new Wish();
        model.addAttribute("newWish", newWish);
        model.addAttribute("user", session.getAttribute("user"));

        return "newWishPage";
    }
}
