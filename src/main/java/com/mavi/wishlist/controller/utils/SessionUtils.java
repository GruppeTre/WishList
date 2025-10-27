package com.mavi.wishlist.controller.utils;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}
