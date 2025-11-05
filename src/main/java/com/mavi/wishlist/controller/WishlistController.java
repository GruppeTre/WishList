package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.service.UserService;
import com.mavi.wishlist.service.WishService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishService wishService;
    private final UserService userService;

    public WishlistController(WishService wishService, UserService userService) {
        this.wishService = wishService;
        this.userService = userService;
    }

    /*
    ==================================
    ==         GET MAPPINGS         ==
    ==================================
     */

    //this method is served on requests to /wishlist/view & /wishlist/view/{userId}
    @GetMapping(value = {"/view", "/view/{listRef}"})
    public String getWishlist(@PathVariable(required = false) String listRef, Model model, HttpSession session){

        //if user is not logged in, send them to login page along with a reference to the endpoint they were trying to visit
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/user/login?ref=" + listRef;
        }

        int sessionId = ((User)session.getAttribute("user")).getId();

        //get refString from ownerID
        Integer ownerId = userService.getOwnerIdFromRefString(listRef);

        //redirect to endpoint with no pathVariable in URL if user goes to /view/{id} endpoint of wishlist they own
        if (ownerId != null && ownerId == sessionId) {
            return "redirect:/wishlist/view";
        }

        //set userId to session owner's ID if no path variable is passed
        ownerId = (ownerId == null) ? sessionId : ownerId;

        List<Wish> wishes = wishService.getWishlistByUser(ownerId);
        List<Integer> reservationsByUser = wishService.getReservationListByUserId(sessionId);
        listRef = userService.getRefStringFromId(ownerId);

        List<Integer> reservedWishes = wishService.getReservedWishes(ownerId);

        model.addAttribute("listRef", listRef);
        model.addAttribute("ownerName", userService.getUserById(ownerId).getFirstName());
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("wishes", wishes);
        model.addAttribute("reservationsByUser", reservationsByUser);
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("reservedWishes", reservedWishes);

        return "wishlist";
    }

    //Adds a new wish to a wishlist
    @GetMapping("/add")
    public String getWishPage(Model model, HttpSession session) {

        //Check if a  session is set
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        Wish newWish = new Wish();
        model.addAttribute("wish", newWish);

        return "wishPage";
    }

    //Edit form for wishes
    @GetMapping("/edit/{id}")
    public String getEditWishPage(@PathVariable int id, Model model, HttpSession session) {

        //Checks if a session is set
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        Wish wishToEdit = wishService.getWish(id);

        session.setAttribute("wish", wishToEdit);

        model.addAttribute(wishToEdit);

        return "editWishPage";
    }

    /*
    ==================================
    ==        POST MAPPINGS         ==
    ==================================
    */

    //Adds a new wish
    @PostMapping("/add")
    public String addWish(@ModelAttribute Wish newWish, Model model, HttpSession session, HttpServletResponse response){

        //Checks if a session is set
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //Checks if there are any invalid fields
        try{
            Integer userId = ((User) session.getAttribute("user")).getId();
            wishService.addWish(newWish, userId);
        } catch (InvalidFieldsException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("error", true);
            model.addAttribute("invalidField", e.getIncorrectField());
            model.addAttribute("wish", newWish);
            return "wishPage";
        }

        return "redirect:/wishlist/view";
    }

    //Edits a wish
    @PostMapping("/edit")
    public String editWish(@ModelAttribute Wish editedWish, RedirectAttributes redirectAttributes, HttpSession session, HttpServletResponse response){

        //Check if a session is set
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        editedWish.setId(((Wish) session.getAttribute("wish")).getId());
        session.removeAttribute("wish");

        //Check for invalid fields
        try {
            wishService.editWish(editedWish);
        } catch (InvalidFieldsException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("invalidField", e.getIncorrectField());
            return "redirect:/wishlist/edit/" + editedWish.getId();
        }

        return "redirect:/wishlist/view";
    }

    //Deleles a wish
    @PostMapping("/edit/delete")
    public String deleteWish(@ModelAttribute Wish wishToDelete, HttpSession session) {

        //Check if a session is set
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        wishToDelete.setId(((Wish) session.getAttribute("wish")).getId());
        wishService.deleteWish(wishToDelete);

        return "redirect:/wishlist/view";
    }

    //Toggles for reservations
    @PostMapping("/{ownerId}/toggleReserve/{wishId}")
    public String toggleWishReservation(@PathVariable int ownerId, @PathVariable int wishId, HttpSession session) {

        //Check if a session is set
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        //get wish object from pathVariable wishId
        Wish wishToReserve =  wishService.getWish(wishId);

        //get id of current user
        int userId = ((User) session.getAttribute("user")).getId();

        if(wishService.isReserved(wishToReserve)) {
            wishService.unreserveWish(wishToReserve);
        }
        else {
            wishService.reserveWish(wishToReserve, userId);
        }

        String redirectRef = this.userService.getRefStringFromId(ownerId);

        return "redirect:/wishlist/view/" + redirectRef;
    }
}
