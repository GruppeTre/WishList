package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.interfaces.IController;
import com.mavi.wishlist.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class WishlistController implements IController {

    @Override
    public boolean isLoggedIn(HttpSession session){
        return session.getAttribute("user") != null;
    }

    @GetMapping("/wishlist")
    public String getWishlist(Model model, @ModelAttribute User user){
        model.addAttribute("user", user);

        return "wishlist";
    }
}
