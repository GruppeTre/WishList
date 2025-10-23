package com.mavi.wishlist.controller.interfaces;

import jakarta.servlet.http.HttpSession;

public interface IController {
    boolean isLoggedIn(HttpSession session);
}
