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


    @GetMapping("/")
    public String getWishlist(Model model, HttpSession session){
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        User user = (User) session.getAttribute("user");
        List<Wish> wishes = service.showWishlistByUser(user.getId());

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

        return "redirect:/wishlist/";
    }

}
