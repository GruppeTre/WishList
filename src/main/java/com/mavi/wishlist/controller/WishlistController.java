package com.mavi.wishlist.controller;

import com.mavi.wishlist.controller.utils.SessionUtils;
import com.mavi.wishlist.exceptions.InvalidFieldsException;
import com.mavi.wishlist.model.User;
import com.mavi.wishlist.model.Wish;
import com.mavi.wishlist.repository.WishRepository;
import com.mavi.wishlist.service.UserService;
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

    private final WishService wishService;
    private final UserService userService;

    public WishlistController(WishService wishService, UserService userService) {
        this.wishService = wishService;
        this.userService = userService;
    }


    //this method is served on requests to /wishlist/view & /wishlist/view/{userId}
    @GetMapping(value = {"/view", "/view/{listRef}"})
    public String getWishlist(@PathVariable(required = false) String listRef, Model model, HttpSession session){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
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

        Wish wishToEdit = wishService.getWish(id);

        session.setAttribute("wish", wishToEdit);

        model.addAttribute(wishToEdit);

        return "editWishPage";
    }

    @PostMapping("/add")
    public String addWish(@ModelAttribute Wish newWish, RedirectAttributes redirectAttributes, HttpSession session){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        try{
            Integer userId = ((User) session.getAttribute("user")).getId();
            wishService.addWish(newWish, userId);
        } catch (InvalidFieldsException e) {
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/wishlist/add";
        }

        return "redirect:/wishlist/view";
    }

    @PostMapping("/edit")
    public String editWish(@ModelAttribute Wish editedWish, RedirectAttributes redirectAttributes, HttpSession session){

        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        editedWish.setId(((Wish) session.getAttribute("wish")).getId());
        session.removeAttribute("wish");

        try {
            wishService.editWish(editedWish);
        } catch (InvalidFieldsException e) {
            redirectAttributes.addFlashAttribute("error", true);
            return "redirect:/wishlist/edit/" + editedWish.getId();
        }

        return "redirect:/wishlist/view";
    }

    @PostMapping("/edit/delete")
    public String deleteWish(@ModelAttribute Wish wishToDelete, HttpSession session) {
        if (!SessionUtils.isLoggedIn(session)) {
            return "redirect:/";
        }

        wishToDelete.setId(((Wish) session.getAttribute("wish")).getId());
        wishService.deleteWish(wishToDelete);

        return "redirect:/wishlist/view";
    }

    @PostMapping("/{ownerId}/toggleReserve/{wishId}")
    public String toggleWishReservation(@PathVariable int ownerId, @PathVariable int wishId, HttpSession session) {

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
