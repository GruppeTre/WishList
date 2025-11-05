package com.mavi.wishlist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    //Index controller, does not fit in User or Wishlist
    @GetMapping("/")
    public String getIndex(){
        return "index";
    }
}
