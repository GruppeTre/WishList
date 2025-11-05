package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getIndex(HttpSession session) {

        if (SessionUtils.isLoggedIn(session)) {
            return "redirect:/wishlist/view";
        }

        return "index";
    }
}
